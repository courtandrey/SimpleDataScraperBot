package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;

public class CycledProcess extends Process {
    private final int waitTime;

    public CycledProcess(Strategy strategy, Long chatId, int waitTime) {
        super(strategy, chatId);
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        while (!this.isKilled()) {
            try {
                strategy.execute();
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
