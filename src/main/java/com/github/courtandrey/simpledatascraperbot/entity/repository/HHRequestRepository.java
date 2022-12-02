package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface HHRequestRepository extends RequestRepository<HHVacancyRequest>  {
}
