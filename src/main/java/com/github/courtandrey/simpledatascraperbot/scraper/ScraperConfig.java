package com.github.courtandrey.simpledatascraperbot.scraper;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.scraper.core.HHScraper;
import com.github.courtandrey.simpledatascraperbot.scraper.core.HabrCareerScraper;
import com.github.courtandrey.simpledatascraperbot.scraper.core.Scraper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ScraperConfig {
    @Bean
    public HabrCareerScraper habrCareerScraper() {
        return new HabrCareerScraper();
    }
    @Bean
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

