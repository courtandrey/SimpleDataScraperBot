package com.github.courtandrey.simpledatascraperbot.entity.data;

import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Lazy
    private User user;
}