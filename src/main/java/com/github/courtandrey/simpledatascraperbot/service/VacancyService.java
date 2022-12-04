package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
@Transactional
public class VacancyService {
    private VacancyRepository repository;
    @Autowired
    public VacancyService(VacancyRepository repository) {
        this.repository = repository;
    }

    public Optional<Vacancy> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    public Vacancy save(Vacancy vacancy) {
        return repository.save(vacancy);
    }


}
