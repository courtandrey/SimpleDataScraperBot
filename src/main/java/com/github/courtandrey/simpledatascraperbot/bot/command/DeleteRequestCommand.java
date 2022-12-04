package com.github.courtandrey.simpledatascraperbot.bot.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class DeleteRequestCommand extends BaseCommand{

    public DeleteRequestCommand() {
        super("delete", "Deletes one of user's requests");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        sendAnswer(
                absSender,
                "Type id of request you'd like to remove. If you don't know id of that request use /show command",
                message.getChatId()
        );
    }
}
