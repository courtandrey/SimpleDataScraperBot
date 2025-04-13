package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.bot.TextMessages;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends BaseCommand {
    @Autowired
    private UserService userService;

    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        String commandLine = userService.getUserById(message.getChatId()).filter(User::isAdmin)
                        .map(user -> TextMessages.ALL_COMMANDS).orElse(TextMessages.COMMAND_DECLARATIONS);
        sendAnswer(
                absSender,
                "Hello! It is SimpleDataScraperBot. Simple Bot for scraping data!" + "\n" + commandLine,
                message.getChatId()
        );
    }
}
