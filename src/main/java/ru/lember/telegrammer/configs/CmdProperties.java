package ru.lember.telegrammer.configs;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("cmdproperties")
public class CmdProperties {

    private boolean ignoreUnknownCmd = true;
    private String unknownCmdMessageTemplate = "The command %s is unknown";
    private @Nullable Map<String, String> syncReplyMapping;
    private @Nullable AsyncCmdProperties asyncCmdProperties;

}
