package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step20Add implements StepFunction{
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            Integer.parseInt(update.getMessage().getText());
            stepResponse.setName("""
                           Which level are you:
                           1. Intern
                           2. Junior
                           3. Middle
                           4. Senior
                           5. Lead
                            """);
            stepResponse.setId(21);
        } catch (Exception e) {
            stepResponse.setName("This is not a number");
            stepResponse.setId(ERROR_RESPONSE);
        }

        return stepResponse;
    }
}
