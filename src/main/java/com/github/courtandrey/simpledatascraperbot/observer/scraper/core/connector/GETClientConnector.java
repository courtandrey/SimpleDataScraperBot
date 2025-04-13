package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.StreamUtil.writeToString;

@Slf4j
@RequiredArgsConstructor
public class GETClientConnector implements IConnector {
    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy()).build();

    private final String url;
    private Map<String, String> headers = new HashMap<>();
    private BiFunction<RequestPagingContext, String, String> pageTransformation = (context, page) -> String.format(page, context.getCurrentPage());

    public GETClientConnector(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    public GETClientConnector(String url, BiFunction<RequestPagingContext, String, String> pageTransformation) {
        this.url = url;
        this.pageTransformation = pageTransformation;
    }

    public GETClientConnector(String url, Map<String, String> headers,
                              BiFunction<RequestPagingContext, String, String> pageTransformation) {
        this.url = url;
        this.headers = headers;
        this.pageTransformation = pageTransformation;
    }

    @Override
    public String connectPageSearch(RequestPagingContext context, Integer timeoutMillis) {
        String newUrl = pageTransformation.apply(context, url);
        return connect(newUrl, timeoutMillis, headers);
    }

    @Override
    public String connect(String url, Integer timeoutMillis) {
        return connect(url, timeoutMillis, headers);
    }

    @Override
    public String connect(Integer timeout) {
        return connect(url, timeout, headers);
    }

    private String connect(String urlString, Integer timeout,
                           Map<String, String> headers) {
        try {
            HttpGet request = new HttpGet();
            for (Map.Entry<String,String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setURI(new URI(urlString));
            HttpResponse response = client.execute(request);
            log.info("Fetched {}", urlString);
            Thread.sleep(timeout);
            return writeToString(response.getEntity().getContent());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Connection issues: {}", url, e);
            return "";
        }
    }}
