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

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public InterconnectorImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendRequest(Request request) {
        log.info("Sending request: {} to /topic/messages", request);
        simpMessagingTemplate.convertAndSend("/topic/messages", request);
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
