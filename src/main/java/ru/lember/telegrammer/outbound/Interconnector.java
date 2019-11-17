package ru.lember.telegrammer.outbound;

import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammer.dto.ToRemote;
import ru.lember.telegrammer.dto.in.RequestFromRemote;

public interface Interconnector {

    void sendRequest(RequestFromRemote request);
    void onRpiUpdateEvent(ToRemote response);
    ReplayProcessor<ToRemote> processor();


}
