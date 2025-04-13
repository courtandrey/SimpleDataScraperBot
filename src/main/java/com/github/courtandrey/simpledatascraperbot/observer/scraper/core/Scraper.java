package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;

import java.util.List;


public interface Scraper<T extends Data> {
    List<Pair<Request, Processee<T>>> scrap(List<Request> reqs);

    String toString();

    boolean rightScraperToRequest(Request request);
}
