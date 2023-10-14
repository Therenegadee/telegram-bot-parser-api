package ru.telegramParser.bot.commands;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.bot.cache.BotCache;
import ru.telegramParser.bot.cache.enums.BotState;
import ru.telegramParser.models.user.model.User;
import ru.telegramParser.models.user.model.enums.AuthState;
import ru.telegramParser.models.user.model.enums.UserState;
import ru.telegramParser.models.user.repository.UserRepository;
import ru.telegramParser.openapi.ApiException;
import ru.telegramParser.openapi.ApiResponse;
import ru.telegramParser.openapi.api.AuthorizationApi;
import ru.telegramParser.openapi.model.LoginRequestOpenApi;
import ru.telegramParser.service.TelegramService;
import ru.telegramParser.service.UserService;
import ru.telegramParser.utils.PasswordEncoder;

import static ru.telegramParser.utils.Consts.*;

@Component
@AllArgsConstructor
@Log4j
public class LoginCommand implements Command {
    private final BotCache botCache;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final UserService userService;
    private final AuthorizationApi authApi;

    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        Long telegramUserId = update.getMessage().getFrom().getId();
        if (!userService.isRegistered(telegramUserId)) {
            telegramService.sendTelegramMessage(new SendMessage(chatId, NON_REGISTERED_OR_LINKED_ACCOUNT));
            message.setText(REGISTER_OR_LINK_YOUR_ACCOUNT);
        } else {
            if (userService.isAuthenticated(telegramUserId)) {
                message.setText(ALREADY_LOGGED_IN_REQUEST);
                return message;
            }
            botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
        }
        return message;
    }

    private SendMessage sendLoginRequest(Long telegramUserId, String chatId, User user, String password) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        LoginRequestOpenApi loginRequest = new LoginRequestOpenApi();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(password);

       ApiResponse response;

        try {
            response =  authApi.authenticateUserWithHttpInfo(loginRequest);
        } catch (ApiException e) {
            log.debug("error occured while sending login request: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if(response.getStatusCode() == 200) {
            user.setAuthState(AuthState.AUTHENTICATED);
            user.setUserState(UserState.WAIT_FOR_EMAIL_VERIFICATION);
            message.setText(SUCCESSFUL_LOGIN);
        } else {
            message.setText(BAD_LOGIN_REQUEST);
        }
        botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
        return message;
    }

    public SendMessage processUsernameLoginInput(Long telegramUserId, String chatId, String usernameInput) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (!userRepository.existsByUsername(usernameInput)) {
            message.setText("Зарегистрируйтесь или введите корректные данные!");
            telegramService.sendTelegramMessage(new SendMessage(chatId, USER_DONT_EXISTS));
        } else {
            message.setText(INPUT_PASSWORD);
            botCache.setBotState(telegramUserId, BotState.WAIT_FOR_LOGIN_PASSWORD_INPUT);
        }
        return message;
    }

    public SendMessage processPasswordLoginInput(Long telegramUserId, String chatId, String passwordInput) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userRepository.findByTelegramUserId(telegramUserId).get();
        String encodedUserPassword = PasswordEncoder.encode(passwordInput);
        if(!user.getPassword().equals(encodedUserPassword)) {
            message.setText(INCORRECT_PASSWORD);
            return message;
        } else {
            botCache.setBotState(telegramUserId, BotState.PROCESSING_LOGIN_REQUEST);
            return sendLoginRequest(telegramUserId, chatId, user, passwordInput);
        }
    }
}

