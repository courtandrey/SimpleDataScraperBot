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

    @Bean
    public ShowRequestsCommand showRequestsCommand() {
        return new ShowRequestsCommand();
    }

    @Bean
    public ShowAllCommand showAllCommand() {
        return new ShowAllCommand();
    }

    @Bean
    public DeleteAdminCommand deleteAdminCommand() {
        return new DeleteAdminCommand();
    }

    @Bean
    public StopAdminCommand stopAdminCommand() {
        return new StopAdminCommand();
    }

    @Bean
    public ShowAllRunningProcesses showAllRunningProcesses() {
        return new ShowAllRunningProcesses();
    }

    @Bean
    public DeleteRequestCommand deleteRequestCommand() {
        return new DeleteRequestCommand();
    }

    public List<BaseCommand> getCommands() {
        List<BaseCommand> baseCommands = new ArrayList<>();
        baseCommands.add(startCommand());
        baseCommands.add(stopCommand());
        baseCommands.add(addRequestCommand());
        baseCommands.add(initScrapingCommand());
        baseCommands.add(showRequestsCommand());
        baseCommands.add(showAllCommand());
        baseCommands.add(deleteAdminCommand());
        baseCommands.add(stopAdminCommand());
        baseCommands.add(deleteRequestCommand());
        baseCommands.add(showAllRunningProcesses());
        return baseCommands;
    }
}
