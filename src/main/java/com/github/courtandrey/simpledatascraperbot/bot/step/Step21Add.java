package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step21Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int num = Integer.parseInt(update.getMessage().getText());
            if (num > 0 && num < 6) {
                stepResponse.setName("Do you consider only remote work? y/n");
                stepResponse.setId(13);
            } else {
                stepResponse.setId(ERROR_RESPONSE);
            }
        }

        catch (Exception e) {
            stepResponse.setName("This is not a number");
            stepResponse.setId(ERROR_RESPONSE);
        }

        return stepResponse;
    }
}
