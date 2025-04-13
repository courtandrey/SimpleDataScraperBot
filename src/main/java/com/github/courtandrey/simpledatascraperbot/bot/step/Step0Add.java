package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step0Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        if (update.getMessage().getText().equals("1")) {
            stepResponse.setName("Type text you'd like to find in vacancy");
            stepResponse.setId(10);
        }

        else if (update.getMessage().getText().equals("2")) {
            stepResponse.setName("Type integer representing main skill you'd like to find in vacancy " +
                    "(you can find one in career.habr url)");
            stepResponse.setId(20);
        }

        else if (update.getMessage().getText().equals("3")) {
            stepResponse.setId(30);
            stepResponse.setName("""
                            Do you consider special region?
                            1. Amsterdam
                            2. Rotterdam
                            3. Enter other city (behavior is not defined)
                           """);
        }

        else if (update.getMessage().getText().equals("4")) {
            stepResponse.setId(40);
            stepResponse.setName("""
                           Please enter the reference number
                           """);
        }

        else if (update.getMessage().getText().equals("5")) {
            stepResponse.setId(50);
            stepResponse.setName("""
                           Please enter the genre (none for no genre)
                           For example: horror, comedy, thriller, drama, action, crime
                           """);
        }

        else {
            stepResponse.setId(ERROR_RESPONSE);
        }

        return stepResponse;
    }
}
