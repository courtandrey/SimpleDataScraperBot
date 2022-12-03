package com.github.courtandrey.simpledatascraperbot.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public abstract class VacancyRequest extends Request{
    @Column(nullable = false,
            updatable = false)
    private boolean isRemote = false;
}
