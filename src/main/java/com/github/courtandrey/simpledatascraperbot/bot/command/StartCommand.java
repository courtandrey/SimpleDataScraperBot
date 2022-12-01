package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.configuration.User;
import com.github.courtandrey.simpledatascraperbot.observer.DataObserver;
import com.github.courtandrey.simpledatascraperbot.process.CycledProcess;
import com.github.courtandrey.simpledatascraperbot.data.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.process.strategy.SendNewVacanciesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BaseCommand {
    @Autowired
    private DataObserver observer;
    @Autowired
    private UserRepository repository;
    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        logger.info("inited");
        try {
            if (repository.findByUserId(message.getFrom().getId()).isEmpty()) {
                repository.save(new User(message.getFrom()));
            }
        } catch (TelegramApiException e) {logger.error("Couldn't save user");}
        CycledProcess process =
                manager.cycledProcess(1000*60,
                        message.getChatId(),
                        new SendNewVacanciesStrategy(observer, message, absSender));
        (new Thread(process)).start();
    }
}
