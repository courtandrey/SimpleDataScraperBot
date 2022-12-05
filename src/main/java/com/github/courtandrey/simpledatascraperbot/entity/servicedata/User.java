package com.github.courtandrey.simpledatascraperbot.entity.servicedata;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true, mappedBy = "user")
    private Set<Request> requests = new HashSet<>();

    public User() {
    }

    public User(org.telegram.telegrambots.meta.api.objects.User user) {
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userId=user.getId();
        this.username=user.getUserName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, lastName, username);
    }
}
