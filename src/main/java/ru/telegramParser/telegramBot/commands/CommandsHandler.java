package ru.telegramParser.telegramBot.commands;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.utils.Consts;

import java.util.Map;


@Component
@Log4j
public class CommandsHandler {
    public final Map<String, Command> commands;

    public CommandsHandler(
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

    public SendMessage handleCommands(Update update) {
        String text = update.getMessage().getText().split(" ")[0];
        String chatId = update.getMessage().getChatId().toString();
        Command command = commands.get(text);
        if (command != null) {
            return command.apply(update);
        } else {
            return new SendMessage(chatId, Consts.UNKNOWN_COMMAND);
        }
    }


}
