package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtil {
    private StreamUtil() {}

    public static String writeToString(InputStream stream) throws IOException {
        StringBuilder res = new StringBuilder();
        String inputLine;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
        }
        return res.toString();
    }
}
