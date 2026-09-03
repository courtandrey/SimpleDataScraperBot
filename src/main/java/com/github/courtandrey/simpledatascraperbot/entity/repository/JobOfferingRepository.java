package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface JobOfferingRepository extends DataRepository<JobOffering> {
    @Query("SELECT j FROM JobOffering j JOIN RequestToData rtd ON rtd.data = j WHERE j.url IN (:urls) AND rtd.request.id = :requestId")
    List<JobOffering> getOfferingsWithUrlsContainingIn(@Param("urls") List<String> urls, @Param("requestId") Long requestId);
}
