package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobSite;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil.renderTextWithSelectAll;

public class Step60Add implements StepFunction {

    @Override
    public StepResponse apply(Update update) {
        StepResponse stepResponse = new StepResponse();
        stepResponse.setName("Choose websites to look into (comma separated):" + "\n" + renderTextWithSelectAll(NLJobSite.values()));
        stepResponse.setId(61);
        return stepResponse;
    }
}
