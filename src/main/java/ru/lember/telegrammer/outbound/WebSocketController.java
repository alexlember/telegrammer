package ru.lember.telegrammer.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import ru.lember.telegrammer.dto.in.ResponseToRemote;
import ru.lember.telegrammer.dto.out.RequestFromRpi;

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
    public void receiveResponse(@Payload ResponseToRemote response) {
        log.info("/topic/messages/reply dto: {}", response);
        interconnector.onRpiUpdateEvent(response);
    }

    @MessageMapping("/topic/requests")
    public void receiveRequest(@Payload RequestFromRpi requestFromRpi) {
        log.info("/topic/requests dto: {}", requestFromRpi);
        interconnector.onRpiUpdateEvent(requestFromRpi);
    }

    /**
     * todo not sure if it's actually working
     */
    @MessageExceptionHandler
    public void handleException(Throwable exception) {
        log.error("WebSocketController error: ", exception);
    }


}
