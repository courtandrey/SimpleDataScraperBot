package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ProcessManager {
    private final Map<Long,List<Process>> processes = new ConcurrentHashMap<>();
    @Bean
    @Scope(scopeName = "prototype")
    public CycledProcess cycledProcess(int waitTime, Long chatId, Strategy strategy) {
        CycledProcess process = new CycledProcess(strategy, waitTime, chatId);
        if (processes.get(chatId) == null) {
            List<Process> processList = new ArrayList<>();
            processList.add(process);
            processes.put(chatId, processList);
        } else {
            processes.get(chatId).add(process);
        }
        return process;
    }

    public boolean checkIfProcessExists(Long chatId, Strategy strategy) {
        if (processes.get(chatId) != null) {
            List<Process> processList = processes.get(chatId);
            return processList.stream().anyMatch(x -> x.getStrategy().getClass() == strategy.getClass());
        }
        return false;
    }

    public Map<Long, List<Process>> getProcesses() {
        return processes;
    }
}
