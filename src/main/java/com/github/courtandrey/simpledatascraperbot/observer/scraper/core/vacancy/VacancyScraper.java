package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.vacancy;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.RequestPagingContext;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.VacancyParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETConnector;

import java.util.ArrayList;
import java.util.List;

public abstract class VacancyScraper implements Scraper<Vacancy> {
    protected GETConnector connector;
    protected VacancyParser parser;
    protected List<Request> requests = new ArrayList<>();
    protected int startPageNum;

    protected ParsingMode parsingMode;

    public VacancyScraper(ParsingMode parsingMode) {
        this.parsingMode = parsingMode;
    }

    protected List<Pair<Request, Processee<Vacancy>>> iterateUrls(Integer timeoutMillis) {
        List<Pair<Request, Vacancy>> vacancies = new ArrayList<>();
        for (Request request : requests) {
            connector = new GETConnector(createUrl(request));
            List<Vacancy> newVacancy = iteratePages(timeoutMillis);
            vacancies.addAll(newVacancy.stream().map(vac -> Pair.of(request, vac)).toList());
        }
        return vacancies.stream().map(pair -> pair.mapSecond(Processee::of)).toList();
    }

    protected List<Vacancy> iteratePages(Integer timeoutMillis) {
        int pageNum = startPageNum;
        List<Vacancy> vacancies = new ArrayList<>();
        List<Vacancy> newVacancies;
        do {
            newVacancies = scrapPage(pageNum, timeoutMillis);
            if (newVacancies == null) break;
            ++pageNum;
        } while (vacancies.addAll(newVacancies));
        return vacancies;
    }

    protected List<Vacancy> scrapPage(int pageNum, Integer timeoutMillis) {
        String docToParse = connector.connectPageSearch(RequestPagingContext.builder().currentPage(pageNum).build(),timeoutMillis);
        if (docToParse == null) return null;
        List<Vacancy> vacancies = parser.parsePage(docToParse);
        if (parsingMode == ParsingMode.EXTRA) {
            vacancies.forEach(v -> parser.addDetails(connector.connect(
                    v.getUrl()
                    .replace("hh.ru","api.hh.ru")
                    .replace("vacancy", "vacancies"),
                    timeoutMillis), v));
        }
        return vacancies;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected abstract String createUrl(Request request);
}
