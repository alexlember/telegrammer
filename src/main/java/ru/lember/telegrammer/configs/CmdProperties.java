package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import java.util.Map;

@Slf4j
@Data
public class CmdProperties {

    /**
     * Число секунд для обработки сообщений,
     * пришедших во время offline бота.
     * По истечении этого таймаута сообщения обрабатываться не будут.
     */
    private int storeCommandsSeconds = 3600; // 1 hour

    /**
     * Число потоков для обработки входящих сообщений.
     */
    private int threadsNumber = 4;

    /**
     * Размер очереди для обработки сообщений.
     */
    private int queueCapacity = 128;

    /**
     * Разделитель названия команды от тела.
     */
    private String cmdNameSeparator = " ";
    private boolean ignoreUnknownCmd = true;
    private ReplyDto unknownCmdMessageTemplate = ReplyDto.ofText("The command %s is unknown");
    private @Nullable Map<String, ReplyDto> syncReplyMapping;
    private @Nullable AsyncCmdProperties asyncCmdProperties;

}
