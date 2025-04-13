package com.github.courtandrey.simpledatascraperbot.bot.step;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

@Slf4j
public class Step0StopAdmin implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        return Try.of(() -> Integer.parseInt(update.getMessage().getText()))
                .map(id -> {
                    StepResponse stepResponse = new StepResponse();
                    stepResponse.setId(TERMINAL_RESPONSE);
                    stepResponse.setName("Cycled processes will finish last loop and will stop");
                    return stepResponse;
                }).getOrElse(() -> {
                    StepResponse stepResponse = new StepResponse();
                    stepResponse.setId(ERROR_RESPONSE);
                    stepResponse.setName("This is not a number");
                    return stepResponse;
                });
    }
}
