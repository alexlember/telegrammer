package ru.lember.telegrammer.analyzer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.lember.telegrammer.configs.reply.ReplyDto;

@Getter
@EqualsAndHashCode
@ToString
public class AnalyzedResult {

    private boolean isSuccess;
    private String errorMessage;

    private boolean needToIgnore;

    private boolean unknown;
    private @Nullable ReplyDto unknownReply;

    private boolean needSyncReply;
    private @Nullable ReplyDto syncReplyDto;

    private @Nullable ImmutableAsyncCmd asyncCmd;

    private AnalyzedResult(
            boolean isSuccess,
            @Nullable String errorMessage,
            boolean needToIgnore,
            boolean unknown,
            @Nullable ReplyDto unknownReply,
            boolean needSyncReply,
            @Nullable ReplyDto syncReplyDto,
            @Nullable ImmutableAsyncCmd asyncCmd) {

        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.needToIgnore = needToIgnore;
        this.unknown = unknown;
        this.unknownReply = unknownReply;
        this.needSyncReply = needSyncReply;
        this.syncReplyDto = syncReplyDto;
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

    static AnalyzedResult unknown(@NotNull ReplyDto unknownReplyMessage) {
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

    static AnalyzedResult sync(@NotNull ReplyDto syncReplyDto) {
        return new AnalyzedResult(
                true,
                null,
                false,
                false,
                null,
                true,
                syncReplyDto,
                null);
    }

    static AnalyzedResult async(
            @NotNull String cmd,
            @NotNull ReplyDto asyncReply,
            long timeout,
            @Nullable ReplyDto timeoutError,
            @Nullable ReplyDto beforeActionReply) {

        ImmutableAsyncCmd asyncCmd = new ImmutableAsyncCmd(cmd, asyncReply, timeout, timeoutError, beforeActionReply);

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
