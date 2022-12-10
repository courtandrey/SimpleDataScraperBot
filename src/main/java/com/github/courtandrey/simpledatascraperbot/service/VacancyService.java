package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import com.github.courtandrey.simpledatascraperbot.entity.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class VacancyService {
    private VacancyRepository repository;
    @Autowired
    private UserService userService;
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
    @Transactional
    public Optional<Vacancy> addIfEmpty(Vacancy vacancy) {
        if (repository.findByUrl(vacancy.getUrl()).isEmpty()) {
            return Optional.of(repository.save(vacancy));
        }
        return Optional.empty();
    }

    @Transactional
    public Collection<Data> addIfEmptyForUser(List<Data> data, Long userId) {
        Collection<Data> uniqueData = new HashSet<>();
        for (Data d:data) {
            d.setUser(userService.getReferenceById(userId));
            if (d instanceof Vacancy vacancy) {
                addIfEmpty(vacancy).ifPresent(uniqueData::add);
            }
        }
        return uniqueData;
    }
}
