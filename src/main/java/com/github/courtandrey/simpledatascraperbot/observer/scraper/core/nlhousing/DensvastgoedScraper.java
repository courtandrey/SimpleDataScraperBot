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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.DensvastgoedParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;

public class DensvastgoedScraper implements Scraper<RentalOffering> {
    private final DensvastgoedParser parser = new DensvastgoedParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(getUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withDataCallback(NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.DENSVASTGOED);
    }

    private String getUrl(NLHousingRequest request) {
        return "https://www.densvastgoed.nl/aanbod/huurwoningen?plaats=" + parseCity().apply(request.getCity())
                    + "&page=%d";
    }
}
