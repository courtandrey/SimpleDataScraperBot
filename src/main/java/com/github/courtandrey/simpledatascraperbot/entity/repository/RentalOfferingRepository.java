package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RentalOfferingRepository extends DataRepository<RentalOffering> {
    @Query("SELECT r FROM RentalOffering r JOIN RequestToData rtd ON rtd.data = r WHERE r.url IN (:urls) AND rtd.request.id = :requestId")
    List<RentalOffering> getOfferingsWithUrlsContainingIn(@Param("urls") List<String> names, @Param("requestId") Long requestId);
}
