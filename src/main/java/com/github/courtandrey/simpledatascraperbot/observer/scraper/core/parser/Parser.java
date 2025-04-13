package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;

import java.util.List;

public interface Parser<T> {

    List<? extends Data> parsePage(String docToParse);

    default void addDetails(String docToParse, T data) {}
}
