package ru.lember.telegrammer.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Slf4j
@Controller
public class WebSocketController {

    private Interconnector interconnector;

    @PostConstruct
    private void postConstruct() {
        log.info("WebSocketController initialized");
    }

    @Autowired
    public WebSocketController(Interconnector interconnector) {
        this.interconnector = interconnector;
    }

    @MessageMapping("/topic/messages/reply")
    public void send(@Payload Response response) {
        log.info("/topic/messages/reply response: {}", response);
        interconnector.onResponse(response);
    }

    /**
     * todo not sure if it's actually working
     */
    @MessageExceptionHandler
    public void handleException(Throwable exception) {
        log.error("WebSocketController error: ", exception);
    }


}
