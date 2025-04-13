package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.vacancy;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.HabrCareerParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;

import java.util.List;

public class HabrCareerScraper extends VacancyScraper {

    public HabrCareerScraper(ParsingMode parsingMode) {
        super(parsingMode);
        parser = new HabrCareerParser();
        startPageNum = 1;
    }

    @Override
    public List<Pair<Request, Processee<Vacancy>>> scrap(List<Request> reqs) {
        this.requests = reqs;
        return iterateUrls(0);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof HabrCareerVacancyRequest;
    }

    @Override
    protected String createUrl(Request request) {
        StringBuilder starter;
        if (request instanceof HabrCareerVacancyRequest habrCareerVacancyRequest) {
            if (habrCareerVacancyRequest.getSkill() == null || habrCareerVacancyRequest.getLevel() == null)
                throw new UnsupportedOperationException("Unsupported request");
            starter = new StringBuilder("https://career.habr.com/vacancies?page=%d&qid=${LEVEL}&skills[]=${SKILL}&type=all");
            if (habrCareerVacancyRequest.isRemote()) {
                starter.append("&remote=true");
            }

            switch (habrCareerVacancyRequest.getLevel()) {
                case INTERN -> starter = new StringBuilder(starter.toString().replace("${LEVEL}", "1"));
                case JUNIOR -> starter = new StringBuilder(starter.toString().replace("${LEVEL}", "3"));
                case MIDDLE -> starter = new StringBuilder(starter.toString().replace("${LEVEL}", "4"));
                case SENIOR -> starter = new StringBuilder(starter.toString().replace("${LEVEL}", "5"));
                case LEAD -> starter = new StringBuilder(starter.toString().replace("${LEVEL}", "6"));
            }

            return starter.toString().replace("${SKILL}", String.valueOf(habrCareerVacancyRequest.getSkill()));
        }

        throw new UnsupportedOperationException("Unknown Request");
    }
}
