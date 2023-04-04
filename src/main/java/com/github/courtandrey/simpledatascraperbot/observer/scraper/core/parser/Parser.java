package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;

import java.util.List;

public interface Parser<T> {
    List<? extends Data> parse(String docToParse);
    default T parseExtra(String docToParse, T data) {
        return data;
    }
}
