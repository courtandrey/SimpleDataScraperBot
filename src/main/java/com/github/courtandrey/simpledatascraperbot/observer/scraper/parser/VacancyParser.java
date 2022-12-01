package com.github.courtandrey.simpledatascraperbot.observer.scraper.parser;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class VacancyParser implements Parser{
    @Override
    public abstract List<Vacancy> parse(Document document);
}
