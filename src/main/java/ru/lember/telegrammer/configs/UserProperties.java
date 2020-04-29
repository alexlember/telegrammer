package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import java.util.List;

@Slf4j
@Data
public class UserProperties {

    private List<String> allowedUsers;
    private ReplyDto replyForUnknownUser;

    /**
     * Список id чатов, которые получат входящие запросы от RPI.
     */
    private List<String> incomingRequestChatIdReceiver;

}
