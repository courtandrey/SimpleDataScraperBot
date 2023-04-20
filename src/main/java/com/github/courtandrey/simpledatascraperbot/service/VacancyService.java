package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepository repository;
    @Autowired
    public VacancyService(VacancyRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public Vacancy save(Vacancy vacancy) {
        return repository.save(vacancy);
    }
    @Transactional
    public Optional<List<Vacancy>> getVacanciesWithUrlsContainingIn(List<String> urls) {
        List<Vacancy> vacancy;
        if (!(vacancy = repository.getVacanciesWithUrlsContainingIn(urls)).isEmpty()) {
            return Optional.of(vacancy);
        }
        return Optional.empty();
    }
}
