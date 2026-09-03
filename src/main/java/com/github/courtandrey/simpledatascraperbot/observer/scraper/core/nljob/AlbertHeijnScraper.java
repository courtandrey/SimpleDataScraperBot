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
            "X-Requested-With", "XMLHttpRequest",
            "Accept", "application/json, text/javascript, */*; q=0.01",
            "Accept-Language", "nl-NL,nl;q=0.9,en;q=0.8",
            "Referer", "https://werk.ah.nl/vacatures");

    private final AlbertHeijnParser parser = new AlbertHeijnParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req), HEADERS),
                parser::parsePage,
                NLJobRequest.class
        )
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.ALBERT_HEIJN);
    }

    private String createUrl(NLJobRequest request) {
        String template = "https://werk.ah.nl/en/api/vacancy/?search=$KEYWORD&sort=created&sortDir=DESC";
        return template.replace("$KEYWORD", ofNullable(request.getKeyword()).map(keyword -> keyword.replace(" ", "+")).orElse(""));
    }
}
