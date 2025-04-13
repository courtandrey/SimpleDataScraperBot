package com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class NLHousingRequest extends Request {
    @Column(updatable = false)
    private String city;

    @Column(nullable = false, updatable = false)
    private int lowestPrice = 0;

    @Column(updatable = false)
    private Integer highestPrice;

    @Column(updatable = false)
    private Boolean petsAllowed;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "nlhousing_request_site")
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SUBSELECT
    )
    private Set<NLHousingSite> sites = new HashSet<>();

    @Override
    public String toString() {
        Hibernate.isInitialized(sites);
        return String.format("NLHousing identified by city: %s, lowest price: %d, highest price: %s, pets allowed: %s and allowed sites: %s",
                city == null ? "Not specified" : city, lowestPrice, highestPrice == null ? "Not specified" : highestPrice,
                petsAllowed == null ? "not specified" : petsAllowed ? "Yes" : "No", sites.stream().map(Enum::name).collect(Collectors.joining(", ")));
    }
}
