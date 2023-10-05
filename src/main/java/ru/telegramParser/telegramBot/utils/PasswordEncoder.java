package ru.telegramParser.telegramBot.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Base64;

@Component
@Log4j
public class PasswordEncoder {

    public static String encode(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.debug("the encryption of password finished with an exception: ", e);
        }
        return null;
    }
}
