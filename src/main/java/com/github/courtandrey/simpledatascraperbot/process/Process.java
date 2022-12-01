package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;
import lombok.Getter;
@Getter
public abstract class Process implements Runnable {
    protected final Strategy strategy;
    private final Long chatId;
    private boolean isKilled = false;
    public Process(Strategy strategy, Long chatId) {
        this.strategy=strategy;
        this.chatId=chatId;
    }

    public void kill() {
        isKilled = true;
    }
}
