package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.vacancy;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.Region;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.HHParser;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode;
import com.github.courtandrey.simpledatascraperbot.utility.RequestMapper;

import java.util.List;

public class HHScraper extends VacancyScraper {

    public HHScraper(ParsingMode parsingMode) {
        super(parsingMode);
        parser = new HHParser();
        startPageNum = 0;
    }

    @Override
    public List<Pair<Request, Processee<Vacancy>>> scrap(List<Request> requests) {
        this.requests = requests;
        return iterateUrls(0);
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof HHVacancyRequest;
    }

    @Override
    protected String createUrl(Request request) {
        StringBuilder starter;
        if (request instanceof  HHVacancyRequest hhVacancyRequest) {
            starter = new StringBuilder("https://api.hh.ru/vacancies?text=${TEXT}&page=%d&per_page=100");
            if (hhVacancyRequest.getExperience() != null) {
                switch (hhVacancyRequest.getExperience()) {
                    case NO -> starter.append("&experience=noExperience");
                    case MORE_THAN_6 -> starter.append("&experience=moreThan6");
                    case BETWEEN_1_AND_3 -> starter.append("&experience=between1And3");
                    case BETWEEN_3_AND_6 -> starter.append("&experience=between3And6");
                }
            }
            if (hhVacancyRequest.isRemote()) {
                starter.append("&schedule=remote");
            }
            if (!hhVacancyRequest.getRegions().isEmpty()) {
                for (Region r:hhVacancyRequest.getRegions()) {
                    starter.append("&area=").append(RequestMapper.mapRegionToHHInteger(r));
                }
            }

            if (hhVacancyRequest.getSearchText() == null) throw  new UnsupportedOperationException("Cannot look for job without search string.");

            return starter.toString().replace("${TEXT}", hhVacancyRequest.getSearchText().replace(" ","+"));
        }

        throw new UnsupportedOperationException("Unknown Request");
    }

}
