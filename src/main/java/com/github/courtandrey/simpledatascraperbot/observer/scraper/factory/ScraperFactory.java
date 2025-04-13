package com.github.courtandrey.simpledatascraperbot.observer.scraper.factory;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.ScraperConfig;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ScraperFactory {
    private static final Logger logger = LoggerFactory.getLogger(ScraperFactory.class);
    private final List<Scraper<? extends Data>> scrapers = new ArrayList<>();

    @Autowired
    public ScraperFactory(ScraperConfig scraperConfiguration) {
        scrapers.addAll(scraperConfiguration.getScrapers());
    }

    @SuppressWarnings("unchecked")
    public List<Pair<Request, Processee<Data>>> scrap(Collection<Request> reqs) {
        return scrapers.parallelStream().map(scraper -> {
            List<Request> requests = reqs
                    .stream().filter(scraper::rightScraperToRequest)
                    .toList();
            List<Pair<Request, Processee<Data>>> processees = scraper.scrap(requests).stream()
                    .map(item -> item.mapSecond(processee -> (Processee<Data>) processee))
                    .toList();
            logger.info("{} scraped", scraper.getClass().getSimpleName());
            return processees;
        }).flatMap(Collection::stream).toList();
    }
}
