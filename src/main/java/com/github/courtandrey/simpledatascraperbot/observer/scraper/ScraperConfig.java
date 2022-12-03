package com.github.courtandrey.simpledatascraperbot.observer.scraper;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.HHScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.HabrCareerScraper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ScraperConfig {
    @Bean
    @Scope(scopeName = "prototype")
    public HabrCareerScraper habrCareerScraper() {
        return new HabrCareerScraper();
    }
    @Bean
    @Scope(scopeName = "prototype")
    public HHScraper hhScraper() {
        return new HHScraper();
    }

    public List<Scraper<? extends Data>> getScrapers() {
        List<Scraper<? extends Data>> scrapers = new ArrayList<>();
        scrapers.add(hhScraper());
        scrapers.add(habrCareerScraper());
        return scrapers;
    }

}

