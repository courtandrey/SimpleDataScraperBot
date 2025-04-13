package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
public class JsoupConnector implements IConnector {
    private final String url;
    private final Map<String, String> headers;
    private final BiFunction<RequestPagingContext, String, String> pageTransformation;

    public JsoupConnector(String url, Map<String, String> headers,
                          BiFunction<RequestPagingContext, String, String> pageTransformation) {
        this.url = url;
        this.headers = headers;
        this.pageTransformation = pageTransformation;
    }

    public JsoupConnector(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
        this.pageTransformation = (context, string) -> string;
    }

    @Override
    public String connectPageSearch(RequestPagingContext context, Integer timeoutMillis) {
        String urlWithPage = pageTransformation.apply(context, url);
        return connect(urlWithPage,timeoutMillis);
    }

    @Override
    public String connect(String url, Integer timeoutMillis) {
        String result = "";
        try {
            Document connectionURL = Jsoup.connect(url)
                    .headers(headers)
                    .get();
            log.info("Fetched {}", url);
            result = connectionURL.html();
            Thread.sleep(timeoutMillis);
        } catch (IOException | InterruptedException e) {
            log.error("Connection issues: {}", url, e);
        }
        if (result.isEmpty()) {
            log.error("Connection issues: Empty input {}", url);
        }
        return result;
    }

    @Override
    public String connect(Integer timeout) {
        return connect(url, 0);
    }
}
