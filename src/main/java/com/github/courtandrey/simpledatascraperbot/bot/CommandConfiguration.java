package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import com.github.courtandrey.simpledatascraperbot.bot.command.StartCommand;
import com.github.courtandrey.simpledatascraperbot.bot.command.StopCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommandConfiguration {
    @Bean
    public StartCommand startCommand() {
        return new StartCommand();
    }

    @Bean
    public StopCommand stopCommand() {
        return new StopCommand();
    }

    public List<BaseCommand> getCommands() {
        List<BaseCommand> baseCommands = new ArrayList<>();
        baseCommands.add(startCommand());
        baseCommands.add(stopCommand());
        return baseCommands;
    }
}
