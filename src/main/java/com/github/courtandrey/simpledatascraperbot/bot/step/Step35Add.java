package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.exception.UnmetRequirementsException;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil.getMax;
import static com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil.isValid;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

public class Step35Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            List<Integer> ints = Arrays.stream(update.getMessage().getText().trim().split(","))
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .map(Integer::parseInt)
                    .filter(number -> isValid(NLHousingSite.values(), number))
                    .distinct()
                    .toList();

            if (ints.isEmpty()) throw new UnmetRequirementsException();

            stepResponse.setName("Your request is registered");
            stepResponse.setId(TERMINAL_RESPONSE);
        } catch (Exception e) {
            stepResponse.setName(String.format("You must provide only numbers between 1-%d, delimited by comma",
                    getMax(NLHousingSite.values())));
            stepResponse.setId(ERROR_RESPONSE);
        }

        return stepResponse;
    }
}
