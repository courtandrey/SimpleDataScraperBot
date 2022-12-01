package com.github.courtandrey.simpledatascraperbot.data.repository;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacancyRepository extends DataRepository<Vacancy> {
    Optional<Vacancy> findByUrl(String url);
}
