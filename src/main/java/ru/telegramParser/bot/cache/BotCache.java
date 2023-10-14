package ru.telegramParser.bot.cache;

import org.springframework.stereotype.Component;
import ru.telegramParser.bot.cache.enums.BotState;

import java.util.HashMap;
import java.util.Map;

import static ru.telegramParser.bot.cache.enums.BotState.BASIC_STATE;

@Component
public class BotCache {
    private final Map<Long, BotState> userBotState = new HashMap<>();

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
