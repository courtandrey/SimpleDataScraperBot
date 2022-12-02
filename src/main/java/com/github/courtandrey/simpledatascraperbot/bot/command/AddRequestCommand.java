package com.github.courtandrey.simpledatascraperbot.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AddRequestCommand extends BaseCommand{
    public AddRequestCommand() {
        super("add", "Adds request for scraping");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            absSender.execute(new SendMessage(message.getChatId().toString(),
                    """
                            Choose one of avalable scrapers:
                            1. HeadHunter
                            2. HabrCareer
                            """));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
