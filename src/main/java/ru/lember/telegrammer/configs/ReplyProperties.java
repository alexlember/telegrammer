package ru.lember.telegrammer.configs;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import ru.lember.telegrammer.configs.reply.ReplyDto;

@Data
public class ReplyProperties {

    /**
     * Reply message sending back in async manner after performing remote action.
     */
    private ReplyDto reply = ReplyDto.ofText("The command is executed on the client side.");

    /**
     * Reply message sending back in sync manner just after receiving message in Telegram.
     */
    private @Nullable ReplyDto beforeActionReply;

    /**
     * Timeout of sending exact this command. In case of null the one in AsyncCmdParams::globalTimeoutMs is used.
     */
    private @Nullable Long timeoutMs;

    /**
     * Error message in case of timeout. In case of null the one in AsyncCmdParams::globalTimeoutError is used.
     */
    private @Nullable ReplyDto timeoutError;
}
