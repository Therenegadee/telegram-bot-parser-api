package ru.telegramParser.telegramBot.cache;

import org.springframework.stereotype.Component;
import ru.telegramParser.telegramBot.cache.enums.BotState;
import ru.telegramParser.telegramBot.cache.enums.CommandExecutionState;

import java.util.HashMap;
import java.util.Map;

import static ru.telegramParser.telegramBot.cache.enums.BotState.BASIC_STATE;
import static ru.telegramParser.telegramBot.cache.enums.CommandExecutionState.WAITING_FOR_COMMAND;

@Component
public class BotCache {
    private final Map<Long, CommandExecutionState> userCommandState = new HashMap<>();
    private final Map<Long, BotState> userBotState = new HashMap<>();

    public void setCommandState(Long telegramUserId, CommandExecutionState commandState) {
        userCommandState.put(telegramUserId, commandState);
    }

    public CommandExecutionState getCommandState(Long telegramUserId) {
        CommandExecutionState commandState = userCommandState.get(telegramUserId);
        if (commandState == null) {
            userCommandState.put(telegramUserId, WAITING_FOR_COMMAND);
        }
        return userCommandState.get(telegramUserId);
    }

    public void setBotState(Long telegramUserId, BotState botState) {
        userBotState.put(telegramUserId, botState);
    }

    public BotState getBotState(Long telegramUserId) {
        BotState botState = userBotState.get(telegramUserId);
        if (botState == null) {
            userBotState.put(telegramUserId, BASIC_STATE);
        }
        return userBotState.get(telegramUserId);
    }
}
