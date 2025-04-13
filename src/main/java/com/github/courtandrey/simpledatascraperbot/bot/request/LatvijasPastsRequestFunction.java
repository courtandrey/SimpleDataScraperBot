package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.entity.request.lv.LatvijasPastsRequest;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

public class LatvijasPastsRequestFunction implements RequestTransformation<LatvijasPastsRequest> {

    @Override
    public LatvijasPastsRequest apply(Map<Integer, Message> integerMessageMap) {
        LatvijasPastsRequest request = new LatvijasPastsRequest();
        request.setReferenceNumber(integerMessageMap.get(40).getText());
        return request;
    }
}
