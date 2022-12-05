package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.process.strategy.SendNewDataStrategy;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class InitScrapingCommand extends BaseCommand{
    @Autowired
    private RequestService requestService;

    public InitScrapingCommand() {
        super("init", "init scraping");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        if (requestService.countByUserId(message.getChatId()) == 0) {
            sendAnswer(
                    absSender,
                    "You should add request using /add command",
                    message.getChatId()
            );
            return;
        }

        if (processManager.checkIfProcessExists(message.getChatId(), SendNewDataStrategy.class)) {
            sendAnswer(
                    absSender,
                    "Process already executes. If you made /stop command, wait till the end of loop",
                    message.getChatId()
            );
            return;
        }

       sendAnswer(
               absSender,
               "How long should be latency (in minutes)?",
               message.getChatId()
       );
    }
}
