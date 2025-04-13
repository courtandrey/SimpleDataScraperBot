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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.RentalRotterdamParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class RentalRotterdamScraper implements Scraper<RentalOffering> {
    private final RentalRotterdamParser parser = new RentalRotterdamParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(
                        getUrl(req),
                        Map.of(),
                        (context, url) -> String.format(url, (context.getCurrentPage() - 1) * 14)
                ),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withDataCallback(NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new))
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.RENTAL_ROTTERDAM);
    }

    private String getUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.rentalrotterdam.nl/woningaanbod/huur?moveunavailablelistingstothebottom=true");
        builder.append("&pricerange.minprice=").append(request.getLowestPrice());
        ofNullable(request.getHighestPrice()).ifPresent(price -> builder.append("&pricerange.maxprice=").append(price));
        builder.append("&skip=%d");
        return builder.toString();
    }
}
