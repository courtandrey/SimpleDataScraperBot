package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HabrCareerParser extends VacancyParser {

    @Override
    public List<Vacancy> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements vacancies = document.getElementsByClass("vacancy-card");
        List<Vacancy> vcs = new ArrayList<>();
        for (Element vacancy:vacancies) {
            Vacancy vc = new Vacancy();
            Elements names = vacancy.getElementsByClass("vacancy-card__title-link");
            vc.setName(names.size() > 0 ? names.get(0).text() : null);
            vc.setUrl(names.size() > 0 ? "https://career.habr.com" + names.get(0).attr("href") : null);
            Elements salaries = vacancy.getElementsByClass("basic-salary");
            vc.setSalary(salaries.size() > 0 ? salaries.get(0).text() : null);
            vcs.add(vc);
        }
        return vcs;
    }
}
