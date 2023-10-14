package ru.telegramParser.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.bot.TelegramBot;

@RestController
@RequiredArgsConstructor
public class WebHookController {
    private final TelegramBot telegramBot;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update){
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessage sendMessage) {
        HttpHeaders headers = new HttpHeaders();
        telegramBot.sendMessage(sendMessage);
        return new ResponseEntity<>(sendMessage, headers, HttpStatus.CREATED);
    }
}

