package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Data
@Component
@ConfigurationProperties
public class BotProperties {

    @PostConstruct
    private void postConstruct() {
        log.info("BotProperties initialized");
    }

    private String token;
    private String name;
    private Integer connectionTimeoutMs = 5000;
}
