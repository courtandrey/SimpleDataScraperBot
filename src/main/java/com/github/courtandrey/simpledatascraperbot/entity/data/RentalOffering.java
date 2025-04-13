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
public class RentalOffering extends Data {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true, length = 1024)
    private String url;
    private String price;
    private String date;
    private String city;
    private Boolean petsAllowed;
    private String deposit;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RentalOffering offering = (RentalOffering) o;
        return Objects.equals(url, offering.url) && Objects.equals(status, offering.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, status);
    }


    @Override
    public String toString() {
        return "RentalOffering{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", price='" + price + '\'' +
                ", date='" + date + '\'' +
                ", city='" + city + '\'' +
                ", petsAllowed=" + petsAllowed +
                ", deposit='" + deposit + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
