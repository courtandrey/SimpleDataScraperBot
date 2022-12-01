package com.github.courtandrey.simpledatascraperbot.configuration;

import jakarta.persistence.*;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false, unique = true)
    private Long userId;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(org.telegram.telegrambots.meta.api.objects.User user) throws TelegramApiException {
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.userId=user.getId();
    }

}
