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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.VerhuurtbeterParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCityLowerCased;

public class VerhuurtbeterScraper implements Scraper<RentalOffering> {
    private final VerhuurtbeterParser parser = new VerhuurtbeterParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest req && req.getSites().contains(NLHousingSite.VERHUURTBETER);
    }

    private String createUrl(NLHousingRequest nlHousingRequest) {
        String template = "https://verhuurtbeter.nl/aanbod/$CITY";

        template = template.replace("$CITY", parseCityLowerCased().apply(nlHousingRequest.getCity()));

        return template;
    }
}
