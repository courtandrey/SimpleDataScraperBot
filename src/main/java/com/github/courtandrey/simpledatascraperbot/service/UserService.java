package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;
    @Autowired
    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findByUserId(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> addIfEmptyByUserId(Message message) throws TelegramApiException {
        if (userRepository.findByUserId(message.getChatId()).isEmpty()) {
            return Optional.of(userRepository.save(new User(message.getFrom())));
        }

        return Optional.empty();
    }

    public User addRequestToUser(Long userId, Request request) {
        User user = userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
        user.getRequests().add(request);
        userRepository.save(user);
        return user;
    }
}
