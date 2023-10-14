package ru.telegramParser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telegramParser.models.user.model.User;
import ru.telegramParser.models.user.model.enums.AuthState;
import ru.telegramParser.models.user.repository.UserRepository;

import java.util.Optional;

import static ru.telegramParser.models.user.model.enums.AuthState.NOT_LOGGED_IN;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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
}
