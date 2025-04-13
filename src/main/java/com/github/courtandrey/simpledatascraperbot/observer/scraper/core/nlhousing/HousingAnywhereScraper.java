package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.HousingAnywhereParser;

import java.util.Collections;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;
import static java.util.Optional.ofNullable;

public class HousingAnywhereScraper implements Scraper<RentalOffering> {
    private final HousingAnywhereParser parser = new HousingAnywhereParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req), Collections.emptyMap(),
                        (context, url) -> String.format(url, context.getCurrentPage())),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing((offering, req) -> {
                    if (Boolean.TRUE.equals(req.getPetsAllowed())) {
                        offering.setPetsAllowed(true);
                    }
                })
                .withReqDataPostProcessing(NLHousingFunctions.cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest) && nlHousingRequest.getSites().contains(NLHousingSite.HOUSING_ANYWHERE);
    }

    private String createUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder("https://housinganywhere.com/s/");
        builder.append(parseCity().apply(request.getCity())).append("--Netherlands?");
        builder.append("priceMin=").append(request.getLowestPrice()).append("00");
        ofNullable(request.getPetsAllowed())
                .filter(Boolean::booleanValue)
                .ifPresent(pets -> builder.append("&facilities=pets_friendly"));
        ofNullable(request.getHighestPrice())
                .ifPresent(price -> builder.append("&priceMax=").append(price).append("00"));
        builder.append("&page=%d");
        return builder.toString();
    }
}
