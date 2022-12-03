package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class Process extends Thread {
    protected final Strategy strategy;
    protected final Long chatId;
    private boolean isKilled = false;
    public Process(Strategy strategy, Long chatId) {
        this.strategy=strategy;
        this.chatId=chatId;
    }

    public void kill() {
        isKilled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return isKilled == process.isKilled && Objects.equals(strategy, process.strategy) && Objects.equals(chatId, process.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strategy, chatId, isKilled);
    }
}
