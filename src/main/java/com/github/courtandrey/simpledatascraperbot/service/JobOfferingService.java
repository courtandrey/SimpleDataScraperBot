package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import com.github.courtandrey.simpledatascraperbot.entity.repository.JobOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOfferingService {
    private final JobOfferingRepository repository;

    public List<JobOffering> getOfferingsWithUrlsContainingIn(List<String> urls, Long requestId) {
        return repository.getOfferingsWithUrlsContainingIn(urls, requestId);
    }
}
