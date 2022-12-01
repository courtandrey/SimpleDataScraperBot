package com.github.courtandrey.simpledatascraperbot.observer.scraper.parser;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import org.jsoup.nodes.Document;

import java.util.List;

public interface Parser {
    List<? extends Data> parse(Document document);
}
