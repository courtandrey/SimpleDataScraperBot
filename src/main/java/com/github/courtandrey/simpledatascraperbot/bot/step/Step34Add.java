package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil.renderText;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;

public class Step34Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        try {
            int num = Integer.parseInt(update.getMessage().getText());
            if (num > 0 && num < 4) {
                stepResponse.setId(35);
                stepResponse.setName("Choose websites to look into (comma separated):" + "\n" + renderText(NLHousingSite.values()));
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
