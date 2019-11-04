package ru.lember.telegrammer.bot;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.List;

@Slf4j
public class BotInitializerImpl implements BotInitializer, ApplicationContextAware {

    private List<LongPollingBot> bots;

    @Autowired
    public BotInitializerImpl(List<LongPollingBot> bots) {
        this.bots = bots;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {

        log.info("Start running bot");

        // todo uncomment to have actually bot working
//        ApiContextInitializer.init();
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        bots.forEach(bot -> {
//            try {
//                telegramBotsApi.registerBot(bot);
//            } catch (TelegramApiRequestException e) {
//                throw new RuntimeException(e);
//            }
//        });

    }

}
