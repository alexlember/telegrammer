package ru.lember.telegrammer.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @PostConstruct
    private void postConstruct() {
        log.info("WebSocketConfiguration initialized");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/websocket");
//        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("/*").withSockJS(); // todo not needed actually

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic/"); // todo could be "/topic/", "/queue/"
        //config.setApplicationDestinationPrefixes("/app"); // todo not needed actually (on client session.send("/app/...)
    }

}