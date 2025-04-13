package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step30Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int num = Integer.parseInt(update.getMessage().getText());
            if (num > 0 && num < 4) {
                if (num == 3) {
                    stepResponse.setId(31);
                    stepResponse.setName("Enter the city name. Correct output is not guaranteed");
                } else {
                    stepResponse.setId(32);
                    stepResponse.setName("Choose your lowest budget (enter the number)");
                }
            } else {
                stepResponse.setId(ERROR_RESPONSE);
            }
        }

        catch (Exception e) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("This is not a number!");
        }

        return stepResponse;
    }
}
