package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.LatvijasPastsStatus;
import com.github.courtandrey.simpledatascraperbot.entity.repository.LatvijasPastsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class LatvijasPastsService {
    private final LatvijasPastsRepository repository;

    public List<LatvijasPastsStatus> getStatuses(List<String> referenceNumber, Long requestId) {
        return repository.getStatusesForReferenceNumber(referenceNumber, requestId);
    }
}
