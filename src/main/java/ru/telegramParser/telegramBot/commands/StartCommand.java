package ru.telegramParser.telegramBot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand extends Command{
    private final String HELLO_WINDOW =
            """
            Приветствую!
            Для начала работы с парсером необходимо зарегистрироваться или войти в аккаунт.
            Сделать это можно с помощью команд:
            /register – для регистрации
            /login – для авторизации
            Узнать список остальных комманд можно с помощью /help
                    """;
    @Override
    public SendMessage apply(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(HELLO_WINDOW);
        return sendMessage;
    }

}
