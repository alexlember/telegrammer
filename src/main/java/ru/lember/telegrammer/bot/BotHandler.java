package ru.lember.telegrammer.bot;

/**
 * Marker bot interface.
 */
public interface BotHandler {

    void handleUpdate(String cmd, Long chatId, Integer messageId, String user);
}
