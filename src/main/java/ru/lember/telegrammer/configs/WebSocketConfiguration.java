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
    public void registerStompEndpoints(StompEndpointRegistry
                                               registry) {
       // registry.addEndpoint("/cmd"); //todo is it necessary?
//        registry.addEndpoint("/websocket")
//                .setAllowedOrigins("*").withSockJS(); // todo which origins?
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat")
                .setAllowedOrigins("/*").withSockJS();

        registry.addEndpoint("/test");
        registry.addEndpoint("/test")
                .setAllowedOrigins("/*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setApplicationDestinationPrefixes("/app");
    }

}