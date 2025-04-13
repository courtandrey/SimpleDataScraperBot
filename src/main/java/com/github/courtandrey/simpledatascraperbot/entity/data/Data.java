package com.github.courtandrey.simpledatascraperbot.entity.data;

import com.github.courtandrey.simpledatascraperbot.entity.request.HibernateInitIgnore;
import com.github.courtandrey.simpledatascraperbot.entity.request.RequestToData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true, mappedBy = "data")
    @HibernateInitIgnore
    @Getter
    private Set<RequestToData> requestToData = new HashSet<>();
}