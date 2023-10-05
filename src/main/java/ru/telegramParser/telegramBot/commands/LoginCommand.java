package ru.telegramParser.telegramBot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.telegramBot.cache.BotCache;
import ru.telegramParser.telegramBot.cache.enums.BotState;
import ru.telegramParser.telegramBot.cache.enums.CommandExecutionState;
import ru.telegramParser.telegramBot.utils.PasswordEncoder;
import ru.telegramParser.user.model.User;
import ru.telegramParser.user.model.enums.AuthState;
import ru.telegramParser.user.model.enums.UserState;
import ru.telegramParser.user.payloads.LoginRequest;

import java.net.http.HttpResponse;

import static ru.telegramParser.telegramBot.utils.Consts.*;

@Component
public class LoginCommand extends Command {
    @Autowired
    private TelegramBotProperties botProperties;
    @Autowired
    private BotCache botCache;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String LOGIN_ENDPOINT = "http://localhost:8080/api/auth/signin";

    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        Long telegramUserId = update.getMessage().getFrom().getId();
        if (!isRegistered(telegramUserId)) {
            sendTelegramMessage(new SendMessage(chatId, NON_REGISTERED_OR_LINKED_ACCOUNT));
            message.setText(REGISTER_OR_LINK_YOUR_ACCOUNT);
        } else {
            if (isAuthenticated(telegramUserId)) {
                message.setText(ALREADY_LOGGED_IN_REQUEST);
                return message;
            }
            botCache.setCommandState(telegramUserId, CommandExecutionState.WAITING_FOR_COMMAND);
            botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
        }
        return message;
    }

    private SendMessage sendLoginRequest(Long telegramUserId, String chatId, User user, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);


        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(password);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<?> response = restTemplate.exchange(
                LOGIN_ENDPOINT,
                HttpMethod.POST,
                request,
                String.class
        );

        if(response.getStatusCode().equals(HttpStatus.OK)) {
            user.setAuthState(AuthState.AUTHENTICATED);
            user.setUserState(UserState.WAIT_FOR_EMAIL_VERIFICATION);
            message.setText(SUCCESSFUL_LOGIN);
        } else {
            message.setText(BAD_LOGIN_REQUEST);
        }
        botCache.setCommandState(telegramUserId, CommandExecutionState.WAITING_FOR_COMMAND);
        botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
        return message;
    }

    public SendMessage processUsernameLoginInput(Long telegramUserId, String chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String username = textMessage;
        if (!userRepository.existsByUsername(username)) {
            message.setText("Зарегистрируйтесь или введите корректные данные!");
            sendTelegramMessage(new SendMessage(chatId, USER_DONT_EXISTS));
            return message;
        } else {
            message.setText(INPUT_PASSWORD);
            botCache.setCommandState(telegramUserId, CommandExecutionState.WAIT_FOR_LOGIN_PASSWORD_INPUT);
            return message;
        }
    }

    public SendMessage processPasswordLoginInput(Long telegramUserId, String chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String password = textMessage;
        User user = userRepository.findByTelegramUserId(telegramUserId).get();
        String encodedUserPassword = passwordEncoder.encode(password);;
        if(!user.getPassword().equals(encodedUserPassword)) {
            message.setText(INCORRECT_PASSWORD);
            return message;
        } else {
            botCache.setCommandState(telegramUserId, CommandExecutionState.PROCESSING_LOGIN_REQUEST);
            return sendLoginRequest(telegramUserId, chatId, user, password);
        }
    }
}

