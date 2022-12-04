package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.factory.ScraperFactory;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DataManager {
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private ScraperFactory scraperFactory;
    @Autowired
    private RequestService requestService;


    public Collection<Data> getNewDataMatchingRequest(Long userId) throws IOException {
        List<Data> data = scraperFactory.scrap(requestService.findRequestsByUserId(userId));

        return repositoryFactory.update(data, userId);
    }
}
