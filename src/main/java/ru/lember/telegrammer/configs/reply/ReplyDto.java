package ru.lember.telegrammer.configs.reply;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ReplyDto {

    private Type type;

    /**
     * For Text only 1 element in the list
     */
    private List<ReplyPair> messages;

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
        ReplyPair pair = new ReplyPair(text, null);
        dto.setMessages(Collections.singletonList(pair));
        return dto;
    }

}
