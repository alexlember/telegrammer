package ru.lember.telegrammer.analyzer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ImmutableAsyncCmd {

    /**
     * Given cmd.
     */
    private String cmd;

    /**
     * Reply message sending back in async manner after performing remote action.
     */
    private String replyMessage;

    /**
     * Timeout of sending exact this command. In case of null the one in AsyncCmdParams::globalTimeoutMs is used.
     */
    private long timeoutMs;

    /**
     * Error message in case of timeout. In case of null the one in AsyncCmdParams::globalTimeoutErrorMessage is used.
     */
    private String timeoutErrorMessage;

    /**
     * Reply message sending back in sync manner just after receiving message in Telegram.
     */
    private String beforeActionReplyMessage;

    ImmutableAsyncCmd(
            @NotNull String cmd,
            @NotNull String replyMessage,
            long timeoutMs,
            @Nullable String timeoutErrorMessage,
            @Nullable String beforeActionReplyMessage) {
        this.cmd = cmd;
        this.replyMessage = replyMessage;
        this.timeoutMs = timeoutMs;
        this.timeoutErrorMessage = timeoutErrorMessage;
        this.beforeActionReplyMessage = beforeActionReplyMessage;
    }
}
