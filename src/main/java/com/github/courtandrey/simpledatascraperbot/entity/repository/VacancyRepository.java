package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacancyRepository extends DataRepository<Vacancy> {
    Optional<Vacancy> findByUrl(String url);
}
