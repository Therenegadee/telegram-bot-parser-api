package ru.telegramParser.telegramBot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.user.payloads.LoginRequest;

import static ru.telegramParser.telegramBot.utils.Consts.NON_REGISTERED_REQUEST;

@Component
public class LoginCommand extends Command {
    @Autowired
    private TelegramBotProperties botProperties;

    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        Long telegramUserId = update.getMessage().getFrom().getId();
        if(!isRegistered(telegramUserId)) {
            message.setText(NON_REGISTERED_REQUEST);
        } else {
            //TODO: change
            String username = "";
            String password = "";
            ResponseEntity<?> response = sendLoginRequest(username, password);
            if (response.getStatusCode() == HttpStatus.OK) {
                String successfulMsg = "Вы успешно прошли авторизацию!";
                message.setText(successfulMsg);
            } else {
                String unSuccessfulMsg = "Произошла ошибка при авторизации.";
                message.setText(unSuccessfulMsg);
            }
        }
        return message;
    }

    private ResponseEntity<?> sendLoginRequest(String username, String password) {
        String loginEndpoint = "http://localhost:8080/api/auth/signin";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<?> response = restTemplate.exchange(
                loginEndpoint,
                HttpMethod.POST,
                request,
                String.class
        );
        return response;
    }

}
