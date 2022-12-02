package com.github.courtandrey.simpledatascraperbot.entity.request;

import org.springframework.stereotype.Component;

@Component
public class URLCreator {
    public String getURL(Request request) {
        String starter = null;
        if (request instanceof  HHVacancyRequest hhVacancyRequest) {
            starter = "https://hh.ru/search/vacancy?search_field=name&search_field=company_name&search_field=description&text=${TEXT}&from=suggest_post&page=%d&hhtmFrom=vacancy_search_list";
            if (hhVacancyRequest.getExperience() != null) {
                switch (hhVacancyRequest.getExperience()) {
                    case NO -> starter += "&experience=noExperience";
                    case MORE_THAN_6 -> starter += "&experience=moreThan6";
                    case BETWEEN_1_AND_3 -> starter += "&experience=between1And3";
                    case BETWEEN_3_AND_6 -> starter += "&experience=between3And6";
                }
            }
            if (hhVacancyRequest.isRemote()) {
                starter += "&schedule=remote";
            }
            if (hhVacancyRequest.getSearchText() == null) throw  new UnsupportedOperationException("Cannot look for job without search string.");
            starter = starter.replace("${TEXT}", hhVacancyRequest.getSearchText());
        } else if (request instanceof HabrCareerVacancyRequest habrCareerVacancyRequest) {
            if (habrCareerVacancyRequest.getSkill() == null || habrCareerVacancyRequest.getLevel() == null)
                throw new UnsupportedOperationException("Unsupported request");
            starter = "https://career.habr.com/vacancies?page=%d&qid=${LEVEL}&skills[]=${SKILL}&type=all";
            if (habrCareerVacancyRequest.isRemote()) {
                starter += "&remote=true";
            }

            switch (habrCareerVacancyRequest.getLevel()) {
                case INTERN -> starter = starter.replace("${LEVEL}", "1");
                case JUNIOR -> starter = starter.replace("${LEVEL}", "3");
                case MIDDLE -> starter = starter.replace("${LEVEL}", "4");
                case SENIOR -> starter = starter.replace("${LEVEL}", "5");
                case LEAD -> starter = starter.replace("${LEVEL}", "6");
            }

            starter = starter.replace("${SKILL}", String.valueOf(habrCareerVacancyRequest.getSkill()));
        }
        if (starter == null) throw new UnsupportedOperationException("Unknown Request");
        return starter;
    }
}
