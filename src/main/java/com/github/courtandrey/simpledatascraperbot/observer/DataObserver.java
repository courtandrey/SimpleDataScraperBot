package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.data.repository.RepositoryFactory;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.factory.ScraperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
@Scope(scopeName = "prototype")
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
