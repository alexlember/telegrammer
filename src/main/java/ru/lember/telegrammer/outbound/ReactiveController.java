package ru.lember.telegrammer.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import reactor.core.publisher.Mono;
import ru.lember.telegrammer.bot.BotHandler;

import javax.annotation.PostConstruct;

@Slf4j
@Profile("with-http")
@RestController
@RequestMapping
public class ReactiveController {

    @Autowired
    private LongPollingBot botHandler;

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
    }

    @GetMapping("/isAvailable")
    public Mono<String> isAvailable() {
        return Mono.just("yes, available");
    }

    @PostMapping("/testcmd")
    public Mono<String> cmd(@RequestBody ChatUpdate update) {

        log.info("/testcmd: {}", update);

        if (botHandler instanceof BotHandler) {
            User user = new User(update.getUserId(), null, null, null, null, null);
            ((BotHandler)botHandler).handleUpdate(update.getCmd(), update.getChatId(), update.getMessageId(), user);
        }

        return Mono.just(String.format("Update: %s is received", update));
    }

}