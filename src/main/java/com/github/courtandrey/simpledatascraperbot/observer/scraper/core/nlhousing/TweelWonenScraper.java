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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.TweelWonenParser;

import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static java.util.Optional.ofNullable;

public class TweelWonenScraper implements Scraper<RentalOffering> {
    private final TweelWonenParser parser = new TweelWonenParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(getUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing(cityFromRequest())
                .withDataCallback(NLHousingFunctions.visitRentalOffering(parser::addDetails, GETConnector::new))
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.TWEEL_WONEN);
    }

    private String getUrl(NLHousingRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.tweelwonen.nl/en-gb/residential-listings/rent/");
        String city = NLHousingFunctions.parseCity().apply(request.getCity());
        builder.append(city.toLowerCase()).append("?locationofinterest=").append(city);
        builder.append("&orderby=8&orderdescending=true");
        builder.append("&pricerange.minprice=").append(request.getLowestPrice());
        ofNullable(request.getHighestPrice()).ifPresent(price -> builder.append("&pricerange.maxprice=").append(price));
        return builder.toString();
    }
}
