package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Region;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.utility.RequestMapper;
import org.springframework.stereotype.Component;

@Component
public class URLCreator {
    public String getURL(Request request) {
        StringBuilder starter;
        if (request instanceof  HHVacancyRequest hhVacancyRequest) {
            starter = new StringBuilder("https://hh.ru/search/vacancy?search_field=name&search_field=company_name&search_field=description&text=${TEXT}&from=suggest_post&page=%d&hhtmFrom=vacancy_search_list&items_on_page=20");
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
            if (hhVacancyRequest.getRegions().size() != 0) {
                for (Region r:hhVacancyRequest.getRegions()) {
                    starter.append("&area=").append(RequestMapper.mapRegionToHHInteger(r));
                }
            }

            if (hhVacancyRequest.getSearchText() == null) throw  new UnsupportedOperationException("Cannot look for job without search string.");

            return starter.toString().replace("${TEXT}", hhVacancyRequest.getSearchText());
        }

        else if (request instanceof HabrCareerVacancyRequest habrCareerVacancyRequest) {
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
