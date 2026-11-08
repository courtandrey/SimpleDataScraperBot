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
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob.BookingParser;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class BookingScraper implements Scraper<JobOffering> {
    private static final String LOCATION = "Netherlands";

    private static final Map<String, String> HEADERS = Map.of(
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
            "Accept", "application/json");

    private final BookingParser parser = new BookingParser();

    @Override
    public List<Pair<Request, Processee<JobOffering>>> scrap(List<Request> reqs) {
        return new PageScrapingFunction<>(
                req -> new GETConnector(createUrl(req), HEADERS,
                        (context, url) -> url.replace("$PAGE_NUM", String.valueOf(context.getCurrentPage()))),
                parser::parsePage,
                NLJobRequest.class
        )
                .withFilteringCondition((req, offering) -> offering.getCity() != null
                        && offering.getCity().toLowerCase().contains(LOCATION.toLowerCase()))
                .apply(reqs);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return (request instanceof NLJobRequest nlJobRequest) && nlJobRequest.getSites().contains(NLJobSite.BOOKING);
    }

    private String createUrl(NLJobRequest request) {
        String template = "https://jobs.booking.com/api/jobs?keywords=$KEYWORD&location=$LOCATION&woe=12&regionCode=NL" +
                "&sortBy=relevance&page=$PAGE_NUM&internal=false" +
                "&tags1=Booking.com%20Company%20Hierarchy%7CTransport%20Company%20Hierarchy%7CBooking%20Holdings%20Company%20Hierarchy";
        template = template.replace("$LOCATION", LOCATION);
        return template.replace("$KEYWORD", ofNullable(request.getKeyword()).map(keyword -> keyword.replace(" ", "+")).orElse(""));
    }
}
