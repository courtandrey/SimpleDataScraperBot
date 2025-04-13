package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.repository.RentalOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalOfferingService {
    private final RentalOfferingRepository repository;

    public List<RentalOffering> getOfferingsWithUrlsContainingIn(List<String> urls, Long requestId) {
        return repository.getOfferingsWithUrlsContainingIn(urls, requestId);
    }
}
