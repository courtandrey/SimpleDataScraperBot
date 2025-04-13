package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.Vacancy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends DataRepository<Vacancy> {
    @Query("SELECT v FROM Vacancy v JOIN RequestToData rtd ON rtd.data = v WHERE v.url IN (:urls) AND rtd.request.id = :requestId")
    List<Vacancy> getVacanciesWithUrlsContainingIn(@Param("urls") List<String> names, @Param("requestId") Long requestId);
}
