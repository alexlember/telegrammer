package ru.lember.telegrammer.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.lember.telegrammer.analyzer.AnalyzedResult;
import ru.lember.telegrammer.analyzer.CommandAnalyzer;
import ru.lember.telegrammer.analyzer.ImmutableAsyncCmd;
import ru.lember.telegrammer.configs.BotProperties;
import ru.lember.telegrammer.outbound.Interconnector;
import ru.lember.telegrammer.outbound.Request;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class BotHandlerImpl extends TelegramLongPollingBot implements BotHandler {

    private BotProperties properties;
    private CommandAnalyzer analyzer;
    private Interconnector interconnector;

    private AtomicLong requestsCounter = new AtomicLong(0);

    public BotHandlerImpl(DefaultBotOptions botOptions,
                          BotProperties botProperties,
                          CommandAnalyzer analyzer,
                          Interconnector interconnector) {
        super(botOptions);
        this.properties = botProperties;
        this.analyzer = analyzer;
        this.interconnector = interconnector;
    }

    public void onUpdateReceived(Update update) {

        if (update == null) {
            log.warn("Null update received");
            return;
        }

        Message message = update.getMessage();
        String cmd = message.getText();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        String user = message.getFrom().toString();

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
    public void handleUpdate(String cmd, Long chatId, Integer messageId, String user) {
        log.info("Cmd: {} from chat id: {} with message id: {} received from: {}", cmd, chatId, messageId, user);

        AnalyzedResult result = analyzer.analyze(cmd);

        if (result.isNeedToIgnore()) {
            log.info("Cmd: {} with message id: {} received from: {} is need to be ignored", cmd, messageId, user);
        } else if (result.isUnknown()) {
            log.info("Cmd: {} with message id: {} received from: {} is unknown", cmd, messageId, user);

            if (!StringUtils.isEmpty(result.getUnknownReplyMessage())) {
                SendMessage replyMessage = new SendMessage();
                replyMessage.setText(result.getUnknownReplyMessage());
                replyMessage.setChatId(chatId);
                tryExecute(replyMessage);
            }
        } else if (result.isNeedSyncReply()) {
            log.info("Cmd: {} with message id: {} received from: {} is need to be replied in sync manner", cmd, messageId, user);

            SendMessage replyMessage = new SendMessage();
            replyMessage.setText(result.getSyncReplyMessage());
            replyMessage.setChatId(chatId);
            tryExecute(replyMessage);

        } else {

            ImmutableAsyncCmd asyncCmd = result.getAsyncCmd();

            if (asyncCmd == null) {
                log.warn("Illegal state: asyncCmd is null");
                return;
            }

            if (!StringUtils.isEmpty(asyncCmd.getBeforeActionReplyMessage())) {
                log.error("Sending sync response for async command: {} with id: {}", cmd, messageId);

                SendMessage replyMessage = new SendMessage();
                replyMessage.setText(asyncCmd.getBeforeActionReplyMessage());
                replyMessage.setChatId(chatId);
                tryExecute(replyMessage);
            }

            Long id = requestsCounter.incrementAndGet();
            Request request = new Request(id, cmd);

            interconnector.sendRequest(request);

            interconnector.processor()
                    .filter(response -> cmd.equals(response.getCmd()) && id.equals(response.getRequestId()))
                    .timeout(Duration.ofMillis(asyncCmd.getTimeoutMs()))
                    .subscribe(response -> {
                        log.info("Response on cmd: {} with id: {}. Message: {}. Sending response to messageId: {}", cmd, id, response.getReplyMessage(), messageId);

                        String responseMessage = Optional.ofNullable(response.getReplyMessage())
                                .orElse(asyncCmd.getReplyMessage());

                        SendMessage replyMessage = new SendMessage();
                        replyMessage.setText(responseMessage);
                        replyMessage.setChatId(chatId);
                        tryExecute(replyMessage);

                    }, t -> {
                        log.error("Error during executing request cmd: {} with id: {}. Error: {}", cmd, id, t.getMessage());

                        if (t instanceof TimeoutException) {


                            String timeoutReplyMessage = asyncCmd.getTimeoutErrorMessage();

                            if (!StringUtils.isEmpty(timeoutReplyMessage)) {
                                log.info("Sending response for timeout exception for cmd: {} with messageId: {}", cmd, messageId);

                                SendMessage replyMessage = new SendMessage();
                                replyMessage.setText(timeoutReplyMessage);
                                replyMessage.setChatId(chatId);
                                tryExecute(replyMessage);
                            }
                        }
                    });
        }
    }
}
