package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Step31Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        stepResponse.setName("Choose your lowest budget (enter the number. 0 for unbound).");
        stepResponse.setId(32);
        return stepResponse;
    }
}
