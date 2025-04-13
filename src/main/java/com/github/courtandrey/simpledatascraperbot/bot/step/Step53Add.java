package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

public class Step53Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            LocalDate.parse(update.getMessage().getText());
            stepResponse.setName("Your request is registered");
            stepResponse.setId(TERMINAL_RESPONSE);
        } catch (Exception exc) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("This is not a correct date format!");
        }
        return stepResponse;
    }
}
