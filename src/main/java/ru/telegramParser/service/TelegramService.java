package ru.telegramParser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.bot.TelegramBotProperties;

@Service
public class TelegramService {
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected TelegramBotProperties botProperties;


    public void sendTelegramMessage(SendMessage sendMessage) {
        restTemplate.postForEntity
                (
                        botProperties.getPathForMessages(),
                        sendMessage,
                        SendMessage.class,
                        ""
                );
    }
}
