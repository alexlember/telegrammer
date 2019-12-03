package ru.lember.telegrammer.keyboards;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.lember.telegrammer.configs.reply.ButtonInfo;

import java.util.List;
import java.util.stream.Collectors;

public class KeyboardFactory {

    @NotNull
    public static InlineKeyboardMarkup inlined(List<List<ButtonInfo>> buttons) {

        return new InlineKeyboardMarkup().setKeyboard(
                buttons.stream()
                        .map(row ->
                                row.stream()
                                        .map(button -> new InlineKeyboardButton(button.getText())
                                                .setCallbackData(button.getCallback()))
                                        .collect(Collectors.toList())
                        )
                        .collect(Collectors.toList()));

    }


    @NotNull
    public static ReplyKeyboardMarkup ofReply(boolean isResize,
                                              boolean isSelective,
                                              boolean isOneTime,
                                              List<List<String>> commandRows) {


        return new ReplyKeyboardMarkup()
                .setResizeKeyboard(isResize)
                .setSelective(isSelective)
                .setOneTimeKeyboard(isOneTime)
                .setKeyboard(commandRows.stream()
                        .map(c -> {
                            KeyboardRow row = new KeyboardRow();
                            c.forEach(row::add);
                            return row;
                        })
                        .collect(Collectors.toList()));

    }

}
