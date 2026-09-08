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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob.GoDutchParser;

import java.util.List;

public class GoDutchScraper implements Scraper<JobOffering> {
    private static final String URL = "https://godutch.com/en/careers";

    private final GoDutchParser parser = new GoDutchParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(URL),
                parser::parsePage,
                NLJobRequest.class
        )
                .withFilteringCondition(new JobOfferingRequestSatisfied())
                .singlePage(true)
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.GO_DUTCH);
    }
}
