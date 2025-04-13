package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step11Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int num = Integer.parseInt(update.getMessage().getText());
            if (num > 0 && num < 5) {
                stepResponse.setId(12);
                stepResponse.setName("""
                        Do you consider special region? Choose several using comma as delimiter
                        1. Doesn't matter
                        2. Russia
                        3. Ukraine
                        4. Azerbaijan
                        5. Belarus
                        6. Kazakhstan
                        7. Kyrgyzstan
                        8. Uzbekistan
                        9. Georgia
                        10. Other region
                       """);
            } else {
                stepResponse.setName("You should type number between 1 and 5");
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
