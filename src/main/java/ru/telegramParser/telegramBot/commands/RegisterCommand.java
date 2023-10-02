package ru.telegramParser.telegramBot.commands;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBot;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.user.payloads.SignupRequest;
import ru.telegramParser.user.repository.UserRepository;

import static ru.telegramParser.telegramBot.utils.Consts.ALREADY_LOGGED_IN_REQUEST;
import static ru.telegramParser.telegramBot.utils.Consts.ALREADY_REGISTERED_REQUEST;

@Component
@Log4j
public class RegisterCommand extends Command {
    @Autowired
    private TelegramBotProperties botProperties;
    @Autowired
    private CommandsHandler commandsHandler;
    @Autowired
    private TelegramBot bot;

    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        Long telegramUserId = update.getMessage().getFrom().getId();
        if (isRegistered(telegramUserId)){
            if(isAuthenticated(telegramUserId)) {
                message.setText(ALREADY_LOGGED_IN_REQUEST);
            } else
                message.setText(ALREADY_REGISTERED_REQUEST);
        } else if(!isRegistered(telegramUserId)) {
            String registrationEndpoint = "http://localhost:8080/api/auth/signup";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String username, email, password;
            username = getLogin(update, chatId);
            password = getPassword(update, chatId);
            email = getEmail(update, chatId);

            SignupRequest signUpRequest = new SignupRequest();
            signUpRequest.setUsername("example_user");
            signUpRequest.setEmail("emal_example@mail.ru");
            signUpRequest.setPassword("password123");

            HttpEntity<SignupRequest> request = new HttpEntity<>(signUpRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<?> response = restTemplate.exchange(
                    registrationEndpoint,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                String successfulMsg = "Регистрация прошла успешно!";
                message.setText(successfulMsg);
            } else {
                String unSuccessfulMsg = "Произошла ошибка при регистрации.";
                message.setText(unSuccessfulMsg);
            }
        }

        return message;
    }

    private String getLogin(Update update, String chatId){
        SendMessage message = new SendMessage(chatId, "Введите логин: ");
        bot.sendMessage(message);
        String username = update.getMessage().getText();
        return username;
    }

    private String getPassword(Update update, String chatId){
        SendMessage message = new SendMessage(chatId, "Введите пароль: ");
        bot.sendMessage(message);
        String password = update.getMessage().getText();
        return password;
    }

    private String getEmail(Update update, String chatId){
        SendMessage message = new SendMessage(chatId, "Введите email: ");
        bot.sendMessage(message);
        String email = update.getMessage().getText();
        return email;
    }

}
