package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.observer.DataManager;
import com.github.courtandrey.simpledatascraperbot.process.CycledProcess;
import com.github.courtandrey.simpledatascraperbot.process.strategy.SendNewDataStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class InitScrapingCommand extends BaseCommand{
    @Autowired
    private DataManager observer;
    @Autowired
    private UserRepository repository;

    public InitScrapingCommand() {
        super("init", "init scraping");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        User user = repository.findByUserId(message.getFrom().getId()).orElseThrow(UserNotFoundException::new);
        if (user.getRequests().size() == 0) {
            try {
                absSender.execute(new SendMessage(
                        String.valueOf(message.getChatId()),
                        "You should add request using /add command"
                ));
            } catch (TelegramApiException e) {
                logger.info("Exception occurred: " + e);
            }
            return;
        }

        try {
            absSender.execute(new SendMessage(
                    String.valueOf(message.getChatId()),
                    "It will take some time. It depends on amount of requests on how broad they are and on internet " +
                            "connection stability\nIt is cycled process it means that it will end only when you send " +
                            "/stop command"
            ));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        CycledProcess process =
                manager.cycledProcess(
                        1000*60,
                        message.getChatId(),
                        new SendNewDataStrategy(observer, message, absSender));
        (new Thread(process, "CycledProcess " + message.getChatId())).start();
    }
}
