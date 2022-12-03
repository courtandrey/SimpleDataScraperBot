package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.observer.DataManager;
import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        logger.info("inited");
        User user;
        try {
            user = repository.findByUserId(message.getFrom().getId()).orElse(null);
            if (user == null) {
                repository.save(new User(message.getFrom()));
            }
        } catch (TelegramApiException e) {logger.error("Couldn't save user");}
    }
}
