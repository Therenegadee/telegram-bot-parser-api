package ru.telegramParser.telegramBot.config;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.telegramParser.telegramBot.TelegramBot;
import ru.telegramParser.telegramBot.TelegramBotProperties;

@Data
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {
    private final TelegramBotProperties botProperties;

    @Bean
    public TelegramBot telegramBot(){
        DefaultBotOptions options = new DefaultBotOptions();

        options.setProxyHost(botProperties.getProxyHost());
        options.setProxyPort(botProperties.getProxyPort());
        options.setProxyType(botProperties.getProxyType());

        TelegramBot telegramBot = new TelegramBot(options);
        return telegramBot;
    }

    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource msgSource =
                new ReloadableResourceBundleMessageSource();
        msgSource.setBasename("classpath:messages");
        msgSource.setDefaultEncoding("UTF-8");
        return msgSource;
    }
}
