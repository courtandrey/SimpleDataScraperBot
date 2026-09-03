package com.github.courtandrey.simpledatascraperbot.entity.request.nljob;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class NLJobRequest extends Request {
    @Column(updatable = false)
    private String keyword;

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "nljob_request_site")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 64)
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SUBSELECT
    )
    private Set<NLJobSite> sites = new HashSet<>();

    @Override
    public String toString() {
        Hibernate.isInitialized(sites);
        return String.format("NLJob identified by keyword: %s and allowed sites: %s",
                keyword == null ? "Not specified" : keyword,
                sites.stream().map(Enum::name).collect(Collectors.joining(", ")));
    }
}
