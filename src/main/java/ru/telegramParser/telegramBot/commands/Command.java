package ru.telegramParser.telegramBot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public interface Command {
    SendMessage apply(Update update);
    // String chatId = update.getMessage().getChatId().toString();
    //        SendMessage sendMessage = new SendMessage();
    //        sendMessage.setChatId(chatId);
    //        sendMessage.setText("parent class for some reason is used");
    //        return sendMessage;
}
