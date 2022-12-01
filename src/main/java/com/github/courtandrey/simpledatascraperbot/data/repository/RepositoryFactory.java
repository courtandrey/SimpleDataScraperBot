package com.github.courtandrey.simpledatascraperbot.data.repository;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RepositoryFactory {
    @Autowired
    private VacancyRepository vacancyRepository;

    public Collection<Data> update(List<Data> data) {
        Collection<Data> uniqueData = new ArrayList<>();
        for (Data d:data) {
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
