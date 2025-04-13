package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Step50Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        stepResponse.setName("Enter number of minimum votes (0 to permit all)");
        stepResponse.setId(51);
        return stepResponse;
    }
}
