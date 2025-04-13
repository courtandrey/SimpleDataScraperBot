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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.InterhouseParser;

import java.util.List;
import java.util.Map;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.cityFromRequest;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.NLHousingFunctions.parseCity;
import static java.util.Optional.ofNullable;

public class InterhouseScraper implements Scraper<RentalOffering> {
    private final InterhouseParser interhouseParser = new InterhouseParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector("https://interhouse.nl/wp-admin/admin-ajax.php",
                        getPostStatement(req), (context, post) -> post.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage())),
                        Map.of("Content-Type", "application/x-www-form-urlencoded")),
                interhouseParser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withReqDataPostProcessing(cityFromRequest())
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.INTERHOUSE);
    }

    private String getPostStatement(NLHousingRequest request) {
        return "action=building_results_action&" +
                "query=%3Flocation_id%3D" + parseCity().apply(request.getCity()) + "_Algemeen" +
                "%26minimum_price%3D" + request.getLowestPrice() +
                getHighestPrice(request) +
                "%26number_of_results%3D20" +
                "%26sort%3Ddate-desc" +
                "%26display%3Dlist" +
                "%26paging%3D$PAGE_NUM" +
                "%26language%3Den_GB";
    }

    private String getHighestPrice(NLHousingRequest request) {
        return ofNullable(request.getHighestPrice())
                .map(price -> "%26maximum_price%3D" + price)
                .orElse("");
    }
}
