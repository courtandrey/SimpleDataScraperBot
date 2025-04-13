package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Set;

import static com.github.courtandrey.simpledatascraperbot.bot.TextMessages.UNKNOWN_REQUEST;

@Slf4j
public class ShowAllRunningProcesses extends BaseCommand {
    @Autowired
    private UserService userService;

    public ShowAllRunningProcesses() {
        super("showAllProcesses", "Shows all runningProcesses");
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
        StringBuilder showProcesses = new StringBuilder();
        Set<Long> processes = processManager.getProcesses().keySet();
        if (processes.stream().allMatch(process -> processManager.getProcesses().get(process).isEmpty())) {
            showProcesses.append("There are no processes running! If you have registered requests use /init to initiate process");
        } else {
            showProcesses.append("There are following running processes:");
            for (Long r : processManager.getProcesses().keySet()) {
                showProcesses
                        .append("\n")
                        .append(r);
            }

            showProcesses.append("\nTo stop on of the processes use command /stopAdmin and then send ID " +
                    "of request you want to be removed");
        }

        sendAnswer(
                absSender,
                showProcesses.toString(),
                chatId
        );
    }
}
