package ru.lember.telegrammer.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @MessageMapping("/cmd")
    public void send(@Payload Response response) {
        log.info("Response received: {}", response);
        interconnector.onResponse(response);
    }

}
