package ru.telegramParser.bot.commands;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.bot.cache.BotCache;
import ru.telegramParser.bot.cache.enums.BotState;
import ru.telegramParser.models.user.model.Role;
import ru.telegramParser.models.user.model.User;
import ru.telegramParser.models.user.model.enums.AuthState;
import ru.telegramParser.models.user.model.enums.ERole;
import ru.telegramParser.models.user.model.enums.UserState;
import ru.telegramParser.models.user.repository.RoleRepository;
import ru.telegramParser.models.user.repository.UserRepository;
import ru.telegramParser.openapi.ApiException;
import ru.telegramParser.openapi.ApiResponse;
import ru.telegramParser.openapi.api.AuthorizationApi;
import ru.telegramParser.openapi.model.SignupRequestOpenApi;
import ru.telegramParser.service.TelegramService;
import ru.telegramParser.service.UserService;
import ru.telegramParser.utils.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.telegramParser.utils.Consts.*;

@Component
@AllArgsConstructor
@Log4j
public class RegisterCommand implements Command {
    private final BotCache botCache;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
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
        if (userService.isRegistered(telegramUserId)) {
            if (userService.isAuthenticated(telegramUserId)) {
                message.setText(ALREADY_LOGGED_IN_REQUEST);
            } else
                message.setText(ALREADY_REGISTERED_REQUEST);
        } else if (!userService.isRegistered(telegramUserId)) {
            message.setText(INPUT_USERNAME);
            botCache.setBotState(telegramUserId, BotState.WAIT_FOR_REGISTER_USERNAME_INPUT);
        }
        return message;
    }

    public SendMessage sendRegisterRequest(Long telegramUserId, String chatId, String notEncryptPassword) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        User user = userRepository.findByTelegramUserId(telegramUserId).get();

        SignupRequestOpenApi signUpRequest = new SignupRequestOpenApi();
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setPassword(notEncryptPassword);

       ApiResponse<Void> response;
        try {
            response = authApi.registerUserWithHttpInfo(signUpRequest);
        } catch (ApiException e) {
            log.debug("error occured while sending login request: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (response.getStatusCode() == 201) {
            Role role = roleRepository.findByName(ERole.ROLE_USER).get();
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            user.setUserState(UserState.WAIT_FOR_EMAIL_VERIFICATION);
            user.setAuthState(AuthState.AUTHENTICATED);
            userRepository.save(user);
            message.setText(SUCCESSFUL_REGISTRATION);
        } else {
            userRepository.delete(user);
            message.setText(BAD_REGISTER_REQUEST);
            log.debug("error while sending register request");
        }
        botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
        return message;
    }

    public SendMessage processUsernameRegisterInput(Long telegramUserId, String chatId, String usernameInput) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (usernameInput
                .chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(b -> b.equals(Character.UnicodeBlock.CYRILLIC))) {
            message.setText(CONTAINS_CYRILLIC_SYMBOLS);
            return message;
        } else if (userRepository.existsByUsername(usernameInput)) {
            message.setText(USERNAME_IS_ALREADY_USED);
            return message;
        } else if (usernameInput.toCharArray().length > 20) {
            message.setText(USERNAME_IS_TOO_LONG);
            return message;
        } else {
            User user = new User();
            user.setTelegramUserId(telegramUserId);
            user.setUsername(usernameInput);
            user.setAuthState(AuthState.NOT_LOGGED_IN);
            userRepository.save(user);
            message.setText(USERNAME_SUCCESSFULLY_SAVED);
            telegramService.sendTelegramMessage(message);

            SendMessage inputEmail = new SendMessage(chatId, INPUT_EMAIL);
            botCache.setBotState(telegramUserId, BotState.WAIT_FOR_REGISTER_EMAIL_INPUT);
            return inputEmail;
        }
    }

    public SendMessage processEmailInput(Long telegramUserId, String chatId, String emailInput) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (!EmailValidator.getInstance().isValid(emailInput)) {
            message.setText(EMAIL_ISNT_VALID);
            return message;
        } else if (userRepository.existsByEmail(emailInput)) {
            message.setText(EMAIL_IS_ALREADY_USED);
            return message;
        } else if (emailInput.toCharArray().length > 35) {
            message.setText(EMAIL_IS_TOO_LONG);
            return message;
        } else {
            User user = userRepository.findByTelegramUserId(telegramUserId).get();
            user.setEmail(emailInput);
            userRepository.save(user);
            message.setText(EMAIL_SUCCESSFULLY_SAVED);
            telegramService.sendTelegramMessage(message);

            SendMessage inputPassword = new SendMessage(chatId, INPUT_PASSWORD);
            botCache.setBotState(telegramUserId, BotState.WAIT_FOR_REGISTER_PASSWORD_INPUT);
            return inputPassword;
        }
    }

    public SendMessage processPasswordRegisterInput(Long telegramUserId, String chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String password = textMessage;
        if (password == null || password.isBlank() || password.isEmpty()) {
            message.setText(PASSWORD_IS_EMPTY);
            return message;
        } else if (!isValidPassword(password)) {
            message.setText(PASSWORD_IS_NOT_VALID);
            return message;
        } else {
            User user = userRepository.findByTelegramUserId(telegramUserId).get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            message.setText(PASSWORD_SUCCESSFULLY_SAVED);
            botCache.setBotState(telegramUserId, BotState.PROCESSING_REGISTER_REQUEST);
            return sendRegisterRequest(telegramUserId, chatId, password);
        }
    }
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
