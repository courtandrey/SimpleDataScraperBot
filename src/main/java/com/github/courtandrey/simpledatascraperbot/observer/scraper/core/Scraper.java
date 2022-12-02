package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;

import java.util.List;


public interface Scraper<T extends Data> {
    List<T> scrap(List<String> urls);
    String toString();

    boolean rightScraperToRequest(Request request);
}
