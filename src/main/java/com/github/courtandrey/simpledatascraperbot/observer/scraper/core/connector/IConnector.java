package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

public interface IConnector {
    String connectPageSearch(RequestPagingContext prevContext, Integer timeoutMillis);

    String connect(String url, Integer timeoutMillis);

    String connect(Integer timeout);
}
