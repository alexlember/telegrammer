package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Data
@Component
@ConfigurationProperties("cmdproperties")
public class CmdProperties {

    @PostConstruct
    private void postConstruct() {
        log.info("UserProperties initialized");
    }

    private boolean ignoreUnknownCmd = true;
    private String unknownCmdMessageTemplate = "The command %s is unknown";
    private @Nullable Map<String, String> syncReplyMapping;
    private @Nullable AsyncCmdProperties asyncCmdProperties;

}
