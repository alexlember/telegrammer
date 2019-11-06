package ru.lember.telegrammer.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import reactor.core.publisher.Mono;
import ru.lember.telegrammer.bot.BotHandler;

@RestController
@RequestMapping
public class ReactiveController {

    @Autowired
    private LongPollingBot botHandler;

    @GetMapping("/isAvailable")
    public Mono<String> isAvailable() {
        return Mono.just("yes, available");
    }

    @PostMapping("/command")
    public Mono<String> cmd(@RequestBody ChatUpdate update) {

        if (botHandler instanceof BotHandler) {
            ((BotHandler)botHandler).handleUpdate(update.getCmd(), update.getChatId(), update.getMessageId(), update.getUser());
        }

        return Mono.just(String.format("Update: %s is received", update));
    }

}