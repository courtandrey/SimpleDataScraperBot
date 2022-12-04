package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RepositoryFactory {
    private final VacancyService vacancyService;
    @Autowired
    public RepositoryFactory(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    public Collection<Data> update(List<Data> data, User user) {
        Collection<Data> uniqueData = new ArrayList<>();
        for (Data d:data) {
            d.setUser(user);
            if (d instanceof Vacancy vacancy) {
                vacancyService.addIfEmpty(vacancy).ifPresent(uniqueData::add);
            }
        }
        return uniqueData;
    }
}
