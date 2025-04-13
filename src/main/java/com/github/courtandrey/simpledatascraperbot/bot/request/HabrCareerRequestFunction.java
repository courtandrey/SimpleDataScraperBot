package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.HabrCareerVacancyRequest;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public class HabrCareerRequestFunction implements RequestTransformation<HabrCareerVacancyRequest> {

    @Override
    public HabrCareerVacancyRequest apply(Map<Integer, Message> dialogChain) {
        HabrCareerVacancyRequest request = new HabrCareerVacancyRequest();
        request.setSkill(Integer.parseInt(dialogChain.get(20).getText()));

        switch (dialogChain.get(21).getText()) {
            case "1" -> request.setLevel(HabrCareerVacancyRequest.Level.INTERN);
            case "2" -> request.setLevel(HabrCareerVacancyRequest.Level.JUNIOR);
            case "3" -> request.setLevel(HabrCareerVacancyRequest.Level.MIDDLE);
            case "4" -> request.setLevel(HabrCareerVacancyRequest.Level.SENIOR);
            case "5" -> request.setLevel(HabrCareerVacancyRequest.Level.LEAD);
        }
        if (dialogChain.get(13).getText().equals("y")) {
            request.setRemote(true);
        }
        return request;
    }
}
