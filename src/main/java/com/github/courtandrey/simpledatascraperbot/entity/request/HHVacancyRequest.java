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
public class HHVacancyRequest extends VacancyRequest {
    public enum Experience {
        NO,
        BETWEEN_1_AND_3,
        BETWEEN_3_AND_6,
        MORE_THAN_6
    }

    @Column(
            nullable = false,
            length = 64
    )
    private String searchText;
    @Enumerated(EnumType.STRING)
    private Experience experience;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HHVacancyRequest that = (HHVacancyRequest) o;
        return Objects.equals(searchText, that.searchText) && experience == that.experience;
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchText, experience);
    }
}
