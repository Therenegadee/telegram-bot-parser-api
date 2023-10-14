package ru.telegramParser.bot.handler;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.bot.cache.BotCache;
import ru.telegramParser.bot.cache.enums.BotState;
import ru.telegramParser.bot.commands.*;
import ru.telegramParser.utils.Consts;

import java.util.Map;


@Component
@Log4j
public class UpdateHandler {
    private final Map<String, Command> commands;
    @Autowired
    private BotCache botCache;


    public UpdateHandler(
            StartCommand startCommand,
            HelpCommand helpCommand,
            RegisterCommand registerCommand,
            LoginCommand loginCommand,
            HowToParseCommand howToParseCommand,
            SetSettingsCommand setSettingsCommand,
            StartParseCommand startParseCommand
    ) {
        commands = Map.of(
                "/start", startCommand,
                "/help", helpCommand,
                "/register", registerCommand,
                "/login", loginCommand,
                "/guide", howToParseCommand,
                "/settings", setSettingsCommand,
                "/parse", startParseCommand
        );
    }

    public SendMessage handleUpdate(Update update) {
        String text = update.getMessage().getText().split(" ")[0];
        Long telegramUserId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        if (text.startsWith("/")) {
            if(commands.containsKey(text)) {
                Command command = commands.get(text);
                botCache.setBotState(telegramUserId, BotState.PROCESSING_COMMAND);
                return command.apply(update);
            }
            else {
                return new SendMessage(chatId, Consts.UNKNOWN_COMMAND);
            }
        } else if (!botCache.getBotState(telegramUserId).equals(BotState.BASIC_STATE)){

            return new SendMessage(chatId, Consts.UNKNOWN_COMMAND);
        } else {
            return new SendMessage(chatId, Consts.CANT_UNDERSTAND);
        }
    }
}

