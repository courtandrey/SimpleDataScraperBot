package com.github.courtandrey.simpledatascraperbot.observer.scraper.factory;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.ScraperConfig;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Scope(scopeName = "prototype")
public class ScraperFactory {
    private static final Logger logger = LoggerFactory.getLogger(ScraperFactory.class);
    private final List<Scraper<? extends Data>> scrapers = new ArrayList<>();
    @Autowired
    public ScraperFactory(ScraperConfig scraperConfiguration) {
        scrapers.addAll(scraperConfiguration.getScrapers());
    }

    public List<Data> scrap() {
        List<Data> vacancies = new ArrayList<>();
        scrapers.forEach(x -> {
            vacancies.addAll(x.scrap());
            logger.info(x + " scraped.");
        });
        return vacancies;
    }
}
