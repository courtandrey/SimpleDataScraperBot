package com.github.courtandrey.simpledatascraperbot.entity.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
            updatable = false,
            length = 64
    )
    private String searchText;
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            updatable = false
    )
    private Experience experience;
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "hhvacancy_request_region")
    @Enumerated(EnumType.STRING)
    private Set<Region> regions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HHVacancyRequest request = (HHVacancyRequest) o;
        return Objects.equals(searchText, request.searchText) && experience == request.experience && Objects.equals(regions, request.regions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchText, experience, regions);
    }

    @Override
    public String toString() {
        String regionString;
        if (Hibernate.isInitialized(regions) && regions.size() > 0) {
            StringBuilder regionStringBuilder = new StringBuilder();
            regionStringBuilder.append(", Search in regions: ");
            for (Region r : regions) {
                regionStringBuilder.append(r.name()).append(", ");
            }
            regionString =
                    regionStringBuilder.substring(0, regionStringBuilder.length() - 2);
        } else {
            regionString = "";
        }
        return "HeadHunter Request with Text to Search: " + "\"" + searchText + "\"," +
                " Experience: " + experience.name() + regionString + super.toString();
    }
}
