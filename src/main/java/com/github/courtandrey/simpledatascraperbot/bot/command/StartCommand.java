package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BaseCommand {
    @Autowired
    private UserRepository userRepository;
    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            User newUser = new User(message.getFrom());
            User oldUser = userRepository.findByUserId(message.getChatId()).orElse(new User());
            if (!oldUser.getUserId().equals(newUser.getUserId())) {
                userRepository.save(newUser);
            }
        } catch (TelegramApiException e) {logger.error("Couldn't save user");}


        sendAnswer(
                absSender,
                """
                            Hello! It is SimpleDataScraperBot. Simple Bot for scraping data!
                            You can use /init command to init scraping if you already have requests
                            If you don't have any request or want to add new use /add command
                            To show all registered requests use /show command
                            To delete one of requests use /delete command
                            """,
                message.getChatId()
        );
    }
}
