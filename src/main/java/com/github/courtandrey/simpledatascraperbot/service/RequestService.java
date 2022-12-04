package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.repository.HHRequestRepository;
import com.github.courtandrey.simpledatascraperbot.entity.repository.HabrCareerRequestRepository;
import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UnknownRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class RequestService {
    private final UserService userService;
    private final HHRequestRepository hhRequestRepository;
    private final HabrCareerRequestRepository habrCareerRequestRepository;
    @Autowired
    public RequestService(UserService userService, HHRequestRepository hhRequestRepository, HabrCareerRequestRepository habrCareerRequestRepository) {
        this.userService = userService;
        this.hhRequestRepository = hhRequestRepository;
        this.habrCareerRequestRepository = habrCareerRequestRepository;
    }

    @Transactional
    public void addRequestToUser(Long userId, Request request) {
        User user = userService.getReferenceById(userId);

        request.setUser(user);

        if (request instanceof HHVacancyRequest hhVacancyRequest) {
            hhRequestRepository.save(hhVacancyRequest);
            return;
        }

        else if (request instanceof HabrCareerVacancyRequest habrCareerVacancyRequest) {
            habrCareerRequestRepository.save(habrCareerVacancyRequest);
            return;
        }

        throw new UnknownRequestException("Unknown type of Request");
    }
    @Transactional
    public Optional<Request> findRequestById(Long requestId) {
        Request request = hhRequestRepository.findById(requestId).orElse(null);

        if (request != null) {
            return Optional.of(request);
        }

        request = habrCareerRequestRepository.findById(requestId).orElse(null);

        if (request != null) {
            return Optional.of(request);
        }

        return Optional.empty();
    }
    @Transactional
    public void deleteRequestById(Long requestId) {
        Request request = findRequestById(requestId)
                .orElseThrow(() -> new UnknownRequestException("There is no such request"));

        if (request instanceof HHVacancyRequest hhVacancyRequest) {
            hhRequestRepository.delete(hhVacancyRequest);
        } else if (request instanceof HabrCareerVacancyRequest habrCareerVacancyRequest) {
            habrCareerRequestRepository.delete(habrCareerVacancyRequest);
        }
    }

    @Transactional
    public Collection<Request> findRequestsByUserId(Long chatId) {
        return habrCareerRequestRepository.findByUserUserId(chatId)
                .and(hhRequestRepository.findByUserUserId(chatId)).stream().toList();
    }
}
