package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class GETConnector implements IConnector {
    private static final Logger logger = LoggerFactory.getLogger(GETConnector.class);
    private final String url;
    private Map<String, String> headers = new HashMap<>();
    private BiFunction<RequestPagingContext, String, String> pageTransformation = (context, page) -> String.format(page, context.getCurrentPage());

    public GETConnector(String url) {
        this.url = url;
    }

    public GETConnector(String url, Map<String, String> headers, BiFunction<RequestPagingContext, String, String> pageTransformation) {
        this.url = url;
        this.headers = headers;
        this.pageTransformation = pageTransformation;
    }

    public GETConnector(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    @Override
    public String connectPageSearch(RequestPagingContext context, Integer timeoutMillis) {
        String urlWithPage = pageTransformation.apply(context, url);
        return connect(urlWithPage,timeoutMillis);
    }

    @Override
    public String connect(Integer timeout) {
        return connect(url, timeout);
    }

    @Override
    public String connect(String url, Integer timeoutMillis) {
        StringBuilder res = new StringBuilder();
        try {
            URL connectionURL = new URL(url);
            URLConnection connection = connectionURL.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            http.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();
            logger.info("Fetched {}", url);
            Thread.sleep(timeoutMillis);
        } catch (IOException | InterruptedException e) {
            logger.error("Connection issues: {}", url, e);
        }
        if (res.isEmpty()) {
            logger.error("Connection issues: Empty input {}", url);
        }
        return res.toString();
    }
}
