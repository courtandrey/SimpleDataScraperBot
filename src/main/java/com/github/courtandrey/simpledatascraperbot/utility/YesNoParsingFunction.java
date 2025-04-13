package com.github.courtandrey.simpledatascraperbot.utility;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Slf4j
public class YesNoParsingFunction implements Function<String, Boolean> {

    @Override
    public Boolean apply(String str) {
        return ofNullable(str)
                .filter("No"::equalsIgnoreCase)
                .map(s -> false)
                .or(() ->
                        ofNullable(str)
                                .filter("Yes"::equalsIgnoreCase)
                                .map(s -> true)
                ).orElseGet(() -> {
                    log.error("Could not parse boolean value: {}. Returning null", str);
                    return null;
                });
    }
}
