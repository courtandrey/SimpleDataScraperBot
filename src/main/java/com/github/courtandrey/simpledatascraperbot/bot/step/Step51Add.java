package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step51Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int minVotes = Integer.parseInt(update.getMessage().getText());
            if (minVotes < 0) {
                stepResponse.setId(ERROR_RESPONSE);
                stepResponse.setName("Only positive numbers allowed");
            } else {
                stepResponse.setId(52);
                stepResponse.setName("Enter code of country (like US for USA). None for no country filtering");
            }
        }

        catch (Exception e) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("This is not a number!");
        }

        return stepResponse;
    }
}
