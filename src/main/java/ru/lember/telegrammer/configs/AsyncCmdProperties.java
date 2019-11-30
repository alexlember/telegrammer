package ru.lember.telegrammer.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import java.util.Map;

@Data
@Component
@ConfigurationProperties
public class AsyncCmdProperties {

    private Long globalTimeoutMs = 5000L;
    private ReplyDto globalTimeoutError = ReplyDto.ofText("Response timeout limit exceeded");
    private Map<String, ReplyProperties> replyMapping;
}
