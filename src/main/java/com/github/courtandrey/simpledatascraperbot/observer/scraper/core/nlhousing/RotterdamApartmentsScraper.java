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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.RotterdamApartmentsParser;

import java.util.List;
import java.util.Optional;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;

public class RotterdamApartmentsScraper implements Scraper<RentalOffering> {
    private final RotterdamApartmentsParser parser = new RotterdamApartmentsParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<> (
               req -> new GETConnector(getUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.ROTTERDAMAPARTMENTS);
    }

    private String getUrl(NLHousingRequest request) {
        return String.format("https://rotterdamapartments.com/en/for-rent/%s?priceMin=%d%s",
                NLHousingFunctions.parseCity().apply(request.getCity()),
                request.getLowestPrice(),
                Optional.ofNullable(request.getHighestPrice()).map(price -> "&priceMax=" + price).orElse("")) + "&page=%d";
    }
}
