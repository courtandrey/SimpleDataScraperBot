package com.github.courtandrey.simpledatascraperbot;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class SimpleDataScraperBotApplication {
    static {
        BasicConfigurator.configure();
    }

    private static final Logger logger = Logger.getLogger(Log4JLogger.class);
    public static void main(String[] args) {
        try {
            new TelegramBotsApi(DefaultBotSession.class);
            SpringApplication.run(SimpleDataScraperBotApplication.class, args);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}