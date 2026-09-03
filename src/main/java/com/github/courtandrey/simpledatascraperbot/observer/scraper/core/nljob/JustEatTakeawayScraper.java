package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nljob;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.POSTConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob.JustEatTakeawayParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class JustEatTakeawayScraper implements Scraper<JobOffering> {
    private static final int PAGE_SIZE = 10;

    private final JustEatTakeawayParser parser = new JustEatTakeawayParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new POSTConnector("https://careers.justeattakeaway.com/widgets",
                        createPostStatement(req),
                        (context, post) -> post.replace("$FROM", String.valueOf((context.getCurrentPage() - 1) * PAGE_SIZE)),
                        Map.of("Content-Type", "application/json")),
                parser::parsePage,
                NLJobRequest.class
        )
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.JUST_EAT_TAKEAWAY);
    }

    private String createPostStatement(NLJobRequest request) {
        String postStatement = """
                {"sortBy":"","subsearch":"","from":$FROM,"jobs":true,"counts":true,
                "all_fields":["category","country","city","type"],"pageName":"search-results","size":$SIZE,
                "clearAll":false,"jdsource":"facets","isSliderEnable":false,"siteType":"external","keywords":"$KEYWORD",
                "global":true,"selected_fields":{"country":["Netherlands"]},"lang":"nl_nl","deviceType":"desktop",
                "country":"nl","refNum":"TAKEGLOBAL","ddoKey":"refineSearch"}
                """;
        postStatement = postStatement.replace("$SIZE", String.valueOf(PAGE_SIZE));
        postStatement = postStatement.replace("$KEYWORD", ofNullable(request.getKeyword()).orElse(""));
        return postStatement;
    }
}
