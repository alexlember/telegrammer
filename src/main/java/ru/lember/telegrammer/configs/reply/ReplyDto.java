package ru.lember.telegrammer.configs.reply;

import lombok.Data;

import java.util.List;

@Data
public class ReplyDto {

    private Type type;

    /**
     * For Text type and for ReplyKeyboardMarkup
     */
    private String text = "Reply from bot";

    /**
     * Only for KeyboardMarkups
     */
    private List<ButtonInfo> buttons;

    /**
     * Only for KeyboardMarkups
     */
    private int columnsLimit = 1;

    /**
     * Only for ReplyKeyboardMarkup
     */
    private boolean resize = false;
    /**
     * Only for ReplyKeyboardMarkup
     */
    private boolean selective = false;
    /**
     * Only for ReplyKeyboardMarkup
     */
    private boolean oneTime = false;


    public static ReplyDto ofText(String text) {
        ReplyDto dto = new ReplyDto();
        dto.setType(Type.TEXT);
        dto.setText(text);
        return dto;
    }

}
