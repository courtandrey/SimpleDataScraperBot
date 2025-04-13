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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.RotterdamRentalServiceParser;

import java.util.List;

import static java.util.Optional.ofNullable;

public class RotterdamRentalServiceScraper implements Scraper<RentalOffering> {
    private final RotterdamRentalServiceParser parser = new RotterdamRentalServiceParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(getUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withFilteringCondition((req, off) -> !"rented".equalsIgnoreCase(off.getStatus()))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.ROTTERDAM_RENTAL_SERVICE);
    }

    private String getUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://rotterdamrentalservice.com/admin/public/api/get-properties?page=%d&limit=50&short_stay=&sort=&property_type=&availibility=&bedrooms=&furnishing=&city=&district=&living_area=&facility=");
        builder.append("&min_price=").append(request.getLowestPrice());
        ofNullable(request.getHighestPrice()).ifPresent(price -> builder.append("&max_price=").append(price));
        return builder.toString();
    }
}
