package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    @Transactional
    public User getReferenceById(Long chatId) {
        return userRepository.getReferenceById(chatId);
    }

    @Transactional
    public Optional<User> getUserById(Long id) {
        return userRepository.findByUserId(id);
    }

    @Transactional
    public Optional<User> getUserWithRequests(Long id) {
        Optional<User> user = userRepository.findByUserId(id);
        if (user.isEmpty()) return user;
        user.get().getRequests().size();
        return user;
    }
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> addIfEmptyByUserId(Message message) throws TelegramApiException {
        if (userRepository.findByUserId(message.getChatId()).isEmpty()) {
            return Optional.of(userRepository.save(new User(message.getFrom())));
        }

        return Optional.empty();
    }
}
