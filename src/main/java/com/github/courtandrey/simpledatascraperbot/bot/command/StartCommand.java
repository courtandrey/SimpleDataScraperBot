package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.observer.DataManager;
import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BaseCommand {
    @Autowired
    private DataManager observer;
    @Autowired
    private UserRepository repository;
    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            repository.save(new User(message.getFrom()));
        } catch (TelegramApiException e) {logger.error("Couldn't save user");}


        try {
            absSender.execute(new SendMessage(
                    String.valueOf(message.getChatId()),
                    """
                            Hello! It is SimpleDataScraperBot. Simple Bot for scraping data!
                            You can use /init command to init scraping if you already have requests
                            If you don't have any request or want to add new use /add command
                            To show all registered requests use /show command
                            To delete one of requests use /delete command
                            """
            ));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
