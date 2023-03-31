package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.VacancyParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.Connector;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class VacancyScraper implements Scraper<Vacancy> {
    protected Connector connector;
    protected VacancyParser parser;
    protected List<String> urls = new ArrayList<>();
    protected int startPageNum;

    protected ParsingMode parsingMode;

    public VacancyScraper(ParsingMode parsingMode) {
        this.parsingMode = parsingMode;
    }

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
        Document parsDoc = connector.connectPageSearch(pageNum);
        if (parsDoc == null) return null;
        List<Vacancy> vacancies = parser.parse(parsDoc);
        if (parsingMode == ParsingMode.EXTRA) {
            vacancies.forEach(v -> parser.parseExtra(connector.connect(v.getUrl()), v));
        }
        return vacancies;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
