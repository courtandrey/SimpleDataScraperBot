package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.factory.ScraperFactory;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataManager {
    private final DataUpdater dataUpdater;
    private final ScraperFactory scraperFactory;
    private final RequestService requestService;

    public Collection<Processee<Data>> getNewDataMatchingRequest(Long userId) throws IOException {
        List<Pair<Request, Processee<Data>>> data = scraperFactory.scrap(requestService.findRequestsByUserId(userId));

        return dataUpdater.update(data);
    }
}
