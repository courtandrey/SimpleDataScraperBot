package com.github.courtandrey.simpledatascraperbot.entity.request.movie;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

@Entity
@Getter
@Setter
public class IMDBRequest extends Request {
    @Column(updatable = false)
    private String country;
    @Column(updatable = false)
    private String genre;
    @Column(nullable = false, updatable = false)
    private Integer minVotes;
    @Column(updatable = false)
    private LocalDate releaseDate;

    @Override
    public String toString() {
        return "IMDB request tracking with country: " + ofNullable(country).orElse("none") +
                " genre: " + ofNullable(genre).orElse("none") +
                " releaseDate: " + ofNullable(releaseDate).map(LocalDate::toString).orElse("none") +
                " minVotes: " + minVotes;
    }
}
