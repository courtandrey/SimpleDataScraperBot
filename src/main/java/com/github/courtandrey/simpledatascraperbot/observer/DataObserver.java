package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.repository.RepositoryFactory;
import com.github.courtandrey.simpledatascraperbot.scraper.factory.ScraperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class DataObserver {
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private ScraperFactory scraperFactory;
    public Collection<Data> observe() throws IOException {
        List<Data> data = scraperFactory.scrap();
        return repositoryFactory.update(data);
    }
}
