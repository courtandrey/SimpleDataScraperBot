package com.github.courtandrey.simpledatascraperbot.scraper.core;

import com.github.courtandrey.simpledatascraperbot.data.Data;

import java.util.List;


public interface Scraper<T extends Data> {
    List<T> scrap();
}
