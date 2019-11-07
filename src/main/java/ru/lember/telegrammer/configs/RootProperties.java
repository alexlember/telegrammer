package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("application")
public class RootProperties {

    @PostConstruct
    private void postConstruct() {
        if (botProperties == null
                || StringUtils.isEmpty(botProperties.getName())
                || StringUtils.isEmpty(botProperties.getToken())) {
            throw new IllegalStateException("botParams branch is need to be specified in application.yml");
        }
        log.info("RootProperties initialized");
    }

    private CmdProperties cmdProperties;
    private BotProperties botProperties;
    private UserProperties userProperties;


}
