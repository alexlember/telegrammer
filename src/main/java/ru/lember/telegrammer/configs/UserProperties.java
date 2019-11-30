package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties("userproperties")
public class UserProperties {

    @PostConstruct
    private void postConstruct() {
        log.info("UserProperties initialized");
    }

    private List<String> allowedUsers;
    private ReplyDto replyForUnknownUser;

    /**
     * Список id чатов, которые получат входящие запросы от RPI.
     */
    private List<String> incomingRequestChatIdReceiver;

}
