package ru.lember.telegrammer.analyzer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@EqualsAndHashCode
@ToString
public class AnalyzedResult {

    private boolean isSuccess;
    private String errorMessage;

    private boolean needToIgnore;

    private boolean unknown;
    private @Nullable String unknownReplyMessage;

    private boolean needSyncReply;
    private @Nullable String syncReplyMessage;

    private @Nullable ImmutableAsyncCmd asyncCmd;

    private AnalyzedResult(
            boolean isSuccess,
            @Nullable String errorMessage,
            boolean needToIgnore,
            boolean unknown,
            @Nullable String unknownReplyMessage,
            boolean needSyncReply,
            @Nullable String syncReplyMessage,
            @Nullable ImmutableAsyncCmd asyncCmd) {

        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.needToIgnore = needToIgnore;
        this.unknown = unknown;
        this.unknownReplyMessage = unknownReplyMessage;
        this.needSyncReply = needSyncReply;
        this.syncReplyMessage = syncReplyMessage;
        this.asyncCmd = asyncCmd;
    }

    static AnalyzedResult failed(@NotNull String errorMessage) {
        return new AnalyzedResult(
                false,
                errorMessage,
                false,
                false,
                null,
                false,
                null,
                null);
    }

    static AnalyzedResult ignored() {
        return new AnalyzedResult(
                true,
                null,
                true,
        true,
                null,
                false,
                null,
                null);
    }

    static AnalyzedResult unknown(@NotNull String unknownReplyMessage) {
        return new AnalyzedResult(
                true,
                null,
                false,
                true,
                unknownReplyMessage,
                false,
                null,
                null);
    }

    static AnalyzedResult sync(@NotNull String syncReplyMessage) {
        return new AnalyzedResult(
                true,
                null,
                false,
                false,
                null,
                true,
                syncReplyMessage,
                null);
    }

    static AnalyzedResult async(
            @NotNull String cmd,
            @NotNull String asyncReplyMessage,
            long timeout,
            @Nullable String timeoutErrorMessage,
            @Nullable String beforeActionReplyMessage) {

        ImmutableAsyncCmd asyncCmd = new ImmutableAsyncCmd(cmd, asyncReplyMessage, timeout, timeoutErrorMessage, beforeActionReplyMessage);

        return new AnalyzedResult(
                true,
                null,
                false,
                false,
                null,
                false,
                null,
                asyncCmd);
    }
}
