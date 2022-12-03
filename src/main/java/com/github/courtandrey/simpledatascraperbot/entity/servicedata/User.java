package com.github.courtandrey.simpledatascraperbot.entity.servicedata;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import jakarta.persistence.*;
import lombok.Getter;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private Set<Request> requests;

    public User() {
    }

    public User(org.telegram.telegrambots.meta.api.objects.User user) throws TelegramApiException {
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userId=user.getId();
        this.username=user.getUserName();
    }

}
