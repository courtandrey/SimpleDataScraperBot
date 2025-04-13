package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.POSTConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.VbtverhuurmakelaarsParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;
import static java.util.Optional.ofNullable;

public class VbtverhuurmakelaarsScraper implements Scraper<RentalOffering> {
    private final VbtverhuurmakelaarsParser parser = new VbtverhuurmakelaarsParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector(
                        "https://vbtverhuurmakelaars.nl/api/properties/search?type=purchase",
                        processPostStatement(req),
                        (context, post) -> post.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage())),
                        Map.of("Content-Type", "application/json")
                ),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withReqDataPostProcessing(cityFromRequest())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest)
                && nlHousingRequest.getSites().contains(NLHousingSite.VBTVERHUUTMAKELAARS);
    }

    private String processPostStatement(NLHousingRequest request) {
       return """
                {"limit":12, "page":$PAGE_NUM, "filter":{"city": "$CITY", "radius":10, "address":"",
                "priceRental": {"min":$LOWEST_PRICE, "max":$HIGHEST_PRICE}, "availableFrom":"",
                "surface":"", "rooms":0, "typeCategory":""}}
                """
               .replace("$CITY", parseCity().apply(request.getCity()))
               .replace("$LOWEST_PRICE", String.valueOf(request.getLowestPrice()))
               .replace("$HIGHEST_PRICE", String.valueOf(ofNullable(request.getHighestPrice()).orElse(0)));
    }
}
