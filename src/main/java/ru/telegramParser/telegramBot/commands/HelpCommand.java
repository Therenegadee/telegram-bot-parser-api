package ru.telegramParser.telegramBot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommand extends Command{

    private static final String HELP_WINDOW = """
            Список доступных команд:
            /register – зарегистрироваться
            /login – войти в аккаунт
            /howtoparse - гайд по настройке и использованию парсера
            /setsettings - установка настроек парсера
            /startparse - запуск парсера
            """;

    @Override
    public SendMessage apply(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(HELP_WINDOW);
        return sendMessage;
    }

}
