package com.github.courtandrey.simpledatascraperbot.repository;

import com.github.courtandrey.simpledatascraperbot.data.Vacancy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface VacancyRepository extends DataRepository<Vacancy> {
    Optional<Vacancy> findByUrl(String url);
}
