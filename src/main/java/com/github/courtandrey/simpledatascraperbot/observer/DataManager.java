package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.repository.RepositoryFactory;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.factory.ScraperFactory;
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
    private UserService userService;


    public Collection<Data> getNewDataMatchingRequest(Long userId) throws IOException {
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);

        List<Data> data = scraperFactory.scrap(user.getRequests());

        return repositoryFactory.update(data, user);
    }
}
