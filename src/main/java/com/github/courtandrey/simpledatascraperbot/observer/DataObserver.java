package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.scraper.factory.ScraperFactory;
import com.github.courtandrey.simpledatascraperbot.view.UpdaterFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DataObserver {
    public Collection<Data> observe() throws IOException {
        List<Data> data = (new ScraperFactory()).scrap();
        return (new UpdaterFactory()).update(data);
    }
}
