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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.VanderlindenParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.*;

public class VanderlindenScraper implements Scraper<RentalOffering> {
    private final VanderlindenParser parser = new VanderlindenParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                request -> new POSTConnector(
                        "https://www.vanderlinden.nl/woning-huren/",
                        generatePost(request),
                        (context, post) -> post,
                        Map.of("Content-Type", "application/x-www-form-urlencoded")
                ),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withPostProcessing(offering ->
                        NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new).accept(offering))
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest) &&
                nlHousingRequest.getSites().contains(NLHousingSite.VANDERLINDEN);
    }

    private String generatePost(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("zoekterm=");
        builder.append(parseCityLowerCased().apply(request.getCity()));
        builder.append("&");
        builder.append("min-prijs=");
        builder.append(request.getLowestPrice() > 0 ? request.getLowestPrice() : "1");
        if (request.getHighestPrice() != null) {
            builder.append("&");
            builder.append("max-prijs=");
            builder.append(request.getHighestPrice());
        }
        return builder.toString();
    }
}
