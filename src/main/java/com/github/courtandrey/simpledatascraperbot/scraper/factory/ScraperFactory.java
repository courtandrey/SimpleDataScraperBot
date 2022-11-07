package com.github.courtandrey.simpledatascraperbot.scraper.factory;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.scraper.core.HHScraper;
import com.github.courtandrey.simpledatascraperbot.scraper.core.HabrCareerScraper;
import com.github.courtandrey.simpledatascraperbot.scraper.core.Scraper;

import java.util.ArrayList;
import java.util.List;

public class ScraperFactory {
    private final List<Scraper<? extends Data>> scrapers = new ArrayList<>();

    public ScraperFactory() {
        scrapers.add(new HHScraper());
        scrapers.add(new HabrCareerScraper());
    }

    public List<Data> scrap() {
        List<Data> vacancies = new ArrayList<>();
        scrapers.forEach(x -> vacancies.addAll(x.scrap()));
        return vacancies;
    }
}
