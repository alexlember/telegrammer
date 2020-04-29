package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BotProperties {

    private boolean inTestMode = false;
    private String token;
    private String name;
    private Integer connectionTimeoutMs = 5000;

}
