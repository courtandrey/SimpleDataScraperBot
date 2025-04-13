package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;

public class ShowRequestsCommand extends BaseCommand {
    @Autowired
    private RequestService requestService;

    public ShowRequestsCommand() {
        super("show", "Shows user requests");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        Collection<Request> requests = requestService.findRequestsByUserId(message.getChatId());
        if (requests.isEmpty()) {
            sendAnswer(
                    absSender,
                    "You don't have registered request. Use /add command to add request",
                    message.getChatId()
            );
        }

        else {
            StringBuilder showRequestsTextBuilder = new StringBuilder();
            showRequestsTextBuilder.append("You have following registered requests:");
            for (Request r:requests) {
                showRequestsTextBuilder
                        .append("\n")
                        .append(r.getId())
                        .append(": ")
                        .append(r);
            }

            showRequestsTextBuilder.append("\nTo delete one of requests use command /delete and then send ID " +
                    "of request you want to be removed");

            sendAnswer(
                    absSender,
                    showRequestsTextBuilder.toString(),
                    message.getChatId()
            );
        }



    }
}
