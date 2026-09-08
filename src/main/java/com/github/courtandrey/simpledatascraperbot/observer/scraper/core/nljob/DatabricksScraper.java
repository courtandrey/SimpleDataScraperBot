package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nljob;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob.DatabricksParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class DatabricksScraper implements Scraper<JobOffering> {
    private static final String LOCATION = "Netherlands";

    private static final Map<String, String> HEADERS = Map.of(
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
            "Accept", "application/json");

    private final DatabricksParser parser = new DatabricksParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req), HEADERS),
                parser::parsePage,
                NLJobRequest.class
        )
                .withFilteringCondition((req, offering) -> offering.getCity().toLowerCase().contains(LOCATION.toLowerCase()))
                .withFilteringCondition((req, offering) -> ofNullable(req.getKeyword())
                        .map(keyword -> offering.getName().toLowerCase().contains(keyword.toLowerCase())).orElse(true))
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.DATABRICKS);
    }

    private String createUrl(NLJobRequest request) {
        String template = "https://www.databricks.com/careers-assets/page-data/company/careers/open-positions/page-data.json" +
                "?department=all&location=$LOCATION&search=$KEYWORD";
        template = template.replace("$LOCATION", LOCATION);
        return template.replace("$KEYWORD", ofNullable(request.getKeyword()).map(keyword -> keyword.replace(" ", "+")).orElse(""));
    }
}
