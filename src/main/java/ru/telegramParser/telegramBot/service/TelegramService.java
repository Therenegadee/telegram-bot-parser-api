package ru.telegramParser.telegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.user.model.User;
import ru.telegramParser.user.model.enums.AuthState;
import ru.telegramParser.user.repository.UserRepository;

import java.util.Optional;

import static ru.telegramParser.user.model.enums.AuthState.NOT_LOGGED_IN;

@Service
public class TelegramService {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected TelegramBotProperties botProperties;

    public boolean isRegistered(Long userID) {
        Optional<User> userOptional =
                userRepository.findByTelegramUserId(userID);
        return userOptional.isPresent();
    }

    public boolean isAuthenticated(Long userID) {
        Optional<User> userOptional =
                userRepository.findByTelegramUserId(userID);
        AuthState authState = userOptional.get().getAuthState();
        if (authState == null) {
            authState = NOT_LOGGED_IN;
            userOptional.get().setAuthState(NOT_LOGGED_IN);
            userRepository.save(userOptional.get());
        }
        return authState.equals(AuthState.AUTHENTICATED);
    }

    public void sendTelegramMessage(SendMessage sendMessage) {
        restTemplate.postForEntity
                (
                        botProperties.getPathForMessages(),
                        sendMessage,
                        SendMessage.class,
                        ""
                );
    }
}
