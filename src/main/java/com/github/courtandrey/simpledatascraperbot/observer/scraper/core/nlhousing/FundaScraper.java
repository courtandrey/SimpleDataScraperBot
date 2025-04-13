package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.JsoupConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.FundaParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCityLowerCased;

public class FundaScraper implements Scraper<RentalOffering> {
    private final FundaParser fundaParser = new FundaParser();

    private static final Map<String, String> HEADERS =  Map.of(
            "Host", "www.funda.nl",
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
            "Sec-Fetch-Dest", "document",
            "If-None-Match", "2618713550");

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new JsoupConnector(createUrl(req), HEADERS,
                        (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                fundaParser::parsePage,
                NLHousingRequest.class
        )
                .withDataCallback(NLHousingFunctions.visitRentalOffering(fundaParser::addDetails,
                        url -> new JsoupConnector(url, HEADERS)))
                .withReqDataPostProcessing(cityFromRequest())
                .withSendingOutCondition(new RentalOfferingRequestSatisfied())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest req && req.getSites().contains(NLHousingSite.FUNDA);
    }

    private String createUrl(NLHousingRequest nlHousingRequest) {
        String template = "https://www.funda.nl/zoeken/huur/?selected_area=%5B%22$CITY%22%5D" +
                "&price=%22$LOWEST_PRICE-$HIGHEST_PRICE%22&search_result=$PAGE_NUM";

        template = template.replace("$CITY", parseCityLowerCased().apply(nlHousingRequest.getCity()));

        String lowestPrice = String.valueOf(nlHousingRequest.getLowestPrice());
        String highestPrice = nlHousingRequest.getHighestPrice() == null ? "" : String.valueOf(nlHousingRequest.getHighestPrice());
        template = template.replace("$LOWEST_PRICE", lowestPrice);
        template = template.replace("$HIGHEST_PRICE", highestPrice);

        return template;
    }
}
