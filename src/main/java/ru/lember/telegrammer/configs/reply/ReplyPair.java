package ru.lember.telegrammer.configs.reply;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ReplyPair {

    private String message;

    /**
     * Nullable for text replies and for reply markup.
     */
    private String callback;

    public ReplyPair(@NotNull String message, @Nullable String callback) {
        this.message = message;
        this.callback = callback;
    }
}
