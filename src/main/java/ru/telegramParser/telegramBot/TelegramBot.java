package ru.telegramParser.telegramBot;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telegramParser.telegramBot.commands.CommandsHandler;
import ru.telegramParser.telegramBot.utils.Consts;

@Component(value = "telegramBot")
@RequiredArgsConstructor
@Log4j
public class TelegramBot extends TelegramWebhookBot {
    @Autowired
    private TelegramBotProperties botProperties;
    @Autowired
    private CommandsHandler commandsHandler;

    public TelegramBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                var sendMessage = commandsHandler.handleCommands(update);
                return sendMessage;
            } else {
                return new SendMessage(chatId, Consts.CANT_UNDERSTAND);
            }
        } return new SendMessage(chatId, Consts.CANT_UNDERSTAND);
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.debug(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotPath() {
        return botProperties.getPath();
    }
}