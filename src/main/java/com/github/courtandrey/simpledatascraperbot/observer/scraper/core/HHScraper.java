package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.parser.HHParser;

import java.util.List;

public class HHScraper extends VacancyScraper {
    public HHScraper() {
        parser = new HHParser();
        urls = new String[]{
                "https://hh.ru/search/vacancy?no_magic=true&L_save_area=true&text=Java&search_field=name&search_field=company_name&search_field=description&area=113&area=40&area=9&area=16&area=28&area=1001&area=48&area=97&salary=&currency_code=RUR&experience=between1And3&employment=full&employment=part&employment=probation&schedule=remote&order_by=publication_time&search_period=0&items_on_page=20&page=%d&hhtmFrom=vacancy_search_list",
                "https://hh.ru/search/vacancy?no_magic=true&L_save_area=true&text=Java&search_field=name&search_field=company_name&search_field=description&area=113&area=40&area=9&area=16&area=28&area=1001&area=48&area=97&salary=&currency_code=RUR&experience=noExperience&employment=full&employment=part&employment=probation&schedule=remote&order_by=publication_time&search_period=0&items_on_page=20&page=%d&hhtmFrom=vacancy_search_list",
                "https://hh.ru/search/vacancy?area=40&area=9&area=28&area=1001&area=48&area=97&employment=full&employment=part&employment=probation&experience=between1And3&search_field=name&search_field=company_name&search_field=description&text=Java&no_magic=true&L_save_area=true&order_by=publication_time&items_on_page=20&page=%d&hhtmFrom=vacancy_search_list",
                "https://hh.ru/search/vacancy?area=40&area=9&area=28&area=1001&area=48&area=97&employment=full&employment=part&employment=probation&experience=noExperience&search_field=name&search_field=company_name&search_field=description&text=Java&no_magic=true&L_save_area=true&order_by=publication_time&items_on_page=20&page=%d&hhtmFrom=vacancy_search_list",
                "https://hh.ru/search/vacancy?schedule=remote&search_field=name&search_field=company_name&search_field=description&text=Промышленный+язык+программирования&from=suggest_post&page=%d&hhtmFrom=vacancy_search_list",
                "https://hh.ru/search/vacancy?schedule=remote&search_field=name&search_field=company_name&search_field=description&text=Дата+аналитик&from=suggest_post&page=%d&hhtmFrom=vacancy_search_list"
        };
        startPageNum = 0;
    }

    @Override
    public List<Vacancy> scrap() {
        return iterateUrls();
    }

}
