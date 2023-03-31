package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class VacancyParser implements Parser<Vacancy>{

    @Override
    public abstract List<Vacancy> parse(Document document);
}
