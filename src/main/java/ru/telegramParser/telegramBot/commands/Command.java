package ru.telegramParser.telegramBot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.telegramBot.TelegramBotProperties;
import ru.telegramParser.user.model.User;
import ru.telegramParser.user.model.enums.AuthState;
import ru.telegramParser.user.repository.UserRepository;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.telegramParser.user.model.enums.AuthState.NOT_LOGGED_IN;

@Component
public class Command {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected TelegramBotProperties botProperties;

    protected SendMessage apply(Update update){
        String chatId = update.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("parent class for some reason is used");
        return sendMessage;
    }

    protected boolean isRegistered(Long userID) {
        Optional<User> userOptional =
                userRepository.findByTelegramUserId(userID);
        return userOptional.isPresent();
    }

    protected boolean isAuthenticated(Long userID) {
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

    protected void sendTelegramMessage(SendMessage sendMessage) {
        restTemplate.postForEntity
                (
                        botProperties.getPathForMessages(),
                        sendMessage,
                        SendMessage.class,
                        ""
                );
    }

}
