package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.entity.request.movie.IMDBRequest;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Optional.of;

public class ImdbRequestFunction implements RequestTransformation<IMDBRequest>{
    @Override
    public IMDBRequest apply(Map<Integer, Message> integerMessageMap) {
        IMDBRequest request = new IMDBRequest();
        request.setCountry(of(integerMessageMap.get(52).getText()).filter(Predicate.not("none"::equalsIgnoreCase)).orElse(null));
        request.setGenre(of(integerMessageMap.get(50).getText()).filter(Predicate.not("none"::equalsIgnoreCase)).orElse(null));
        request.setMinVotes(Integer.parseInt(integerMessageMap.get(51).getText()));
        request.setReleaseDate(of(integerMessageMap.get(53).getText()).filter(Predicate.not("none"::equalsIgnoreCase)).map(LocalDate::parse).orElse(null));
        return request;
    }
}
