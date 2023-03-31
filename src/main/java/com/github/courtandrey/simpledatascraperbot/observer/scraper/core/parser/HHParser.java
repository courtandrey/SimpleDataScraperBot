package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
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
            Elements towns = vacancy.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address");
            vc.setTown(towns.size() > 0 ? towns.get(0).text() : null);
            Elements company = vacancy.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer");
            vc.setCompany(company.size() > 0 ? company.get(0).text() : null);
            vcs.add(vc);
        }
        return vcs;
    }

    @Override
    public Vacancy parseExtra(Document document, Vacancy vacancy) {
        Elements dates = document.getElementsByClass("vacancy-creation-time-redesigned");
        vacancy.setDate(dates.size() > 0 ? dates.get(0).text() : null);
        Elements texts = document.getElementsByAttributeValue("data-qa", "vacancy-description");
        vacancy.setText(texts.size() > 0 ? texts.get(0).text().split("опубликована ")[1].split(" в ")[0] : null);
        return vacancy;
    }

    private String unifyURL(String href) {
        return href.split("\\?")[0];
    }
}
