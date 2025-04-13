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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.KamernetParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.*;

public class KamernetScraper implements Scraper<RentalOffering> {
    private final KamernetParser parser = new KamernetParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector("https://kamernet.nl/services/api/listing/findlistings",
                        createPostStatement(req), (context, post) -> post.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage())),
                        Map.of("Content-Type", "application/json")),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withReqDataPostProcessing((offering, req) -> {
                    if (Boolean.TRUE.equals(req.getPetsAllowed())) {
                        offering.setPetsAllowed(true);
                    }
                })
                .withReqDataPostProcessing(cityFromRequest())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest) && nlHousingRequest.getSites().contains(NLHousingSite.KAMERNET);
    }

    private String createPostStatement(NLHousingRequest request) {
        String postStatement = """
                {"location":{"cityName":"$CITY","citySlug":"$LOWERCASE_CITY","name":"$CITY"},
                "radiusId":5,"listingTypeIds":[],"maxRentalPriceId":$PRICE,"surfaceMinimumId":2,"listingSortOptionId":1,
                "pageNo":$PAGE_NUM,"suitableForGenderIds":[],"furnishings":[],"availabilityPeriods":[],
                "availableFromDate":null,"isBathroomPrivate":null,"isToiletPrivate":null,"isKitchenPrivate":null,
                "hasInternet":null,"suitableForNumberOfPersonsId":null,"candidateAge":null,"suitableForStatusIds":[],
                "isSmokingInsideAllowed":null,"isPetsInsideAllowed":$PETS_ALLOWED,"roommateMaxNumberId":null,"roommateGenderIds":[],
                "ownerTypeIds":[],"variant":null,"searchview":1,"rowsPerPage":17,"OpResponse":{"Code":1000,"Message":"Operation successful.","HttpStatusCode":200},
                "LogEntryId":null,"citySlug":"$LOWERCASE_CITY","streetName":null,"streetSlug":null}
                """;
        postStatement = postStatement.replace("$PRICE", getPriceIdFromPrice(request.getHighestPrice()));
        postStatement = postStatement.replace("$CITY", parseCity().apply(request.getCity()));
        postStatement = postStatement.replace("$LOWERCASE_CITY", parseCityLowerCased().apply(request.getCity()).toLowerCase());
        postStatement = postStatement.replace("$PETS_ALLOWED", request.getPetsAllowed() == null ? "null" : request.getPetsAllowed().toString());
        return postStatement;
    }

    private String getPriceIdFromPrice(Integer price) {
        if (price == null) return "0";

        for (int i = 100; i <= 1500; i += 100) {
            if (price <= i) return String.valueOf(i/100);
        }

        for (int i = 1750; i <= 6000; i += 250) {
            if (price <= i) return String.valueOf((i - 1500) / 250 + 15);
        }

        return "0";
    }
}
