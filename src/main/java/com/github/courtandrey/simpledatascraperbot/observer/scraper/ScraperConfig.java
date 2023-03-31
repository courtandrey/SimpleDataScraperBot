package com.github.courtandrey.simpledatascraperbot.observer.scraper;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.HHScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.HabrCareerScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode.EXTRA;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode.MAIN;

@Configuration
public class ScraperConfig {
    @Value("${app.parsing-mode}")
    private String parsingMode;
    @Bean
    @Scope(scopeName = "prototype")
    public HabrCareerScraper habrCareerScraper() {
        if (parsingMode.equals("extra")) {
            return new HabrCareerScraper(EXTRA);
        }
        return new HabrCareerScraper(MAIN);
    }
    @Bean
    @Scope(scopeName = "prototype")
    public HHScraper hhScraper() {
        if (parsingMode.equals("extra")) {
            return new HHScraper(EXTRA);
        }
        return new HHScraper(MAIN);
    }

    public List<Scraper<? extends Data>> getScrapers() {
        List<Scraper<? extends Data>> scrapers = new ArrayList<>();
        scrapers.add(hhScraper());
        scrapers.add(habrCareerScraper());
        return scrapers;
    }

}

