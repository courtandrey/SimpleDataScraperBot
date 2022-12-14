package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BaseCommand {
    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        sendAnswer(
                absSender,
                """
                            Hello! It is SimpleDataScraperBot. Simple Bot for scraping data!
                            You can use /init command to init scraping if you already have requests
                            If you don't have any request or want to add new use /add command
                            To show all registered requests use /show command
                            To delete one of requests use /delete command
                            To stop all cycled processes use /stop command
                            """,
                message.getChatId()
        );
    }
}
