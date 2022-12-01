package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.parser.HabrCareerParser;

import java.util.List;

public class HabrCareerScraper extends VacancyScraper {
    public HabrCareerScraper() {
        parser = new HabrCareerParser();
        urls = new String[] {
                "https://career.habr.com/vacancies?page=%d&qid=1&remote=true&skills[]=1012&type=all",
                "https://career.habr.com/vacancies?page=%d&qid=3&remote=true&skills[]=1012&type=all",
                "https://career.habr.com/vacancies?page=%d&qid=4&remote=true&skills[]=1012&type=all"
        };
        startPageNum = 1;
    }

    @Override
    public List<Vacancy> scrap() {
        return iterateUrls();
    }
}
