package ru.lember.telegrammer.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.lember.telegrammer.configs.reply.ReplyDto;
import ru.lember.telegrammer.configs.reply.ReplyPair;
import ru.lember.telegrammer.configs.reply.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class ReplyFactoryTest {

    @Test
    void rebuildTest() {

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                ReplyFactory.rebuild(Collections.emptyList(), 4));

        List<String> list = Arrays.asList("1", "2", "3");

        Assertions.assertEquals(
                Collections.singletonList(list),
                ReplyFactory.rebuild(list, 4));

        Assertions.assertEquals(
                Collections.singletonList(list),
                ReplyFactory.rebuild(list, 3));

        Assertions.assertEquals(
                Arrays.asList(Arrays.asList("1", "2"), Collections.singletonList("3")),
                ReplyFactory.rebuild(list, 2));

        Assertions.assertEquals(
                Arrays.asList(Collections.singletonList("1"), Collections.singletonList("2"), Collections.singletonList("3")),
                ReplyFactory.rebuild(list, 1));

        Assertions.assertEquals(
                Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4")),
                ReplyFactory.rebuild(Arrays.asList("1", "2", "3", "4"), 2));

        Assertions.assertEquals(
                Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("3", "4"), Collections.singletonList("5")),
                ReplyFactory.rebuild(Arrays.asList("1", "2", "3", "4", "5"), 2));

        Assertions.assertEquals(
                Arrays.asList(Arrays.asList("1", "2", "3"), Arrays.asList("4", "5")),
                ReplyFactory.rebuild(Arrays.asList("1", "2", "3", "4", "5"), 3));
    }

    @Test
    void ofTextTest() {
        ReplyDto dto = new ReplyDto();
        dto.setType(Type.TEXT);

        List<ReplyPair> messages = new ArrayList<>();
        ReplyPair pair = new ReplyPair("bla bla", null);
        messages.add(pair);
        dto.setMessages(messages);

        SendMessage expectedSendMessage = new SendMessage();
        expectedSendMessage.setChatId(15L);
        expectedSendMessage.setText("bla bla");
        SendMessage actualMessage = ReplyFactory.of(15L, dto);

        Assertions.assertNotNull(actualMessage);
        Assertions.assertNull(actualMessage.getReplyMarkup());

        Assertions.assertEquals(expectedSendMessage.getChatId(), actualMessage.getChatId());
        Assertions.assertEquals(expectedSendMessage.getText(), actualMessage.getText());
    }

    @Test
    void ofReplyMarkupTest() {
        ReplyDto dto = new ReplyDto();
        dto.setType(Type.REPLY_MARKUP);

        List<ReplyPair> messages = new ArrayList<>();

        ReplyPair pair1 = new ReplyPair("bla bla 1", null);
        ReplyPair pair2 = new ReplyPair("bla bla 2", null);
        ReplyPair pair3 = new ReplyPair("bla bla 3", null);

        messages.add(pair1);
        messages.add(pair2);
        messages.add(pair3);

        dto.setMessages(messages);

        SendMessage expectedSendMessage = new SendMessage();
        expectedSendMessage.setChatId(15L);
        SendMessage actualMessage = ReplyFactory.of(15L, dto);

        Assertions.assertNotNull(actualMessage);
        Assertions.assertNotNull(actualMessage.getReplyMarkup());
        Assertions.assertNull(actualMessage.getText());

        Assertions.assertEquals(expectedSendMessage.getChatId(), actualMessage.getChatId());
        Assertions.assertTrue(actualMessage.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        ReplyKeyboardMarkup markup = (ReplyKeyboardMarkup) actualMessage.getReplyMarkup();
        Assertions.assertFalse(markup.getOneTimeKeyboard());
        Assertions.assertFalse(markup.getSelective());
        Assertions.assertFalse(markup.getResizeKeyboard());

        List<KeyboardRow> rows = markup.getKeyboard();

        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals(1, rows.get(0).size());
        Assertions.assertEquals(1, rows.get(1).size());
        Assertions.assertEquals(1, rows.get(2).size());


        /////

        dto.setMessages(messages);
        dto.setColumnsLimit(3);
        dto.setOneTime(true);
        dto.setSelective(true);
        dto.setResize(true);

        actualMessage = ReplyFactory.of(15L, dto);

        Assertions.assertNotNull(actualMessage);
        Assertions.assertNotNull(actualMessage.getReplyMarkup());
        Assertions.assertNull(actualMessage.getText());

        Assertions.assertTrue(actualMessage.getReplyMarkup() instanceof ReplyKeyboardMarkup);

        markup = (ReplyKeyboardMarkup) actualMessage.getReplyMarkup();
        Assertions.assertTrue(markup.getOneTimeKeyboard());
        Assertions.assertTrue(markup.getSelective());
        Assertions.assertTrue(markup.getResizeKeyboard());

        rows = markup.getKeyboard();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals(3, rows.get(0).size());

    }

    @Test
    void ofInlinedMarkupTest() {
        ReplyDto dto = new ReplyDto();
        dto.setType(Type.INLINED_MARKUP);

        List<ReplyPair> messages = new ArrayList<>();

        ReplyPair pair1 = new ReplyPair("bla bla 1", "callback1");
        ReplyPair pair2 = new ReplyPair("bla bla 2", "callback2");
        ReplyPair pair3 = new ReplyPair("bla bla 3", "callback3");

        messages.add(pair1);
        messages.add(pair2);
        messages.add(pair3);

        dto.setMessages(messages);

        SendMessage expectedSendMessage = new SendMessage();
        expectedSendMessage.setChatId(15L);
        SendMessage actualMessage = ReplyFactory.of(15L, dto);

        Assertions.assertNotNull(actualMessage);
        Assertions.assertNotNull(actualMessage.getReplyMarkup());
        Assertions.assertNull(actualMessage.getText());

        Assertions.assertEquals(expectedSendMessage.getChatId(), actualMessage.getChatId());
        Assertions.assertTrue(actualMessage.getReplyMarkup() instanceof InlineKeyboardMarkup);

        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) actualMessage.getReplyMarkup();


        List<List<InlineKeyboardButton>> rows = markup.getKeyboard();

        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals(1, rows.get(0).size());
        Assertions.assertEquals(1, rows.get(1).size());
        Assertions.assertEquals(1, rows.get(2).size());


        /////

        dto.setMessages(messages);
        dto.setColumnsLimit(3);

        actualMessage = ReplyFactory.of(15L, dto);

        Assertions.assertNotNull(actualMessage);
        Assertions.assertNotNull(actualMessage.getReplyMarkup());
        Assertions.assertNull(actualMessage.getText());

        Assertions.assertTrue(actualMessage.getReplyMarkup() instanceof InlineKeyboardMarkup);

        markup = (InlineKeyboardMarkup) actualMessage.getReplyMarkup();

        rows = markup.getKeyboard();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals(3, rows.get(0).size());

    }
}
