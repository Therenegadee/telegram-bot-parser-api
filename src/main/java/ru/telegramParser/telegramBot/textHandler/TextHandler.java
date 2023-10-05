package ru.telegramParser.telegramBot.textHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.telegramBot.cache.enums.BotState;
import ru.telegramParser.telegramBot.cache.enums.CommandExecutionState;
import ru.telegramParser.telegramBot.cache.BotCache;
import ru.telegramParser.telegramBot.commands.RegisterCommand;

import static ru.telegramParser.telegramBot.utils.Consts.*;

@Component
@RequiredArgsConstructor
public class TextHandler {
    private final BotCache botCache;
    private final RegisterCommand registerCommand;

    public SendMessage handleTextMessage(Long telegramUserId, String chatId, String textMessage) {
        CommandExecutionState commandExecutionState = botCache.getCommandState(telegramUserId);
        BotState botState = botCache.getBotState(telegramUserId);
        if (botState.equals(BotState.PROCESSING_REQUEST)) {
            return new SendMessage(chatId, PROCESSING_REQUEST_NOW);
        } else {
            switch (commandExecutionState) {
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
