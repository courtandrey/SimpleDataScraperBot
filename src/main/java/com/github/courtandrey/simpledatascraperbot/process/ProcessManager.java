package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ProcessManager {
    private final List<Process> processes = new ArrayList<>();
    @Bean
    @Scope(scopeName = "prototype")
    public CycledProcess cycledProcess(int waitTime, Long chatId, Strategy strategy) {
        CycledProcess process = new CycledProcess(strategy, chatId, waitTime);
        processes.add(process);
        return process;
    }

    public List<Process> getProcesses() {
        return processes;
    }
}
