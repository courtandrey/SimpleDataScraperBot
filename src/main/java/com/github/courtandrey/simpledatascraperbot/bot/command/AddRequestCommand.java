package com.github.courtandrey.simpledatascraperbot.bot.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class AddRequestCommand extends BaseCommand{
    public AddRequestCommand() {
        super("add", "Adds request for scraping");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        sendAnswer(
                absSender,
                """
                            Choose one of avalable scrapers:
                            1. NLHousing
                            2. Latvijas Pasts
                            3. IMDB movie
                            4. NL Jobs
                            """,
                message.getChatId()
        );
    }
}
