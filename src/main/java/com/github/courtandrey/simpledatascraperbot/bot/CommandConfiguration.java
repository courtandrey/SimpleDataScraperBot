package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.*;
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
    public AddRequestCommand addRequestCommand() {
        return new AddRequestCommand();
    }
    @Bean
    public StopCommand stopCommand() {
        return new StopCommand();
    }
    @Bean
    public InitScrapingCommand initScrapingCommand() {
        return new InitScrapingCommand();
    }

    public List<BaseCommand> getCommands() {
        List<BaseCommand> baseCommands = new ArrayList<>();
        baseCommands.add(startCommand());
        baseCommands.add(stopCommand());
        baseCommands.add(addRequestCommand());
        baseCommands.add(initScrapingCommand());
        return baseCommands;
    }
}
