package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

public class ConnectionUtil {
    public static String getAllCookies(HttpResponse response, String currentCookies) {
        StringBuilder builder = new StringBuilder(currentCookies);
        for (Header header : response.getHeaders("Set-Cookie")) {
            String val = header.getValue().split(";")[0];
            if (!builder.toString().contains(val)) {
                builder.append(val).append("; ");
            }
        }
        return builder.toString().trim();
    }
}
