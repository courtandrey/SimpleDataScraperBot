package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.configuration.Configuration;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.observer.DataManager;
import com.github.courtandrey.simpledatascraperbot.process.CycledProcess;
import com.github.courtandrey.simpledatascraperbot.process.strategy.SendNewDataStrategy;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class InitScrapingCommand extends BaseCommand{
    @Autowired
    private UserService userService;
    @Autowired
    private Configuration configuration;

    public InitScrapingCommand() {
        super("init", "init scraping");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        User user = userService.getUserById(message.getFrom().getId()).orElseThrow(UserNotFoundException::new);

        if (user.getRequests().size() == 0) {
            sendAnswer(
                    absSender,
                    "You should add request using /add command",
                    message.getChatId()
            );
            return;
        }

        DataManager observer = configuration.manager();
        if (processManager.checkIfProcessExists(message.getChatId(),
                new SendNewDataStrategy(observer, message, absSender))) {
            sendAnswer(
                    absSender,
                    "Process already executes. If you made /stop command, wait till the end of loop",
                    message.getChatId()
            );
            return;
        }

        CycledProcess process =
                processManager.cycledProcess(
                        1000*60,
                        message.getChatId(),
                        new SendNewDataStrategy(observer, message, absSender));

        sendAnswer(
                absSender,
                "It will take some time. It depends on amount of requests on how broad they are and on internet " +
                        "connection stability\nIt is cycled process it means that it will end only when you send " +
                        "/stop command",
                message.getChatId()
        );

       process.start();
    }
}
