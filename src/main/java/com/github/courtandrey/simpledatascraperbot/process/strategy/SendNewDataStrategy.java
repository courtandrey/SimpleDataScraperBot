package com.github.courtandrey.simpledatascraperbot.process.strategy;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collection;

public class SendNewDataStrategy implements Strategy {
    private static final Logger logger = LoggerFactory.getLogger(SendNewDataStrategy.class);
    private final DataManager observer;
    private final Message message;
    private final AbsSender absSender;
    public SendNewDataStrategy(DataManager observer, Message message, AbsSender absSender) {
        this.observer = observer;
        this.message = message;
        this.absSender = absSender;
    }

    @Override
    public void execute() {
        try {
            Collection<Data> data = observer.getNewDataMatchingRequest(message.getFrom().getId());
            for (Data d : data) {
                logger.info(d.toString());
                SendMessage dataMessage = new SendMessage();
                dataMessage.setText(d.toString());
                dataMessage.setChatId(message.getChatId());
                absSender.execute(dataMessage);
            }
            logger.info("Finished circle.");
        } catch (IOException | TelegramApiException e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
