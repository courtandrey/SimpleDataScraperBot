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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.VhpnParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.*;
import static java.util.Optional.ofNullable;

public class VhpnScraper implements Scraper<RentalOffering> {
    private final VhpnParser vhpnParser = new VhpnParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req)),
                vhpnParser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.VHPN);
    }

    private String createUrl(NLHousingRequest request) {
        return String.format(
                "https://www.vhpn.nl/index.php?action=search&p=search&street=&city=%s&house_type=&bedrooms=&min-price=%d&max-price=%d&interior=&lng=en",
                parseCity().apply(request.getCity()).toLowerCase(),
                request.getLowestPrice(),
                ofNullable(request.getHighestPrice()).orElse(99999999)
        ) + "&page=%d";
    }
}
