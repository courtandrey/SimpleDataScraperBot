package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step10Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        if (update.getMessage().getText().length() < 2) {
            stepResponse.setId(ERROR_RESPONSE);
            stepResponse.setName("You should provide more text.");
        }

        else {
            stepResponse.setId(11);
            stepResponse.setName("""
                            What experience do you have:
                            1. No experience
                            2. Between 1 and 3 years
                            3. Between 3 and 6 years
                            4. More than 6 years
                            5. Doesn't matter
                            """);
        }

        return stepResponse;
    }
}
