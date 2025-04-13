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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.POSTConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.HousehuntingParser;

import java.util.List;
import java.util.Map;

public class HousehuntingScraper implements Scraper<RentalOffering> {
    private final HousehuntingParser househuntingParser = new HousehuntingParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector("https://househunting.nl/wp-json/houses/posts",
                        getPost(req), (context, post) -> String.format(post, context.getCurrentPage()),
                        Map.of("Content-Type", "application/x-www-form-urlencoded")),
                househuntingParser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withDataCallback(rental -> househuntingParser.addDetails(new GETConnector(rental.getUrl()).connect(0), rental))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.HOUSEHUNTING);
    }

    private String getPost(NLHousingRequest request) {
        return "km=10&filter_location=" + NLHousingFunctions.parseCity().apply(request.getCity()) +
                "&type=for-rent&t=420251&page=%d&sort=";
    }
}
