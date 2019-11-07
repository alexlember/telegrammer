package ru.lember.telegrammer.bot;

import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Marker bot interface.
 */
public interface BotHandler {

    void handleUpdate(String cmd, Long chatId, Integer messageId, User user);
}
