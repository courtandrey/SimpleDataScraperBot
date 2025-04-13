package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser;

import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.function.Predicate;

import static java.util.Optional.of;

public class ParsingUtil {
    private ParsingUtil() {}

    public static String textFromElements(Elements elements) {
        return of(elements)
                .filter(Predicate.not(Elements::isEmpty))
                .map(els -> els.get(0).text())
                .filter(StringUtils::hasText).orElse(null);
    }
}
