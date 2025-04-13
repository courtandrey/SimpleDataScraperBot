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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.WonenbijbouwinvestParser;

import java.util.Collection;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;
import static java.util.Optional.ofNullable;

public class WonenbijbouwinvestScraper implements Scraper<RentalOffering> {
    private final WonenbijbouwinvestParser parser = new WonenbijbouwinvestParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req)),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing(cityFromRequest())
                .withSendingOutCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs)
                .parallelStream()
                .map(pair -> {
                    String doc = new GETConnector(pair.getSecond().unwrap().getUrl()).connect(0);
                    Collection<RentalOffering> offerings = parser.parse(doc);
                    offerings.forEach(offering -> offering.setCity(((NLHousingRequest) pair.getFirst()).getCity()));
                    if (offerings.isEmpty()) return List.of(pair);
                    return offerings.stream().map(offering -> Pair.of(pair.getFirst(), Processee.of(offering)))
                            .peek(pr -> pr.getSecond()
                                    .validIf(offering -> new RentalOfferingRequestSatisfied().test((NLHousingRequest) pr.getFirst(), offering)))
                            .toList();
                }).flatMap(Collection::stream).toList();
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest)
                && nlHousingRequest.getSites().contains(NLHousingSite.WONENBIJBOUWINVEST);
    }

    private String createUrl(NLHousingRequest request) {
        return String.format("https://wonenbijbouwinvest.nl/api/search?price=%d-%s&query=%s&range=5",
                request.getLowestPrice(),
                ofNullable(request.getHighestPrice()).map(String::valueOf).orElse(""),
                parseCity().apply(request.getCity())) + "&page=%d";
    }
}
