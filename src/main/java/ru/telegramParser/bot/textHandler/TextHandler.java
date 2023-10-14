package ru.telegramParser.bot.textHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.bot.cache.enums.BotState;
import ru.telegramParser.bot.cache.BotCache;
import ru.telegramParser.bot.commands.RegisterCommand;

import static ru.telegramParser.utils.Consts.*;

@Service
@RequiredArgsConstructor
public class TextHandler {
    private final BotCache botCache;
    private final RegisterCommand registerCommand;

    public SendMessage handleMessage(Long telegramUserId, String chatId, String textMessage) {
        BotState botState = botCache.getBotState(telegramUserId);
        if (botState.equals(BotState.PROCESSING_COMMAND)) {
            return new SendMessage(chatId, PROCESSING_REQUEST_NOW);
        } else {
            switch (botState) {
                case WAIT_FOR_REGISTER_USERNAME_INPUT -> {
                    return registerCommand.processUsernameRegisterInput(telegramUserId, chatId, textMessage);
                }
                case WAIT_FOR_REGISTER_EMAIL_INPUT -> {
                    return registerCommand.processEmailInput(telegramUserId, chatId, textMessage);
                }
                case WAIT_FOR_REGISTER_PASSWORD_INPUT -> {
                    return registerCommand.processPasswordRegisterInput(telegramUserId, chatId, textMessage);
                }
                default -> {
                    return new SendMessage(chatId, CANT_UNDERSTAND);
                }
            }
        }
    }

}
