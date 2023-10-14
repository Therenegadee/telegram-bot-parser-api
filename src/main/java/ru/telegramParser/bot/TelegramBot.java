package ru.telegramParser.bot;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.telegramParser.bot.cache.BotCache;
import ru.telegramParser.bot.handler.UpdateHandler;
import ru.telegramParser.bot.textHandler.TextHandler;
import ru.telegramParser.utils.Consts;

@Log4j
@Setter
public class TelegramBot extends SpringWebhookBot {
    @Autowired
    private TelegramBotProperties botProperties;
    @Autowired
    private UpdateHandler updateHandler;
    @Autowired
    private TextHandler textHandler;
    @Autowired
    private BotCache botCache;

    private String botPath;
    private String botUsername;
    private String botToken;

    public TelegramBot(SetWebhook setWebhook) {
        super(setWebhook);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        Long telegramUserId = update.getMessage().getFrom().getId();
        String textMessage = update.getMessage().getText();
        if (update.hasMessage() && textMessage.startsWith("/")) {
            return updateHandler.handleUpdate(update);
        } else if (update.hasMessage()){
            return textHandler.handleMessage(telegramUserId, chatId, textMessage);
        } else {
            return new SendMessage(chatId, Consts.CANT_UNDERSTAND);
        }
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
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotPath() {
        return botProperties.getWebhookPath();
    }
}