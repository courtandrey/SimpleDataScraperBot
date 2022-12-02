package com.github.courtandrey.simpledatascraperbot.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class HabrCareerVacancyRequest extends VacancyRequest {
    public enum Level {
        INTERN,
        JUNIOR,
        MIDDLE,
        SENIOR,
        LEAD
    }

    @Column(nullable = false)
    private Integer skill;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabrCareerVacancyRequest that = (HabrCareerVacancyRequest) o;
        return Objects.equals(skill, that.skill) && level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skill, level);
    }
}
