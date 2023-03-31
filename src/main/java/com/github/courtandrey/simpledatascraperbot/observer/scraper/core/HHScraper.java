package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.HHParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;

import java.util.ArrayList;
import java.util.List;

public class HHScraper extends VacancyScraper {
    public HHScraper(ParsingMode parsingMode) {
        super(parsingMode);
        parser = new HHParser();
        startPageNum = 0;
    }

    @Override
    public List<Vacancy> scrap(List<String> urls) {
        this.urls = new ArrayList<>(urls);
        return iterateUrls();
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof HHVacancyRequest;
    }

}
