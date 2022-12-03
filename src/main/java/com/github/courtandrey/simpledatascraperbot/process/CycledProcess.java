package com.github.courtandrey.simpledatascraperbot.process;

import com.github.courtandrey.simpledatascraperbot.process.strategy.Strategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CycledProcess extends Process {
    @Autowired
    private ProcessManager processManager;
    private final int waitTime;

    public CycledProcess(Strategy strategy, int waitTime, Long chatId) {
        super(strategy, chatId);
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        while (!this.isKilled()) {
            try {
                strategy.execute();
                if (this.isKilled()) break;
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                break;
            }
        }

        processManager.getProcesses().get(chatId).remove(this);
    }
}
