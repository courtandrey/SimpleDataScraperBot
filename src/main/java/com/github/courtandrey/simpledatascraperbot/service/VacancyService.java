package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VacancyService {
    private final VacancyRepository repository;
    @Autowired
    public VacancyService(VacancyRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public Optional<Vacancy> findByUrl(String url) {
        return repository.findByUrl(url);
    }
    @Transactional
    public Vacancy save(Vacancy vacancy) {
        return repository.save(vacancy);
    }
    @Transactional
    public Optional<Vacancy> addIfEmpty(Vacancy vacancy) {
        if (repository.findByUrl(vacancy.getUrl()).isEmpty()) {
            return Optional.of(repository.save(vacancy));
        }
        return Optional.empty();
    }
}
