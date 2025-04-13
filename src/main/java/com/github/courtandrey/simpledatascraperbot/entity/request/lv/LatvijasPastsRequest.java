package com.github.courtandrey.simpledatascraperbot.entity.request.lv;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LatvijasPastsRequest extends Request  {
    @Column(nullable = false, updatable = false)
    private String referenceNumber;

    @Override
    public String toString() {
        return "Latvijas Pasts tracking with id: " + referenceNumber;
    }
}
