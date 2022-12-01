package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SimpleDataScraperBot extends TelegramLongPollingCommandBot {
    @Value("${BOT_NAME}")
    private String BOT_USERNAME;
    @Value("${BOT_TOKEN}")
    private String BOT_TOKEN;

    public SimpleDataScraperBot(CommandConfiguration commandConfiguration) {
        for (BaseCommand command:commandConfiguration.getCommands()) {
            register(command);
        }
    }
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }
}

