package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import com.github.courtandrey.simpledatascraperbot.entity.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository repository;

    public List<Movie> getMovies(List<String> urls, Long requestId) {
        return repository.getMoviesForUrls(urls, requestId);
    }
}
