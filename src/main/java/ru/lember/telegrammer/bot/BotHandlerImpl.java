package ru.lember.telegrammer.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.lember.telegrammer.analyzer.AnalyzedResult;
import ru.lember.telegrammer.analyzer.CommandAnalyzer;
import ru.lember.telegrammer.analyzer.ImmutableAsyncCmd;
import ru.lember.telegrammer.configs.BotProperties;
import ru.lember.telegrammer.configs.UserProperties;
import ru.lember.telegrammer.configs.reply.ReplyDto;
import ru.lember.telegrammer.dto.in.RequestFromRemote;
import ru.lember.telegrammer.outbound.Interconnector;
import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class BotHandlerImpl extends TelegramLongPollingBot implements BotHandler {

    private BotProperties properties;
    private UserProperties userProperties;
    private CommandAnalyzer analyzer;
    private Interconnector interconnector;

    private AtomicLong requestsCounter = new AtomicLong(0);

    public BotHandlerImpl(DefaultBotOptions botOptions,
                          BotProperties botProperties,
                          CommandAnalyzer analyzer,
                          Interconnector interconnector,
                          UserProperties userProperties) {
        super(botOptions);
        this.properties = botProperties;
        this.analyzer = analyzer;
        this.interconnector = interconnector;
        this.userProperties = userProperties;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
        subscribeOnRequests();
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

    @Override
    public void onUpdateReceived(Update update) {

        if (update == null) {
            log.warn("Null update received");
            return;
        }

        Message message = update.getMessage();
        String cmd = message.getText();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        User user = message.getFrom();

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
        log.info("Cmd: {} from chat id: {} with message id: {} received from: {}", cmd, chatId, messageId, user);

        Integer userId = user.getId();

        if (!userProperties.getAllowedUsers().contains(String.valueOf(userId))) {

            log.info("Cmd: {} from chat id: {} received from unknown userId: {} name: {} lastName: {}", userId, user.getFirstName(), user.getLastName());

            ReplyDto replyForUnknownUser = userProperties.getReplyForUnknownUser();
            if (replyForUnknownUser != null) {

                log.info("Sending response: {} on cmd: {} from chat id: {} received from unknown",
                        replyForUnknownUser, cmd, chatId);

                tryExecute(ReplyFactory.of(chatId, replyForUnknownUser));
            }

            return;
        }

        AnalyzedResult result = analyzer.analyze(cmd);

        if (result.isNeedToIgnore()) {
            log.info("Cmd: {} with message id: {} received from: {} is need to be ignored", cmd, messageId, user);
        } else if (result.isUnknown()) {
            log.info("Cmd: {} with message id: {} received from: {} is unknown", cmd, messageId, user);

            if (result.getUnknownReply() != null) {
                tryExecute(ReplyFactory.of(chatId, result.getUnknownReply()));
            }
        } else if (result.isNeedSyncReply()) {
            log.info("Cmd: {} with message id: {} received from: {} is need to be replied in sync manner", cmd, messageId, user);

            tryExecute(ReplyFactory.of(chatId, Objects.requireNonNull(result.getSyncReplyDto())));

        } else {

            ImmutableAsyncCmd asyncCmd = result.getAsyncCmd();

            if (asyncCmd == null) {
                log.warn("Illegal state: asyncCmd is null");
                return;
            }

            if (asyncCmd.getBeforeActionReply() != null) {
                log.error("Sending sync response for async command: {} with id: {}", cmd, messageId);

                tryExecute(ReplyFactory.of(chatId, asyncCmd.getBeforeActionReply()));
            }

            Long id = requestsCounter.incrementAndGet();
            RequestFromRemote request = new RequestFromRemote(id, cmd, "body", asyncCmd.getTimeoutMs()); // todo fix body

            interconnector.sendRequest(request);

            interconnector.processor()
                    .filter(response -> cmd.equals(response.cmd()) && id.equals(response.correlationId()))
                    .timeout(Duration.ofMillis(asyncCmd.getTimeoutMs()))
                    .take(1)
                    .subscribe(response -> {
                        log.info("Response on cmd: {} with id: {}. Message: {}. Sending response to messageId: {}", cmd, id, response.message(), messageId);

                        ReplyDto responseMessage = Optional.ofNullable(ReplyDto.ofText(response.message()))
                                .orElse(asyncCmd.getReply());

                        tryExecute(ReplyFactory.of(chatId, responseMessage));

                    }, t -> {
                        log.error("Error during executing request cmd: {} with id: {}. Error: {}", cmd, id, t.getMessage());

                        if (t instanceof TimeoutException) {


                            ReplyDto timeoutReplyMessage = asyncCmd.getTimeoutError();

                            if (timeoutReplyMessage != null) {
                                log.info("Sending response for timeout exception: {} for cmd: {} with messageId: {}", timeoutReplyMessage, cmd, messageId);

                                tryExecute(ReplyFactory.of(chatId, timeoutReplyMessage));
                            }
                        }
                    });
        }
    }
}
