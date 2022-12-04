package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.process.Process;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class StopCommand extends BaseCommand{

    public StopCommand() {
        super("stop", "stops all processes");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        logger.info("Stopping processes for user identified " + message.getFrom().getId());

        sendAnswer(
                absSender,
                "Cycled processes will finish last loop and will stop",
                message.getChatId()
        );

        List<Process> processList = processManager.getProcesses().get(message.getChatId());

        for (Process process:processList) {
            process.kill();
        }
    }
}
