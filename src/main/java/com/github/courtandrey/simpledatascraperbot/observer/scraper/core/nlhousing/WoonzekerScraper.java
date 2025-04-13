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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.WoonzekerParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.*;

public class WoonzekerScraper implements Scraper<RentalOffering> {
    private final WoonzekerParser parser = new WoonzekerParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(getUrl(req),
                        Map.of(),
                        (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withReqDataPostProcessing(cityFromRequest())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.WOONZEKER);
    }

    private String getUrl(NLHousingRequest request) {
        return "https://woonzeker.com/marketsuite/listing/properties?" +
                "perPage=12" +
                "&filter%5Blocation%5D=" + parseCity().apply(request.getCity()) +
                "&filter%5Bimport_type%5D=RentResident" +
                "&page=$PAGE_NUM";
    }
}
