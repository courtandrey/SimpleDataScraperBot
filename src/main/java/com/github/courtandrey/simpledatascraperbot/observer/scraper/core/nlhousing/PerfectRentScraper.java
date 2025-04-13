package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.PerfectRentParser;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

public class PerfectRentScraper implements Scraper<RentalOffering> {
    private final PerfectRentParser parser = new PerfectRentParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector("https://www.perfectrent.nl/en/realtime-listings/consumer"),
                parser::parsePage,
                NLHousingRequest.class
        )
                .sameOutput(true)
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withFilteringCondition((__, offering) -> ofNullable(offering.getStatus())
                        .filter(Predicate.not("Rented"::equalsIgnoreCase)).isPresent())
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.PERFECT_RENT);
    }
}
