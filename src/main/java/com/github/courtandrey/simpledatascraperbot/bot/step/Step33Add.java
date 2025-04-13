package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step33Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            if (!"UB".equalsIgnoreCase(update.getMessage().getText())) {
                int price = Integer.parseInt(update.getMessage().getText());
                if (price < 0) {
                    stepResponse.setId(ERROR_RESPONSE);
                    stepResponse.setName("Only positive numbers allowed");
                    return stepResponse;
                }
            }
            stepResponse.setId(34);
            stepResponse.setName("""
                    Should pets be allowed:
                    1. Yes (not stated and negotiable will be still returned)
                    2. No
                    3. Does not matter
                    """);
        }

        catch (Exception e) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("This is not a number!");
        }

        return stepResponse;
    }
}
