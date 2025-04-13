package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.LatvijasPastsStatus;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LatvijasPastsRepository extends DataRepository<LatvijasPastsStatus> {

    @Query("SELECT s FROM LatvijasPastsStatus s JOIN RequestToData rtd ON rtd.data = s WHERE s.referenceNumber IN :referenceNumbers AND rtd.request.id = :requestId")
    List<LatvijasPastsStatus> getStatusesForReferenceNumber(@Param("referenceNumbers") List<String> refs, @Param("requestId") Long requestId);

}
