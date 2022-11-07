package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.DataObserver;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collection;

public class StartCommand extends BaseCommand {
    public StartCommand(String description) {
        super("start", description);
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        logger.info("inited");
        DataObserver observer = new DataObserver();
        while (true) {
            try {
                Collection<Data> data = observer.observe();
                for (Data d:data) {
                    System.out.println(d.toString());
                    SendMessage dataMessage = new SendMessage();
                    dataMessage.setText(d.toString());
                    dataMessage.setChatId(message.getChatId());
                    absSender.execute(dataMessage);
                }
                Thread.sleep(1000 * 60 * 1);
            } catch (IOException | TelegramApiException | InterruptedException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }
}
