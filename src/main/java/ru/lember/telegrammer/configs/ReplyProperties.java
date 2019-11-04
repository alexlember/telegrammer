package ru.lember.telegrammer.configs;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class ReplyProperties {

    /**
     * Reply message sending back in async manner after performing remote action.
     */
    private String replyMessage = "The command is executed on the client side.";

    /**
     * Reply message sending back in sync manner just after receiving message in Telegram.
     */
    private @Nullable String beforeActionReplyMessage;

    /**
     * Timeout of sending exact this command. In case of null the one in AsyncCmdParams::globalTimeoutMs is used.
     */
    private @Nullable Long timeoutMs;

    /**
     * Error message in case of timeout. In case of null the one in AsyncCmdParams::globalTimeoutErrorMessage is used.
     */
    private @Nullable String timeoutErrorMessage;
}
