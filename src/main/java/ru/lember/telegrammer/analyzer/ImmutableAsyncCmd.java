package ru.lember.telegrammer.analyzer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.lember.telegrammer.configs.reply.ReplyDto;

@Getter
public class ImmutableAsyncCmd {

    /**
     * Given cmd.
     */
    private String cmd;

    /**
     * Reply message sending back in async manner after performing remote action.
     */
    private ReplyDto reply;

    /**
     * Timeout of sending exact this command. In case of null the one in AsyncCmdParams::globalTimeoutMs is used.
     */
    private long timeoutMs;

    /**
     * Error message in case of timeout. In case of null the one in AsyncCmdParams::globalTimeoutError is used.
     */
    private ReplyDto timeoutError;

    /**
     * Reply message sending back in sync manner just after receiving message in Telegram.
     */
    private ReplyDto beforeActionReply;

    ImmutableAsyncCmd(
            @NotNull String cmd,
            @NotNull ReplyDto reply,
            long timeoutMs,
            @Nullable ReplyDto timeoutError,
            @Nullable ReplyDto beforeActionReply) {
        this.cmd = cmd;
        this.reply = reply;
        this.timeoutMs = timeoutMs;
        this.timeoutError = timeoutError;
        this.beforeActionReply = beforeActionReply;
    }
}
