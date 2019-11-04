package ru.lember.telegrammer.outbound;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import reactor.core.publisher.ReplayProcessor;

@Slf4j
public class InterconnectorImpl implements Interconnector {

    @Getter
    private ReplayProcessor<Response> processor = ReplayProcessor.create();

    private String topicName;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public InterconnectorImpl(String topicName, SimpMessagingTemplate simpMessagingTemplate) {
        this.topicName = topicName;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendRequest(Request request) {
        log.info("Sending request: {} to clients to topic: {}", request, topicName);
        simpMessagingTemplate.convertAndSend(topicName, request);
    }

    @Override
    public void onResponse(Response response) {
        processor.onNext(response);
    }

    @Override
    public ReplayProcessor<Response> processor() {
        return processor;
    }
}
