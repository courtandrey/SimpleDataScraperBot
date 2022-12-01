package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.DataObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class StartCommand extends BaseCommand {
    @Autowired
    private DataObserver observer;
    public StartCommand() {
        super("start", "initializes bot");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        logger.info("inited");
        while (true) {
            try {
                Collection<Data> data = observer.observe();
                for (Data d : data) {
                    logger.info(d.toString());
                    SendMessage dataMessage = new SendMessage();
                    dataMessage.setText(d.toString());
                    dataMessage.setChatId(message.getChatId());
                    absSender.execute(dataMessage);
                }
                logger.info("Finished circle.");
                Thread.sleep(1000 * 60);
            } catch (IOException | TelegramApiException | InterruptedException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }
}
