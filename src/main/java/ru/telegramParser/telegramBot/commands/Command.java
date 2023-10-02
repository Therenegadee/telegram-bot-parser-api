package ru.telegramParser.telegramBot.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegramParser.user.model.User;
import ru.telegramParser.user.model.enums.AuthState;
import ru.telegramParser.user.repository.UserRepository;

import java.util.Optional;

@Component
public class Command {
    @Autowired
    private UserRepository userRepository;

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
        return userOptional.get().getAuthState().equals(AuthState.AUTHENTICATED);
    }

}
