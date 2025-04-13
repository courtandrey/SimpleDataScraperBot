package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.lv;

import com.github.courtandrey.simpledatascraperbot.entity.data.LatvijasPastsStatus;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.lv.LatvijasPastsRequest;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.lv.LatvijasPastsParser;

import java.util.ArrayList;
import java.util.List;

public class LatvijasPastsScraper implements Scraper<LatvijasPastsStatus> {
    private final LatvijasPastsParser parser = new LatvijasPastsParser();

    @Override
    public List<Pair<Request, Processee<LatvijasPastsStatus>>> scrap(List<Request> reqs) {
        List<Pair<Request, LatvijasPastsStatus>> statuses = new ArrayList<>();
        for (LatvijasPastsRequest request : reqs.stream().map(LatvijasPastsRequest.class::cast).toList()) {
            parser.parsePage(new GETConnector(createUrl(request.getReferenceNumber())).connect(0))
                    .stream().peek(status -> status.setReferenceNumber(request.getReferenceNumber()))
                    .map(status -> Pair.of((Request) request, status))
                    .forEach(statuses::add);
        }
        return statuses.stream().map(pair -> pair.mapSecond(Processee::of)).toList();
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof LatvijasPastsRequest;
    }

    private String createUrl(String referenceNumber) {
        return String.format("https://mans.pasts.lv/tracking-api?lang=en-US&id=%s&include_group=true", referenceNumber);
    }
}
