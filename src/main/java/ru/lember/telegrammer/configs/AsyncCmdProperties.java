package ru.lember.telegrammer.configs;

import lombok.Data;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import java.util.Map;

@Data
public class AsyncCmdProperties {

    private Long globalTimeoutMs = 5000L;
    private ReplyDto globalTimeoutError = ReplyDto.ofText("Response timeout limit exceeded");
    private Map<String, ReplyProperties> replyMapping;
}
