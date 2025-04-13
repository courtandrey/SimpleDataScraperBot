package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Collection;

import static com.github.courtandrey.simpledatascraperbot.bot.TextMessages.UNKNOWN_REQUEST;

@Slf4j
public class ShowAllCommand extends BaseCommand {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    public ShowAllCommand() {
        super("showAll", "Shows all user requests");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        userService.getUserById(message.getChatId())
            .filter(User::isAdmin)
            .ifPresentOrElse(
                    usr -> showAll(absSender, message.getChatId()),
                    () -> Try.of(() -> absSender.execute(new SendMessage(
                            String.valueOf(message.getChatId()),
                            UNKNOWN_REQUEST
                    ))).onFailure(exc -> log.error("Could not send message", exc))
            );
    }

    private void showAll(AbsSender absSender, Long chatId) {
        Collection<Request> requests = requestService.findAll();
        StringBuilder showRequestsTextBuilder = new StringBuilder();
        showRequestsTextBuilder.append("There are following registered requests:");
        for (Request r:requests) {
            showRequestsTextBuilder
                    .append("\n")
                    .append(r.getId())
                    .append(": ")
                    .append(r)
                    .append(" registered for user: ")
                    .append(r.getUser());
        }

        showRequestsTextBuilder.append("\nTo delete one of requests use command /deleteAdmin and then send ID " +
                "of request you want to be removed");

        sendAnswer(
                absSender,
                showRequestsTextBuilder.toString(),
                chatId
        );
    }
}
