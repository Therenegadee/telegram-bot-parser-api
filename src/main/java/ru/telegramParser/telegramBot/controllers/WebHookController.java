package ru.telegramParser.telegramBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBot;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class WebHookController {
    private final TelegramBot telegramBot;

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update){
        return telegramBot.onWebhookUpdateReceived(update);
    }

}
