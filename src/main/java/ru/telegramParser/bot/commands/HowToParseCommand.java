package ru.telegramParser.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HowToParseCommand implements Command{

    @Override
    public SendMessage apply(Update update) {
        return null;
    }

}
