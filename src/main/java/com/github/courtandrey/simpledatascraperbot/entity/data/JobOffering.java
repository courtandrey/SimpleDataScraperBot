package com.github.courtandrey.simpledatascraperbot.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@org.hibernate.annotations.BatchSize(size = 32)
@Entity
public class JobOffering extends Data {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, length = 1024)
    private String url;
    private String city;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobOffering offering = (JobOffering) o;
        return Objects.equals(url, offering.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "JobOffering{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
