package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface VacancyRepository extends DataRepository<Vacancy> {
    @Query("SELECT v FROM Vacancy v WHERE v.url IN (:urls)")
    List<Vacancy> getVacanciesWithUrlsContainingIn(@Param("urls") List<String> names);
}
