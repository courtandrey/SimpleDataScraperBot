package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.repository.CommonRequestRepository;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UnknownRequestException;
import com.github.courtandrey.simpledatascraperbot.utility.HibernateInitializationFunction;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {
    private final UserService userService;
    private final EntityManager entityManager;
    private final CommonRequestRepository commonRequestRepository;

    public void addRequestToUser(Long userId, Request request) {
        User user = userService.getReferenceById(userId);

        request.setUser(user);

        entityManager.persist(request);
    }

    public void deleteRequestById(Long requestId, Long userId) {
        userService.getUserById(userId).filter(User::isAdmin)
                .flatMap(user -> commonRequestRepository.findById(requestId))
                .map(List::of)
                .orElse(findRequestsByUserId(userId))
                .stream().filter(req -> Objects.equals(req.getId(), requestId))
                .findAny().ifPresentOrElse(entityManager::remove,
                        () -> {
                    throw new UnknownRequestException("There is no request identified by " + requestId
                                    + "for user " + userId);
                        });
    }

    public int countByUserId(Long userId) {
        return commonRequestRepository.findByUserUserId(userId).toList().size();
    }

    public List<Request> findRequestsByUserId(Long chatId) {
        return commonRequestRepository.findByUserUserId(chatId).stream()
                .peek(new HibernateInitializationFunction())
                .toList();
    }

    public Collection<Request> findAll() {
        return commonRequestRepository.findAll()
                .stream().peek(new HibernateInitializationFunction())
                .toList();
    }
}
