package com.github.courtandrey.simpledatascraperbot.entity.servicedata;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    private boolean isAdmin = false;

    public User(org.telegram.telegrambots.meta.api.objects.User user) {
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userId=user.getId();
        this.username=user.getUserName();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
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
