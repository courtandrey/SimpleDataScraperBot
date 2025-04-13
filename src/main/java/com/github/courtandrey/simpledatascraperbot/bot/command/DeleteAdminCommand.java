package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;


import static com.github.courtandrey.simpledatascraperbot.bot.TextMessages.UNKNOWN_REQUEST;

@Slf4j
public class DeleteAdminCommand extends BaseCommand {
    @Autowired
    private UserService userService;

    public DeleteAdminCommand() {
        super("deleteAdmin", "Delete any request");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        userService.getUserById(message.getChatId())
                .filter(User::isAdmin)
                .ifPresentOrElse(
                        usr -> showMessage(absSender, message.getChatId()),
                        () -> Try.of(() -> absSender.execute(new SendMessage(
                                String.valueOf(message.getChatId()),
                                UNKNOWN_REQUEST
                        ))).onFailure(exc -> log.error("Could not send message", exc))
                );
    }

    private void showMessage(AbsSender absSender, Long chatId) {
        sendAnswer(
                absSender,
                "Type id of request you'd like to remove. If you don't know id of that request use /showAll command",
                 chatId
        );
    }
}
