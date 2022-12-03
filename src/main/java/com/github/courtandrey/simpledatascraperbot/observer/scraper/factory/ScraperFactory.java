package com.github.courtandrey.simpledatascraperbot.observer.scraper.factory;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.URLCreator;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.ScraperConfig;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Scope(scopeName = "prototype")
public class ScraperFactory {
    @Autowired
    private URLCreator creator;
    private static final Logger logger = LoggerFactory.getLogger(ScraperFactory.class);
    private final List<Scraper<? extends Data>> scrapers = new ArrayList<>();
    @Autowired
    public ScraperFactory(ScraperConfig scraperConfiguration) {
        scrapers.addAll(scraperConfiguration.getScrapers());
    }

    public List<Data> scrap(Set<Request> urls) {
        List<Data> data = new ArrayList<>();
        scrapers.forEach(x -> {
            List<String> searchStrings = urls
                    .stream().filter(x::rightScraperToRequest)
                    .map(creator::getURL)
                    .toList();
            data.addAll(x.scrap(searchStrings));
            logger.info(x + " scraped.");
        });
        return data;
    }
}
