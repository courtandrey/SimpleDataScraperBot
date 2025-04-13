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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.MaxRentalParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;

public class MaxRentalScraper implements Scraper<RentalOffering> {
    private final MaxRentalParser parser = new MaxRentalParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(getUrl(req), Map.of(), (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withStartingPage(0)
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.MAX_RENTAL);
    }

    private String getUrl(NLHousingRequest request) {
        String template = "https://www.maxrental.nl/rts/collections/public/e1a08999/runtime/collection/EAZLEE/data?" +
                "page=%7B%22pageSize%22%3A%226%22%2C%22pageNumber%22%3A$PAGE_NUM%7D" +
                "&filters=%7B%22field%22%3A%22division%22%2C%22operator%22%3A%22eq%22%2C%22value%22%3A%22property%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_label%22%2C%22operator%22%3A%22NIN%22%2C%22value%22%3A%5B%22*%22%5D%7D" +
                "&filters=%7B%22field%22%3A%22tmp_forrent%22%2C%22operator%22%3A%22EQ%22%2C%22value%22%3A%221%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_city%22%2C%22operator%22%3A%22EQ%22%2C%22value%22%3A%22$CITY%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_streetAddress%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_property_type_1%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_property_type_2%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_property_type_3%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_num_bedrooms%22%2C%22operator%22%3A%22GTE%22%2C%22value%22%3A%220%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_interior%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_surface%22%2C%22operator%22%3A%22GTE%22%2C%22value%22%3A%220%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_price%22%2C%22operator%22%3A%22GTE%22%2C%22value%22%3A%22$LOWEST_PRICE%22%7D" +
                "&filters=%7B%22field%22%3A%22tmp_price%22%2C%22operator%22%3A%22$HIGHEST_PRICE_OPERAND%22%2C%22value%22%3A%22$HIGHEST_PRICE%22%7D" +
                "&filters=%7B%22field%22%3A%22po-api%22%2C%22operator%22%3A%22NE%22%2C%22value%22%3A%22*%22%7D" +
                "&sortBy=%7B%22field%22%3A%22ranking%22%2C%22direction%22%3A%22asc%22%7D&language=DUTCH";

        return template
                .replace("$CITY", parseCity().apply(request.getCity()))
                .replace("$LOWEST_PRICE", String.valueOf(request.getLowestPrice()))
                .replace("$HIGHEST_PRICE_OPERAND", request.getHighestPrice() == null ? "GTE" : "LTE")
                .replace("$HIGHEST_PRICE", request.getHighestPrice() == null ? "0" : String.valueOf(request.getHighestPrice()));
    }
}
