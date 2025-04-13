package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestPagingContext {
    private int currentPage;
    private String previousResponse;
}
