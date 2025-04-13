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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.VgwgroupParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class VgwgroupScraper implements Scraper<RentalOffering> {
    private final VgwgroupParser parser = new VgwgroupParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(
                        getUrl(req),
                        Map.of(),
                        (context, url) -> String.format(url, context.getCurrentPage() * 12)
                ),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withStartingPage(0)
                .withDataCallback(NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new))
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withFilteringCondition((__, off) -> !"verhuurd".equalsIgnoreCase(off.getStatus()))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.VGWGROUP);
    }

    private String getUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder();
        String city = NLHousingFunctions.parseCity().apply(request.getCity());
        builder.append(String.format("https://www.vgwgroup.nl/woningaanbod/huur/%s", city));
        builder.append("?locationofinterest=").append(city);
        builder.append("&orderby=8&orderdescending=true");
        builder.append("&pricerange.minprice=").append(request.getLowestPrice());
        ofNullable(request.getHighestPrice()).ifPresent(price -> builder.append("&pricerange.maxprice=").append(price));
        builder.append("&skip=%d");
        return builder.toString();
    }
}
