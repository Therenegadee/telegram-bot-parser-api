package ru.telegramParser.telegramBot.commands;

import lombok.extern.log4j.Log4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.telegramBot.cache.enums.BotState;
import ru.telegramParser.telegramBot.cache.enums.CommandExecutionState;
import ru.telegramParser.telegramBot.cache.BotCache;
import ru.telegramParser.user.model.Role;
import ru.telegramParser.user.model.User;
import ru.telegramParser.user.model.enums.AuthState;
import ru.telegramParser.user.model.enums.ERole;
import ru.telegramParser.user.model.enums.UserState;
import ru.telegramParser.user.payloads.SignupRequest;
import ru.telegramParser.user.repository.RoleRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.telegramParser.telegramBot.utils.Consts.*;

@Component
@Log4j
public class RegisterCommand extends Command {
    @Autowired
    private TelegramBotProperties botProperties;
    @Autowired
    private BotCache botCache;
    @Autowired
    private RoleRepository roleRepository;
    private static final String REGISTRATION_ENDPOINT = "http://localhost:8080/api/telegram/auth/signup";

    @Override
    public SendMessage apply(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        Long telegramUserId = update.getMessage().getFrom().getId();
        if (isRegistered(telegramUserId)) {
            if (isAuthenticated(telegramUserId)) {
                message.setText(ALREADY_LOGGED_IN_REQUEST);
            } else
                message.setText(ALREADY_REGISTERED_REQUEST);
        } else if (!isRegistered(telegramUserId)) {
            message.setText(INPUT_USERNAME);
            botCache.setCommandState(telegramUserId, CommandExecutionState.WAIT_FOR_REGISTER_USERNAME_INPUT);
        }
        return message;
    }

    public void sendRegisterRequest(Long telegramUserId, String chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        User user = userRepository.findByTelegramUserId(telegramUserId).get();

        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setPassword(user.getPassword());

        HttpEntity<SignupRequest> request = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<?> response = restTemplate.exchange(
                REGISTRATION_ENDPOINT,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Role role = roleRepository.findByName(ERole.ROLE_USER).get();
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            user.setUserState(UserState.WAIT_FOR_EMAIL_VERIFICATION);
            user.setAuthState(AuthState.AUTHENTICATED);
            userRepository.save(user);
            SendMessage successMessage = new SendMessage();
            successMessage.setText("Регистрация прошла успешно!");
            successMessage.setChatId(chatId);
            sendTelegramMessage(successMessage);
        } else {
            userRepository.delete(user);
            SendMessage unsuccessMessage = new SendMessage();
            unsuccessMessage.setText(BAD_REGISTER_REQUEST);
            unsuccessMessage.setChatId(chatId);
            sendTelegramMessage(unsuccessMessage);
            log.debug("error while sending register request");
        }
        botCache.setBotState(telegramUserId, BotState.BASIC_STATE);
    }

    public SendMessage processUsernameRegisterInput(Long telegramUserId, String chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String username = textMessage;
        if (username
                .chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(b -> b.equals(Character.UnicodeBlock.CYRILLIC))) {
            message.setText(CONTAINS_CYRILLIC_SYMBOLS);
            return message;
        } else if (userRepository.existsByUsername(username)) {
            message.setText(USERNAME_IS_ALREADY_USED);
            return message;
        } else if (username.toCharArray().length > 20) {
            message.setText(USERNAME_IS_TOO_LONG);
            return message;
        } else {
            User user = new User();
            user.setTelegramUserId(telegramUserId);
            user.setUsername(username);
            user.setAuthState(AuthState.NOT_LOGGED_IN);
            userRepository.save(user);
            message.setText(USERNAME_SUCCESSFULLY_SAVED);
            SendMessage inputEmail = new SendMessage(chatId, "Введите Ваш Email");
            sendTelegramMessage(inputEmail);
            botCache.setCommandState(telegramUserId, CommandExecutionState.WAIT_FOR_REGISTER_EMAIL_INPUT);
            return message;
        }
    }

    public SendMessage processEmailInput(Long telegramUserId, String chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String email = textMessage;
        if (!EmailValidator.getInstance().isValid(email)) {
            message.setText(EMAIL_ISNT_VALID);
            return message;
        } else if (userRepository.existsByEmail(email)) {
            message.setText(EMAIL_IS_ALREADY_USED);
            return message;
        } else if (email.toCharArray().length > 35) {
            message.setText(EMAIL_IS_TOO_LONG);
            return message;
        } else {
            User user = userRepository.findByTelegramUserId(telegramUserId).get();
            user.setEmail(email);
            userRepository.save(user);
            message.setText(EMAIL_SUCCESSFULLY_SAVED);
            SendMessage inputPassword = new SendMessage(chatId, "Введите Ваш пароль");
            sendTelegramMessage(inputPassword);
            botCache.setCommandState(telegramUserId, CommandExecutionState.WAIT_FOR_REGISTER_PASSWORD_INPUT);
            return message;
        }
    }

    public SendMessage processPasswordInput(Long telegramUserId, String chatId, String textMessage) {
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
            user.setPassword(password);
            userRepository.save(user);
            message.setText(PASSWORD_SUCCESSFULLY_SAVED);
            botCache.setCommandState(telegramUserId, CommandExecutionState.PROCESSING_REGISTER_REQUEST);
            sendRegisterRequest(telegramUserId, chatId);
            return message;
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
