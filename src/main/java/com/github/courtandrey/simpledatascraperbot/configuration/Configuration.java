package com.github.courtandrey.simpledatascraperbot.configuration;

import com.github.courtandrey.simpledatascraperbot.bot.SimpleDataScraperBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@org.springframework.context.annotation.Configuration
public class Configuration {
    private final static Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    @Bean
    public TelegramLongPollingCommandBot bot() {
        return new SimpleDataScraperBot();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot());
            return api;
        } catch (TelegramApiException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

}
