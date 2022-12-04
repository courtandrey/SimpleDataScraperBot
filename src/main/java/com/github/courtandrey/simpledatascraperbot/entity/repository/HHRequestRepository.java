package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

@Repository
public interface HHRequestRepository extends RequestRepository<HHVacancyRequest>  {
    Streamable<Request> findByUserUserId(Long userId);
}
