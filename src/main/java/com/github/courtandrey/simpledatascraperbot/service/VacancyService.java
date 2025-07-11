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

    public List<Vacancy> getVacanciesWithUrlsContainingIn(List<String> urls, Long requestId) {
        return repository.getVacanciesWithUrlsContainingIn(urls, requestId);
    }
}
