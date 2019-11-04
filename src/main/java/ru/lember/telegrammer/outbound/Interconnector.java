package ru.lember.telegrammer.outbound;

import reactor.core.publisher.ReplayProcessor;

public interface Interconnector {

    void sendRequest(Request request);
    void onResponse(Response response);
    ReplayProcessor<Response> processor();


}
