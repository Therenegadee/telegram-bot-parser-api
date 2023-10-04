package ru.telegramParser.telegramBot.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static ru.telegramParser.telegramBot.cache.BotState.BASIC_STATE;

@Component
public class BotStateCache {
    private final Map<Long, BotState> userBotState = new HashMap<>();

    public void setBotState(Long telegramUserId, BotState botState){
        userBotState.put(telegramUserId, botState);
    }

    public BotState getBotState(Long telegramUserId) {
        BotState botState = userBotState.get(telegramUserId);
        if(botState == null) {
           userBotState.put(telegramUserId, BASIC_STATE);
        }
        return userBotState.get(telegramUserId);
    }
}
