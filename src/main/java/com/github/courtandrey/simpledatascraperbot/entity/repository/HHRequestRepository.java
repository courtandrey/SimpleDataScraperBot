package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface HHRequestRepository extends RequestRepository<HHVacancyRequest>  {
    Streamable<HHVacancyRequest> findByUserUserId(Long userId);
}
