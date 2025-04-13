package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

public class Step40Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        stepResponse.setName("Your request is registered");
        stepResponse.setId(TERMINAL_RESPONSE);
        return stepResponse;
    }
}
