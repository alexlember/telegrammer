package ru.lember.telegrammer.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
    private String replyForUnknownUser;

    /**
     * Список id чатов, которые получат входящие запросы от RPI.
     */
    private List<String> incomingRequestChatIdReceiver;

}
