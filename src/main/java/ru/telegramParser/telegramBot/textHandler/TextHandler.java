package ru.telegramParser.telegramBot.textHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.telegramBot.cache.BotState;
import ru.telegramParser.telegramBot.cache.BotStateCache;
import ru.telegramParser.telegramBot.commands.RegisterCommand;

import static ru.telegramParser.telegramBot.utils.Consts.CANT_UNDERSTAND;
import static ru.telegramParser.telegramBot.utils.Consts.PASSWORD_SUCCESSFULLY_SAVED;

@Component
@RequiredArgsConstructor
public class TextHandler {
    private final BotStateCache botStateCache;
    private final RegisterCommand registerCommand;

    public SendMessage handleTextMessage(Long telegramUserId, String chatId, String textMessage) {
        BotState botState = botStateCache.getBotState(telegramUserId);
        switch (botState) {
            case WAIT_FOR_REGISTER_USERNAME_INPUT -> {
                return registerCommand.processUsernameRegisterInput(telegramUserId, chatId, textMessage);
            }
            case WAIT_FOR_REGISTER_EMAIL_INPUT -> {
                return registerCommand.processEmailInput(telegramUserId, chatId, textMessage);
            }
            case WAIT_FOR_REGISTER_PASSWORD_INPUT -> {
                return registerCommand.processPasswordInput(telegramUserId, chatId, textMessage);
            }
            case PROCESSING_REGISTER_REQUEST -> {
                return SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(PASSWORD_SUCCESSFULLY_SAVED)
                        .build();
            }
            default -> {
                return new SendMessage(chatId, CANT_UNDERSTAND);
            }
        }
    }

}
