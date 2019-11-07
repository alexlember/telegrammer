package ru.lember.telegrammer.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import ru.lember.telegrammer.analyzer.CommandAnalyzer;
import ru.lember.telegrammer.analyzer.CommandAnalyzerImpl;
import ru.lember.telegrammer.bot.BotInitializer;
import ru.lember.telegrammer.bot.BotInitializerImpl;
import ru.lember.telegrammer.bot.BotHandlerImpl;
import ru.lember.telegrammer.outbound.Interconnector;
import ru.lember.telegrammer.outbound.InterconnectorImpl;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
public class AppConfiguration {

    private RootProperties properties;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AppConfiguration(RootProperties properties, SimpMessagingTemplate messagingTemplate) {
        this.properties = properties;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("AppConfiguration initialized");
    }

    @Bean
    BotInitializer botConnector(final List<LongPollingBot> botHandlers) {
        return new BotInitializerImpl(botHandlers);
    }

    @Bean
    LongPollingBot botHandler(final CommandAnalyzer analyzer, final Interconnector interconnector) {

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(properties.getBotProperties().getConnectionTimeoutMs())
                .build();

        botOptions.setRequestConfig(config);

        return new BotHandlerImpl(botOptions, properties.getBotProperties(), analyzer, interconnector, properties.getUserProperties());
    }

    @Bean
    CommandAnalyzer analyzer() {

        return new CommandAnalyzerImpl(properties.getCmdProperties());
    }

    @Bean
    Interconnector interconnector() {
        return new InterconnectorImpl(messagingTemplate);
    }

}
