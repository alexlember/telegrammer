package ru.lember.telegrammer.outbound;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammer.dto.ToRemote;
import ru.lember.telegrammer.dto.in.RequestFromRemote;

@Slf4j
public class InterconnectorImpl implements Interconnector {

    @Getter
    private ReplayProcessor<ToRemote> processor = ReplayProcessor.create(1);

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public InterconnectorImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendRequest(RequestFromRemote request) {
        log.info("Sending request: {} to /topic/messages", request);
        simpMessagingTemplate.convertAndSend("/topic/messages", request);
    }

    @Override
    public void onRpiUpdateEvent(ToRemote updateEvent) {
        processor.onNext(updateEvent);
    }

    @Override
    public ReplayProcessor<ToRemote> processor() {
        return processor;
    }
}
