package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import com.github.courtandrey.simpledatascraperbot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class DataUpdater {
    private final VacancyService vacancyService;
    private final UserService userService;
    @Autowired
    public DataUpdater(VacancyService vacancyService, UserService userService) {
        this.userService=userService;
        this.vacancyService = vacancyService;
    }
    @Transactional
    public Collection<Data> update(List<Data> data, Long userId) {
        Collection<Data> uniqueData = new HashSet<>();
        User user = userService.getReferenceById(userId);
        List<Vacancy> vacancies = new ArrayList<>();
        for (Data d:data) {
            d.setUser(user);
            if (d instanceof Vacancy vacancy) {
                vacancies.add(vacancy);
            }
        }
        List<String> urls = vacancies.stream().map(Vacancy::getUrl).toList();
        List<Vacancy> oldVacancies = new ArrayList<>();
        int batch = 500;
        for (int start = 0; start < urls.size(); start += batch) {
            int end = Math.min(start+batch,urls.size());
            List<String> subUrls = urls.subList(start, end);
            oldVacancies.addAll(vacancyService.getVacanciesWithUrlsContainingIn(subUrls).orElse(new ArrayList<>()));
        }
        for (Vacancy vacancy:vacancies) {
            if (!oldVacancies.contains(vacancy)) {
                uniqueData.add(vacancy);
                vacancyService.save(vacancy);
            }
        }
        return uniqueData;
    }
}
