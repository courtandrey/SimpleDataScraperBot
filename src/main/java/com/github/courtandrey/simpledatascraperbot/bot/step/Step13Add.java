package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

public class Step13Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        if (!update.getMessage().getText().equals("y") && !update.getMessage().getText().equals("n")) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("You should type y - for yes and n - for no");
        } else {
            stepResponse.setName("Your request is registered");
            stepResponse.setId(TERMINAL_RESPONSE);
        }
        return stepResponse;
    }
}
