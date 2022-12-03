package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RepositoryFactory {
    private final VacancyRepository vacancyRepository;
    @Autowired
    public RepositoryFactory(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    public Collection<Data> update(List<Data> data, User user) {
        Collection<Data> uniqueData = new ArrayList<>();
        for (Data d:data) {
            d.setUser(user);
            if (d instanceof Vacancy vacancy) {
                if (vacancyRepository.findByUrl(vacancy.getUrl()).isEmpty()) {
                    uniqueData.add(vacancy);
                    vacancyRepository.save(vacancy);
                }
            }
        }
        return uniqueData;
    }
}
