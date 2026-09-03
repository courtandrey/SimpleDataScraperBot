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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob.AlbertHeijnParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class AlbertHeijnScraper implements Scraper<JobOffering> {
    private static final Map<String, String> HEADERS = Map.of(
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
            "X-Requested-With", "XMLHttpRequest");

    private final AlbertHeijnParser parser = new AlbertHeijnParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req), HEADERS,
                        (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                parser::parsePage,
                NLJobRequest.class
        )
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.ALBERT_HEIJN);
    }

    private String createUrl(NLJobRequest request) {
        String template = "https://werk.ah.nl/api/vacancy/?search=$KEYWORD&sort=created&sortDir=DESC&pageNumber=$PAGE_NUM";
        return template.replace("$KEYWORD", ofNullable(request.getKeyword()).map(keyword -> keyword.replace(" ", "+")).orElse(""));
    }
}
