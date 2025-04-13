package com.github.courtandrey.simpledatascraperbot.entity.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@org.hibernate.annotations.BatchSize(size = 32)
@Entity
public class Vacancy extends Data {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, length = 1024)
    private String url;
    private String salary;
    private String town;
    private String date;
    private String company;
    @Column(length = 65536)
    @Lob
    private String text;

    @Override
    public String toString() {
        return "Vacancy{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", salary='" + salary + '\'' +
                ", town='" + town + '\'' +
                ", company='" + company + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(url, vacancy.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}