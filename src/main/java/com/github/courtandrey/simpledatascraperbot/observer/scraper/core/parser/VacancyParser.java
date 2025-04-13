package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;

import java.util.List;

public abstract class VacancyParser implements Parser<Vacancy>{

    @Override
    public abstract List<Vacancy> parsePage(String docToParse);
}
