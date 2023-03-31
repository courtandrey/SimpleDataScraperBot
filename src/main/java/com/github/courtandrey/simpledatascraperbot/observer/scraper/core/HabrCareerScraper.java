package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.HabrCareerParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;

import java.util.ArrayList;
import java.util.List;

public class HabrCareerScraper extends VacancyScraper {
    public HabrCareerScraper(ParsingMode parsingMode) {
        super(parsingMode);
        parser = new HabrCareerParser();
        startPageNum = 1;
    }

    @Override
    public List<Vacancy> scrap(List<String> urls) {
        this.urls = new ArrayList<>(urls);
        return iterateUrls();
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof HabrCareerVacancyRequest;
    }
}
