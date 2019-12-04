package ru.lember.telegrammer.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
public class BotInitializerImpl implements BotInitializer, ApplicationContextAware {

    private final List<LongPollingBot> bots;

    @Autowired
    public BotInitializerImpl(final List<LongPollingBot> bots) {
        this.bots = bots;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("BotInitializerImpl initialized");
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {

        log.info("Start running bot");

        // todo comment/uncomment to have actually bot working/not working

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        bots.forEach(bot -> {
            try {
                telegramBotsApi.registerBot(bot);
            } catch (TelegramApiRequestException e) {
                log.error("Start running bot error: ", e);
                throw new RuntimeException(e);
            }
        });

    }

}
