package com.github.courtandrey.simpledatascraperbot.scraper.connector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Connector {
    private final String url;

    public Connector(String url) {
        this.url = url;
    }

    public Document connect(int pageNum) {
        Document document = null;
        try {
            document = Jsoup
                    .connect(String.format(url, pageNum))
                    .get();
            Thread.sleep(1000);
        } catch (IOException e) {
            System.out.println("Connection issues: " + e.getMessage() + " " + String.format(url, pageNum));
        } catch (InterruptedException e) {
            System.out.println("Finished.");
            System.exit(0);
        }
        return document;
    }
}
