package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.BiFunction;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.StreamUtil.writeToString;

@Slf4j
public class POSTConnector implements IConnector {
    private final HttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();
    private final String url;
    private final String postStatement;
    private BiFunction<RequestPagingContext, String, String> postPageTransformation = (context, post) -> post;
    private Map<String, String> headers = Map.of();

    public POSTConnector(String url, String postStatement,
                         BiFunction<RequestPagingContext, String, String> postPageTransformation,
                         Map<String, String> headers) {
        this.url = url;
        this.postStatement = postStatement;
        this.postPageTransformation = postPageTransformation;
        this.headers = headers;
    }

    @Override
    public String connectPageSearch(RequestPagingContext pageNum, Integer timeoutMillis) {
        String newStatement = postPageTransformation.apply(pageNum, postStatement);
        return connect(url, newStatement, timeoutMillis, headers);
    }

    @Override
    public String connect(String url, Integer timeoutMillis) {
        String newStatement = postPageTransformation.apply(RequestPagingContext.builder().currentPage(1).build(), postStatement);
        return connect(url, newStatement, timeoutMillis, headers);
    }

    @Override
    public String connect(Integer timeout) {
        String newStatement = postPageTransformation.apply(RequestPagingContext.builder().currentPage(1).build(), postStatement);
        return connect(url, newStatement, timeout, headers);
    }

    private String connect(String urlString, String post, Integer timeout,
                           Map<String, String> headers) {
        try {
            HttpPost request = new HttpPost();
            for (Map.Entry<String,String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setURI(new URI(urlString));
            request.setEntity(new StringEntity(post));
            HttpResponse response = client.execute(request);
            log.info("Fetched {}", url);
            Thread.sleep(timeout);
            return writeToString(response.getEntity().getContent());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Connection issues: {}", url, e);
            return "";
        }
    }
}