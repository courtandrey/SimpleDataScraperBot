package com.github.courtandrey.simpledatascraperbot.entity.repository;

import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends DataRepository<Movie> {
    @Query("SELECT s FROM Movie s JOIN RequestToData rtd ON rtd.data = s WHERE s.url IN :urls AND rtd.request.id = :requestId")
    List<Movie> getMoviesForUrls(@Param("urls") List<String> urls, @Param("requestId") Long requestId);
}
