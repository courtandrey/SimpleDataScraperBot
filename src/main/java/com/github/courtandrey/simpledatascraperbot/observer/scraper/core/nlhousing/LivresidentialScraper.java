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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.LivresidentialParser;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCityLowerCased;

@Slf4j
public class LivresidentialScraper implements Scraper<RentalOffering> {
    private final LivresidentialParser parser = new LivresidentialParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector(
                        "https://search.livresidential.nl/indexes/livnl_prod_properties/search",
                        generatePostStatement(req),
                        (context, post) -> post.replace("$PAGE_NUM", String.valueOf((context.getCurrentPage() - 1) * 201)),
                        Map.of("Content-Type", "application/json",
                                "Authorization", "Bearer " + getToken())
                ),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withReqDataPostProcessing(cityFromRequest())
                .apply(reqs);
    }

    private String getToken() {
        return Try.of(() -> {
            GETConnector connector = new GETConnector("https://livresidential.nl/en/properties");
            String doc = connector.connect(0);
            Document document = Jsoup.parse(doc);
            return document.getElementsByAttribute("api-key").attr("api-key");
        }).onFailure(exc -> log.error("Could not get token", exc)).getOrElse(() -> "");
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLHousingRequest nlHousingRequest)
                && nlHousingRequest.getSites().contains(NLHousingSite.LIVRESIDENTIAL);
    }

    private String generatePostStatement(NLHousingRequest request) {
        return """
                {"q": "$CITY", "facets":["bedrooms","city","price","type"], "attributesToHighlight":["*"],
                "highlightPreTag":"__ais-highlight__","highlightPostTag":"__/ais-highlight__",
                "limit":201,"offset":$PAGE_NUM,"filter":["price>=$LOWEST_PRICE"$HIGHEST_PRICE]}
                """.replace("$CITY", parseCityLowerCased().apply(request.getCity()))
                .replace("$LOWEST_PRICE", String.valueOf(request.getLowestPrice()))
                .replace("$HIGHEST_PRICE", getHighestPrice(request.getHighestPrice()));
    }

    private String getHighestPrice(Integer price) {
        if (price == null) return "";
        return String.format(", \"price<=%d\"", price);
    }
}
