package com.github.courtandrey.simpledatascraperbot.scraper.core;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.scraper.connector.Connector;
import com.github.courtandrey.simpledatascraperbot.scraper.parser.VacancyParser;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class VacancyScraper implements Scraper<Vacancy> {
    protected Connector connector;
    protected VacancyParser parser;
    protected String[] urls;
    protected int startPageNum;


    public abstract List<Vacancy> scrap();

    protected List<Vacancy> iterateUrls() {
        List<Vacancy> vacancies = new ArrayList<>();
        for (String url:urls) {
            connector = new Connector(url);
            vacancies.addAll(iteratePages());
        }
        return vacancies;
    }

    protected List<Vacancy> iteratePages() {
        int pageNum = startPageNum;
        List<Vacancy> vacancies = new ArrayList<>();
        List<Vacancy> newVacancies;
        do {
            newVacancies = scrapPage(pageNum);
            if (newVacancies == null) break;
            ++pageNum;
        } while (vacancies.addAll(newVacancies));
        return vacancies;
    }

    protected List<Vacancy> scrapPage(int pageNum) {
        Document hhDocument = connector.connect(pageNum);
        if (hhDocument == null) return null;
        return parser.parse(hhDocument);
    }

    @Override
    public String toString() {
        return this.getClass().toString();
    }
}
