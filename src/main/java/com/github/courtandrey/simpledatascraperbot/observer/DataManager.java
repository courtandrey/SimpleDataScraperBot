package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.repository.RepositoryFactory;
import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.factory.ScraperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DataManager {
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private ScraperFactory scraperFactory;
    @Autowired
    private UserRepository userRepository;


    public Collection<Data> getNewDataMatchingRequest(Long userId) throws IOException {
        User user = userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);

        List<Data> data = scraperFactory.scrap(user.getRequests());

        return repositoryFactory.update(data, user);
    }
}
