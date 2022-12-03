package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.process.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public abstract class BaseCommand implements IBotCommand {
    @Autowired
    protected ProcessManager processManager;
    protected static Logger logger = LoggerFactory.getLogger(BaseCommand.class);
    final String identifier;
    final String description;
    public BaseCommand(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    @Override
    public String getCommandIdentifier() {
        return identifier;
    }

    @Override
    public String getDescription() {
        return description;
    }


    void sendAnswer(AbsSender absSender, String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        try {
            message.setText(text);
            absSender.execute(message);
        } catch (TelegramApiException e) {
            logger.warn(String.format("Exception %s occurred while executing %s", e.getLocalizedMessage(), identifier));
        }
    }
}

