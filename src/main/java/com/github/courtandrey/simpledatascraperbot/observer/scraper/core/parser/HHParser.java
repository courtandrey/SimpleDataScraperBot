package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HHParser extends VacancyParser{
    @Override
    public List<Vacancy> parse(Document document) {
        Elements vacancies = document.getElementsByClass("serp-item");
        List<Vacancy> vcs = new ArrayList<>();
        for (Element vacancy:vacancies) {
            Vacancy vc = new Vacancy();
            Elements names = vacancy.getElementsByClass("serp-item__title");
            vc.setName(names.size() > 0 ? names.get(0).text() : null);
            vc.setUrl(names.size() > 0 ? unifyURL(names.get(0).attr("href")) : null);
            Elements salaries = vacancy.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation");
            vc.setSalary(salaries.size() > 0 ? salaries.get(0).text() : null);
            vcs.add(vc);
        }
        return vcs;
    }

    private String unifyURL(String href) {
        return href.split("\\?")[0];
    }
}
