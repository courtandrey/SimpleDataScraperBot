package com.github.courtandrey.simpledatascraperbot.observer.scraper.connector;

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


    public Document connect(int pageNum) {
        Document document = null;
        try {
            String urlWithPage = String.format(url, pageNum);
            document = Jsoup
                    .connect(urlWithPage)
                    .get();
            logger.info("Fetched " + urlWithPage);
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            logger.error("Connection issues: " + e + " " + String.format(url, pageNum));
        }
        return document;
    }
}
