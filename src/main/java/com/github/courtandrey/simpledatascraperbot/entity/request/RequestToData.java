package com.github.courtandrey.simpledatascraperbot.entity.request;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class RequestToData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @Setter
    private Request request;

    @ManyToOne
    @JoinColumn(name = "data_id")
    @Setter
    private Data data;
}
