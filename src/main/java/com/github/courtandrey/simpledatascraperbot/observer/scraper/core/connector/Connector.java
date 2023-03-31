package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Connector {
    private static final Logger logger = LoggerFactory.getLogger(Connector.class);
    private final String url;

    public Connector(String url) {
        this.url = url;
    }


    public Document connectPageSearch(int pageNum) {
        String urlWithPage = String.format(url, pageNum);
        return connect(urlWithPage);
    }

    public Document connect(String url) {
        Document document = null;
        try {
            document = Jsoup
                    .connect(url)
                    .get();
            logger.info("Fetched " + url);
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            logger.error("Connection issues: " + e + " " + String.format(url));
        }
        return document;
    }
}
