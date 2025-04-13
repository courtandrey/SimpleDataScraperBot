package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step32Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int price = Integer.parseInt(update.getMessage().getText());
            if (price < 0) {
                stepResponse.setId(ERROR_RESPONSE);
                stepResponse.setName("Only positive numbers allowed");
            } else {
                stepResponse.setId(33);
                stepResponse.setName("Enter the maximum budget. UB for unbound");
            }
        }

        catch (Exception e) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("This is not a number!");
        }

        return stepResponse;
    }
}
