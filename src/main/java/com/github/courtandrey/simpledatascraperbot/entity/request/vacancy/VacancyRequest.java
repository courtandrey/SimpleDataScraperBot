package com.github.courtandrey.simpledatascraperbot.entity.request.vacancy;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public abstract class VacancyRequest extends Request {
    @Column(nullable = false,
            updatable = false)
    private boolean isRemote = false;

    @Override
    public String toString() {
        return isRemote? ", Schedule: Remote" : "";
    }
}
