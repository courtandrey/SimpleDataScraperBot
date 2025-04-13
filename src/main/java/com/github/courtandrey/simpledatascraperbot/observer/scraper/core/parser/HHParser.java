package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HHParser extends VacancyParser{
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(HHParser.class);

    @Override
    @SuppressWarnings(value = "all")
    public List<Vacancy> parsePage(String docToParse) {
        try {
            HashMap<String, Object> map = mapper.readValue(docToParse, HashMap.class);
            List<HashMap<String, Object>> vacancies = (List<HashMap<String, Object>>) map.get("items");
            List<Vacancy> vcs = new ArrayList<>();
            for (HashMap<String,Object> vacancy:vacancies) {
                Vacancy vc = new Vacancy();
                vc.setName((String) vacancy.get("name"));
                vc.setUrl((String) vacancy.get("alternate_url"));
                HashMap<String,Object> salary = (HashMap<String, Object>) vacancy.get("salary");
                vc.setSalary(getSalaryString(salary));
                HashMap<String,Object> area = (HashMap<String, Object>) vacancy.get("area");
                vc.setTown((String) area.get("name"));
                HashMap<String,Object> employer = (HashMap<String, Object>) vacancy.get("employer");
                vc.setCompany((String) employer.get("name"));
                vc.setDate(((String) vacancy.get("published_at")).split("T")[0]);
                vcs.add(vc);
            }
            return vcs;
        } catch (JsonProcessingException e) {
            logger.error("JSON malformed: " + e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    private String getSalaryString(HashMap<String,Object> salary) {
        if (salary == null) return null;
        StringBuilder builder = new StringBuilder();
        if (salary.get("from") != null) {
            builder.append("from ").append(salary.get("from"));
        }
        if (salary.get("to") != null) {
            builder.append(" to ").append(salary.get("to"));
        }
        builder.append(" ").append(salary.get("currency"));
        return builder.toString();
    }

    @Override
    @SuppressWarnings(value = "all")
    public void addDetails(String docToParse, Vacancy vacancy) {
        try {
            HashMap<String,Object> map = mapper.readValue(docToParse, HashMap.class);
            vacancy.setText((String) map.get("description"));
        }
        catch (JsonProcessingException e) {
            logger.error("JSON malformed: " + e.getLocalizedMessage());
        }
    }
}
