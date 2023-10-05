package ru.telegramParser.telegramBot.config;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.telegramParser.telegramBot.TelegramBot;
import ru.telegramParser.telegramBot.TelegramBotProperties;

@Configuration
@AllArgsConstructor
public class TelegramBotConfig {
    private final TelegramBotProperties botProperties;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botProperties.getWebhookPath()).build();
    }

    @Bean
    public TelegramBot webhookBot(SetWebhook setWebhook) {
        TelegramBot bot = new TelegramBot(setWebhook);
        bot.setBotPath(botProperties.getWebhookPath());
        bot.setBotUsername(botProperties.getUsername());
        bot.setBotToken(botProperties.getToken());
        return bot;
    }

    @Bean
    @Scope(scopeName = "prototype")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
