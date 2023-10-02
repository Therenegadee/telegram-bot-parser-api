package ru.telegramParser.telegramBot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
@Data
@PropertySource("classpath:application.yml")
public class TelegramBotProperties {
    private String name;
    private String token;
    private String path;
    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;
}
