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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.ParariusParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;
import static java.util.Optional.ofNullable;

public class ParariusScraper implements Scraper<RentalOffering> {
    private final ParariusParser parser = new ParariusParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withDataCallback(NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new))
                .withReqDataPostProcessing(cityFromRequest())
                .withSendingOutCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest)
                && nlHousingRequest.getSites().contains(NLHousingSite.PARARIUS);
    }

    private String createUrl(NLHousingRequest request) {
        return String.format("https://pararius.com/apartments/%s/%d-%d/",
                parseCity().apply(request.getCity()).toLowerCase(),
                getLowestPrice(request.getLowestPrice()),
                getHighestPrice(request.getHighestPrice())) + "page-%d";
    }

    private int getLowestPrice(int lowestPrice) {
        return lowestPrice;
    }

    private int getHighestPrice(Integer highestPrice) {
        return ofNullable(highestPrice)
                .orElse(60000);
    }
}
