package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.exception.UnmetRequirementsException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step12Add implements StepFunction {
    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            List<Integer> ints = Arrays.stream(update.getMessage().getText().trim().split(","))
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .map(Integer::parseInt)
                    .filter(x -> (x > 0) && (x < 11))
                    .distinct()
                    .toList();

            if (ints.isEmpty()) throw new UnmetRequirementsException();

            stepResponse.setId(13);
            stepResponse.setName("Do you consider only remote work? y/n");
        } catch (Exception e) {
            stepResponse.setName("You must provide only numbers between 1-11, delimited by comma");
            stepResponse.setId(ERROR_RESPONSE);
        }

        return stepResponse;
    }
}
