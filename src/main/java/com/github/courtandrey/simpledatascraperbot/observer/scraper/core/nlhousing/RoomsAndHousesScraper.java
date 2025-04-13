package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETClientConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.RoomsAndHousesParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCityLowerCased;
import static java.util.Optional.ofNullable;

public class RoomsAndHousesScraper implements Scraper<RentalOffering> {
    private final RoomsAndHousesParser parser = new RoomsAndHousesParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETClientConnector(getUrl(req),
                        (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                parser::parsePage,
                NLHousingRequest.class
        )
                .processReqsInParallel(true)
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withFilteringCondition((__, off) -> !"rented".equalsIgnoreCase(off.getStatus()))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.ROOMS_AND_HOUSES);
    }

    private String getUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder("https://www.roomsandhouses.nl/advanced-search/page/$PAGE_NUM/?location%5B0%5D=");
        builder.append(parseCityLowerCased().apply(request.getCity()));
        builder.append("&status%5B0%5D&min-price=").append(request.getLowestPrice());
        builder.append("&max-price=");
        ofNullable(request.getHighestPrice()).ifPresent(builder::append);
        return builder.toString();
    }
}
