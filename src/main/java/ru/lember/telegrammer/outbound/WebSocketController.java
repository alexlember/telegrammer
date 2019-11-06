package ru.lember.telegrammer.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
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

//    @MessageMapping("/news")
//    @SendTo("/topic/news")
//    public void send(@Payload Response response) {
//        log.info("Response received: {}", response);
//        interconnector.onResponse(response);
//    }

    @MessageMapping("/topic/messages/reply")
    //@MessageMapping("/chat")
    //@SendTo("/topic/messages")
    public void send(@Payload Response message) {
        log.info("Response Message: {}", message);
        interconnector.onResponse(message);
        //return message;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

//    @MessageMapping("/test")
//    @SendTo("/topic/messages")
//    public void test(@Payload Response message) {
//        log.info("Response Message: {}", message);
//        interconnector.onResponse(message);
//        //return message;
//    }

}
