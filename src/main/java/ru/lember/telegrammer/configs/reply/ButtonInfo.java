package ru.lember.telegrammer.configs.reply;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ButtonInfo {

    private String text;

    /**
     * Nullable for text replies and for reply markup.
     */
    private String callback;

    public ButtonInfo(@NotNull String text, @Nullable String callback) {
        this.text = text;
        this.callback = callback;
    }
}
