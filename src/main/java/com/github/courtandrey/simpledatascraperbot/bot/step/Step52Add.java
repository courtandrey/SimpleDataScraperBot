package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Step52Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        stepResponse.setName("Enter release date in format yyyy-mm-dd. None for no release date filtering");
        stepResponse.setId(53);
        return stepResponse;
    }
}
