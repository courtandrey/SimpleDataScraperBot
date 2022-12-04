package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import com.github.courtandrey.simpledatascraperbot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RepositoryFactory {
    private final VacancyService vacancyService;
    private final UserService userService;
    @Autowired
    public RepositoryFactory(VacancyService vacancyService, UserService userService) {
        this.userService=userService;
        this.vacancyService = vacancyService;
    }

    public Collection<Data> update(List<Data> data, Long userId) {
        Collection<Data> uniqueData = new ArrayList<>();
        for (Data d:data) {
            d.setUser(userService.getReferenceById(userId));
            if (d instanceof Vacancy vacancy) {
                vacancyService.addIfEmpty(vacancy).ifPresent(uniqueData::add);
            }
        }
        return uniqueData;
    }
}
