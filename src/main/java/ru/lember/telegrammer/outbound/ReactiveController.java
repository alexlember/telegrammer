package ru.lember.telegrammer.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.User;
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

    @PostMapping("/testcmd")
    public Mono<String> cmd(@RequestBody ChatUpdate update) {

        if (botHandler instanceof BotHandler) {
            User user = new User(update.getUserId(), null, null, null, null, null);
            ((BotHandler)botHandler).handleUpdate(update.getCmd(), update.getChatId(), update.getMessageId(), user);
        }

        return Mono.just(String.format("Update: %s is received", update));
    }

}