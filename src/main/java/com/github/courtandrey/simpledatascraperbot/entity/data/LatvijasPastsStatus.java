package com.github.courtandrey.simpledatascraperbot.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@org.hibernate.annotations.BatchSize(size = 32)
@Entity
public class LatvijasPastsStatus extends Data {
    @Column(nullable = false)
    private String referenceNumber;
    private String generalStatus;
    private String status;
    private String place;
    private LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LatvijasPastsStatus that = (LatvijasPastsStatus) o;
        return Objects.equals(referenceNumber, that.referenceNumber)
                && Objects.equals(generalStatus, that.generalStatus)
                && Objects.equals(status, that.status)
                && Objects.equals(place, that.place)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceNumber, generalStatus, status, place, date);
    }

    @Override
    public String toString() {
        return "LatvijasPastsStatus{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", generalStatus='" + generalStatus + '\'' +
                ", status='" + status + '\'' +
                ", place='" + place + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
