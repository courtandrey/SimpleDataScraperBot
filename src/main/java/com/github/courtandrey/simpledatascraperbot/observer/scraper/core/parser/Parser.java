package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import org.jsoup.nodes.Document;

import java.util.List;

public interface Parser<T> {
    List<? extends Data> parse(Document document);
    default T parseExtra(Document document, T data) {
        return data;
    }
}
