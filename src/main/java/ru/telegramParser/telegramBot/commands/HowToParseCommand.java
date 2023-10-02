package ru.telegramParser.telegramBot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HowToParseCommand extends Command{

    @Override
    public SendMessage apply(Update update) {
        return null;
    }

}
