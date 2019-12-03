package ru.lember.telegrammer.bot;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.lember.telegrammer.configs.reply.ReplyDto;
import ru.lember.telegrammer.configs.reply.ButtonInfo;
import ru.lember.telegrammer.configs.reply.Type;
import ru.lember.telegrammer.keyboards.KeyboardFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyFactory {

    @NotNull
    public static SendMessage of(@NotNull Long chatId, @NotNull ReplyDto replyDto) {

        SendMessage replyMessage = new SendMessage(chatId, replyDto.getText());

        Type type = replyDto.getType();
        switch (type) {
            case REPLY_MARKUP:
                replyMessage.setReplyMarkup(KeyboardFactory.ofReply(
                        replyDto.isResize(),
                        replyDto.isSelective(),
                        replyDto.isOneTime(),
                        rebuild(replyDto.getButtons().stream()
                                        .map(ButtonInfo::getText)
                                        .collect(Collectors.toList()),
                                replyDto.getColumnsLimit())));
                break;
            case INLINED_MARKUP:
                replyMessage.setReplyMarkup(KeyboardFactory.inlined(
                        rebuild(replyDto.getButtons(),
                                replyDto.getColumnsLimit())));
                break;

        }

        return replyMessage;
    }

    private static void failIfNeeded(List<?> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List should at least have 1 element");
        }
    }

    @NotNull
    static <T> List<List<T>> rebuild(@NotNull List<T> list, int columnsLimit) {

        failIfNeeded(list);

        if (list.size() <= columnsLimit) {
            return Collections.singletonList(list);
        }

        List<List<T>> rebuiltList = new ArrayList<>();

        List<T> underList = null;
        int count = 0;

        for (T e : list) {

            if (count == columnsLimit) {
                rebuiltList.add(underList);
                count = 0;
            }

            if (count == 0) {
                underList = new ArrayList<>();
            }

            underList.add(e);

            count++;

        }

        rebuiltList.add(underList); // add the last one

        return rebuiltList;

    }
}
