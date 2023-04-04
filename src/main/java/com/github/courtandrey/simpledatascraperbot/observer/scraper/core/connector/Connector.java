package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);
    private final String url;

    public Connector(String url) {
        this.url = url;
    }


    public String connectPageSearch(int pageNum, Integer timeoutMillis) {
        String urlWithPage = String.format(url, pageNum);
        return connect(urlWithPage,timeoutMillis);
    }

    public String connect(String url, Integer timeoutMillis) {
        StringBuilder res = new StringBuilder();
        try {
            URL connectionURL = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionURL.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();
            logger.info("Fetched " + url);
            Thread.sleep(timeoutMillis);
        } catch (IOException | InterruptedException e) {
            logger.error("Connection issues: " + e + " " + String.format(url));
        }
        if (res.isEmpty()) {
            logger.error("Connection issues: Empty input" + String.format(url));
        }
        return res.toString();
    }
}
