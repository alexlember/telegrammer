package ru.lember.telegrammer.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.lember.telegrammer.analyzer.AnalyzedResult;
import ru.lember.telegrammer.analyzer.CommandAnalyzer;
import ru.lember.telegrammer.analyzer.ImmutableAsyncCmd;
import ru.lember.telegrammer.configs.BotProperties;
import ru.lember.telegrammer.configs.CmdProperties;
import ru.lember.telegrammer.configs.UserProperties;
import ru.lember.telegrammer.configs.reply.ReplyDto;
import ru.lember.telegrammer.dto.in.RequestFromRemote;
import ru.lember.telegrammer.outbound.Interconnector;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class BotHandlerImpl extends TelegramLongPollingBot implements BotHandler {

    /**
     * Special mark which is telling to supply user and chat id to Telegram chat.
     */
    private static final String MARK_TELL_WHO_YOU_ARE = "tellWhoYouAre";
    private static final String WHO_YOU_ARE_TEMPLATE = "I don't know you. Tell administrator to add your data to the system. User id: %s, chat id: %s";

    private BotProperties properties;
    private UserProperties userProperties;
    private CommandAnalyzer analyzer;
    private Interconnector interconnector;
    private CmdProperties cmdProperties;

    private AtomicLong requestsCounter = new AtomicLong(0);

    private ExecutorService commandExecutor;
    private BlockingQueue<Update> commandQueue;

    private final ExecutorService queueWatcher = Executors.newSingleThreadExecutor();

    public BotHandlerImpl(DefaultBotOptions botOptions,
                          BotProperties botProperties,
                          CommandAnalyzer analyzer,
                          Interconnector interconnector,
                          UserProperties userProperties,
                          CmdProperties cmdProperties) {
        super(botOptions);
        this.properties = botProperties;
        this.analyzer = analyzer;
        this.interconnector = interconnector;
        this.userProperties = userProperties;
        this.cmdProperties = cmdProperties;
        this.commandExecutor = Executors.newFixedThreadPool(cmdProperties.getThreadsNumber());
        this.commandQueue = new LinkedBlockingDeque<>(cmdProperties.getQueueCapacity());
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
        subscribeOnRequests();

        queueWatcher.submit(() -> {
            while (true) {
                Update update;
                try {
                    update = commandQueue.take();
                } catch (InterruptedException e) {
                    log.error("commandQueue interrupted: ", e);
                    throw new RuntimeException(e);
                }

                log.debug("Submitting new message with update id: {} to commandExecutor", update.getUpdateId());
                commandExecutor.submit(() -> handleUpdate(update));

            }
        });
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("Update with id: {}. Putting to the queue", update.getUpdateId());

        try {
            commandQueue.put(update);
        } catch (InterruptedException e) {
            log.error("commandQueue put error: {}", e.getMessage());
        }

    }

    private void subscribeOnRequests() {
        interconnector.processor()
                .filter(updateEvent -> updateEvent.correlationId() == null)
                .subscribe(updateEvent -> {
                    log.info("Request from RPI received. cmd: {}. Sending response to chats: {}",
                            updateEvent.cmd(), userProperties.getIncomingRequestChatIdReceiver());

                    String responseMessage = updateEvent.message();
                    if (!StringUtils.isEmpty(responseMessage)) {

                        for (String chatId : userProperties.getIncomingRequestChatIdReceiver()) {
                            SendMessage replyMessage = new SendMessage();
                            replyMessage.setText(responseMessage);
                            replyMessage.setChatId(chatId);
                            tryExecute(replyMessage);
                        }

                    }


                }, t -> log.error("Error during executing incoming request Error: {}", t.getMessage()));
    }

    private void handleUpdate(Update update) {

        log.info("Start handling update with id: {}", update.getUpdateId());

        String cmd;
        Long chatId;
        Integer messageId;
        User user;

        //Instant messageInstant;

        if (update.hasMessage()) {
            Message message = update.getMessage();
            user = message.getFrom();

//            messageInstant = Instant.ofEpochMilli(message.getDate() * 1000);
//
//            log.info("Message date: {}", message.getDate());

            cmd = message.getText();
            chatId = message.getChatId();
            messageId = message.getMessageId();
        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            cmd = callbackQuery.getData();
            user = callbackQuery.getFrom();

            Message message = callbackQuery.getMessage();

//            log.info("Message date: {}", message.getDate());
//
//            messageInstant = Instant.ofEpochMilli(message.getDate() * 1000);

            chatId = message.getChatId();
            messageId = message.getMessageId();

        } else {
            log.warn("This type of update is not supported right now. Update: {}", update);
            return;
        }

        // todo this funtionality is to prevent from massive messages handling after some shutdown.
//        Instant now = Instant.now();
//        log.info("Now instant: {}", now); // todo move to debug
//        log.info("Message instant: {}", messageInstant); // todo move to debug
//
//        Instant nowMinusDelta = now.minus(Duration.ofMillis(cmdProperties.getStoreCommandsSeconds() * 1000));
//        log.info("Now - delta instant: {}", nowMinusDelta); // todo move to debug
//
//        if (nowMinusDelta.isAfter(messageInstant)) {
//            log.warn("Message update with id: {} is out of date. It won't be handled", update.getUpdateId());
//            return;
//        }

        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1); // get rid of '/' at the beginning.
        }

        handleUpdate(cmd, chatId, messageId, user);
    }

    private void tryExecute(@NotNull SendMessage message) {
        try {
            log.info("Trying sending message: {}", message);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("On update received failed. error: {}", e.getMessage());
        }
    }

    public String getBotUsername() {
        return properties.getName();
    }

    public String getBotToken() {
        return properties.getToken();
    }

    @Override
    public void handleUpdate(String cmd, Long chatId, Integer messageId, User user) {
        log.info("Cmd: '{}' from chat id: '{}' with message id: {} received from: {}", cmd, chatId, messageId, user);

        Integer userId = user.getId();

        if (!userProperties.getAllowedUsers().contains(String.valueOf(userId))) {

            log.info("Cmd: '{}' from chat id: {} received from unknown userId: {} name: {} lastName: {}", userId, user.getFirstName(), user.getLastName());

            ReplyDto replyForUnknownUser = userProperties.getReplyForUnknownUser();
            if (replyForUnknownUser != null) {

                if (MARK_TELL_WHO_YOU_ARE.equals(replyForUnknownUser.getText())) {
                    replyForUnknownUser.setText(String.format(WHO_YOU_ARE_TEMPLATE, userId, chatId));
                }

                log.info("Sending response: {} on cmd: '{}' from chat id: {} received from unknown",
                        replyForUnknownUser, cmd, chatId);

                tryExecute(ReplyFactory.of(chatId, replyForUnknownUser));
            }

            return;
        }


        int separatorIndex = cmd.indexOf(cmdProperties.getCmdNameSeparator());
        final String cmdName = separatorIndex == -1 ? cmd : cmd.substring(0, separatorIndex);
        final String cmdBody = separatorIndex == -1 ? "" : cmd.substring(separatorIndex + 1);


        AnalyzedResult result = analyzer.analyze(cmdName);

        if (result.isNeedToIgnore()) {
            log.info("Cmd: '{}' with message id: {} received from: {} is need to be ignored", cmd, messageId, user);
        } else if (result.isUnknown()) {
            log.info("Cmd: '{}' with message id: {} received from: {} is unknown", cmd, messageId, user);

            if (result.getUnknownReply() != null) {
                tryExecute(ReplyFactory.of(chatId, result.getUnknownReply()));
            }
        } else if (result.isNeedSyncReply()) {
            log.info("Cmd: '{}' with message id: {} received from: {} is need to be replied in sync manner", cmd, messageId, user);

            tryExecute(ReplyFactory.of(chatId, Objects.requireNonNull(result.getSyncReplyDto())));

        } else {

            ImmutableAsyncCmd asyncCmd = result.getAsyncCmd();

            if (asyncCmd == null) {
                log.warn("Illegal state: asyncCmd is null");
                return;
            }

            if (asyncCmd.getBeforeActionReply() != null) {
                log.error("Sending sync response for async cmd: '{}' with id: {}", cmd, messageId);

                tryExecute(ReplyFactory.of(chatId, asyncCmd.getBeforeActionReply()));
            }


            Long id = requestsCounter.incrementAndGet();
            RequestFromRemote request = new RequestFromRemote(id, cmdName, cmdBody, asyncCmd.getTimeoutMs());

            interconnector.sendRequest(request);

            interconnector.processor()
                    .filter(response -> cmdName.equals(response.cmd()) && id.equals(response.correlationId()))
                    .timeout(Duration.ofMillis(asyncCmd.getTimeoutMs()))
                    .take(1)
                    .subscribe(response -> {
                        log.info("Response on cmd: '{}' with id: {}. Message: {}. Sending response to messageId: {}", cmd, id, response.message(), messageId);

                        ReplyDto responseMessage = Optional.ofNullable(ReplyDto.ofText(response.message())) // todo support of other replies from RPI node.
                                .orElse(asyncCmd.getReply());

                        tryExecute(ReplyFactory.of(chatId, responseMessage));

                    }, t -> {
                        log.error("Error during executing request cmd: '{}' with id: {}. Error: {}", cmd, id, t.getMessage());

                        if (t instanceof TimeoutException) {


                            ReplyDto timeoutReplyMessage = asyncCmd.getTimeoutError();

                            if (timeoutReplyMessage != null) {
                                log.info("Sending response for timeout exception: {} for cmd: '{}' with messageId: {}", timeoutReplyMessage, cmd, messageId);

                                tryExecute(ReplyFactory.of(chatId, timeoutReplyMessage));
                            }
                        }
                    });
        }
    }
}
