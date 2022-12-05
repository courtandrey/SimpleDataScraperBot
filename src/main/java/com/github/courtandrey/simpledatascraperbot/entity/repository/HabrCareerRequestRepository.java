package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface HabrCareerRequestRepository extends RequestRepository<HabrCareerVacancyRequest>{
    Streamable<HabrCareerVacancyRequest> findByUserUserId(Long userId);
}
