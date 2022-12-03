package com.github.courtandrey.simpledatascraperbot.bot.command;

import com.github.courtandrey.simpledatascraperbot.process.Process;
import com.github.courtandrey.simpledatascraperbot.process.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StopCommand extends BaseCommand{

    public StopCommand() {
        super("stop", "stops all processes");
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        logger.info("Stopping processes for user identified " + message.getFrom().getId());
        try {
            absSender.execute(new SendMessage(
                    String.valueOf(message.getChatId()),
                    "Cycled processes will finish last loop and will stop"
            ));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        for (Process process:manager.getProcesses()) {
            if (process.getChatId().equals(message.getChatId())) {
                process.kill();
            }
        }
    }
}
