package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Set;

public class ShowRequestsCommand extends BaseCommand{
    @Autowired
    private UserService userService;
    public ShowRequestsCommand() {
        super("show", "Shows user requests");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        User user = userService.getUserById(message.getFrom().getId()).orElse(null);

        if (user == null || user.getRequests().size() == 0) {
            sendAnswer(
                    absSender,
                    "You don't have registered request. Use /add command to add request",
                    message.getChatId()
            );
        }

        else {
            Set<Request> requests = user.getRequests();
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
