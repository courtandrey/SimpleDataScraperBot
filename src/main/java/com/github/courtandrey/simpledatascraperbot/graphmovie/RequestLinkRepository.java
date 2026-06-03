package com.github.courtandrey.simpledatascraperbot.graphmovie;

import com.github.courtandrey.simpledatascraperbot.entity.request.RequestToData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RequestLinkRepository extends JpaRepository<RequestToData, Long> {

    @Query("""
           select rtd from RequestToData rtd
           join fetch rtd.request r
           left join fetch r.user
           where rtd.data.id in :movieIds
           """)
    List<RequestToData> findWithRequestAndUserByMovieIds(@Param("movieIds") Collection<Long> movieIds);
}
