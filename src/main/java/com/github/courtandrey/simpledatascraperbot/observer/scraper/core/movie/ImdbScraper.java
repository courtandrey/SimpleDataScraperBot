package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.movie;

import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.movie.IMDBRequest;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.*;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.movie.ImdbParser;
import io.vavr.control.Try;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.ConnectionUtil.getAllCookies;
import static java.util.Optional.ofNullable;

public class ImdbScraper implements Scraper<Movie> {
    private final ImdbParser parser = new ImdbParser();

    @Override
    public List<Pair<Request, Processee<Movie>>> scrap(List<Request> reqs) {
       return new PageScrapingFunction<>(
               getConnector(),
               parser::parsePage,
               IMDBRequest.class
       )
               .withReqDataPostProcessing((movie, req) -> movie.setCountry(req.getCountry()))
               .apply(reqs);
    }

    private Function<IMDBRequest, IConnector> getConnector() {
        return req -> Try.of(() -> {
            HttpClient client = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpGet get = new HttpGet("https://www.imdb.com/search/title/?title_type=feature");
            get.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0");
            HttpResponse response = client.execute(get);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", "application/json");
            headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0");
            headers.put("Cookie", getAllCookies(response, ""));
            return new GETClientConnector(getUrl(req), headers, transformation());
        }).getOrElseThrow(exc -> new RuntimeException("Could not create a connection to imdb", exc));
    }

    private BiFunction<RequestPagingContext, String, String> transformation() {
        return (ctx, post) -> {
            if (StringUtils.hasText(ctx.getPreviousResponse())) {
                Optional<String> token = parser.getAfterToken(ctx.getPreviousResponse());
                if (token.isEmpty()) return post.replace("$PAGE", "");
                String after = token.get();
                return post.replace("$PAGE", "%22after%22%3A%22" + after + "%22%2C");
            }

            return post.replace("$PAGE", "");
        };
    }

    private String getUrl(IMDBRequest request) {
        String template = "https://caching.graphql.imdb.com/?operationName=AdvancedTitleSearch" +
                "&variables=%7B$PAGE%22first%22%3A50%2C%22genreConstraint%22%3A%7B" +
                "%22allGenreIds%22%3A%5B$GENRE%5D%2C%22excludeGenreIds%22%3A%5B%5D%7D%2C" +
                "%22locale%22%3A%22en-US%22%2C%22originCountryConstraint%22%3A%7B%22allCountries%22%3A%5B$COUNTRY%5D%7D%2C" +
                "%22releaseDateConstraint%22%3A%7B%22releaseDateRange%22%3A%7B%22start%22%3A$RELEASE_DATE%7D%7D%2C%22sortBy%22%3A%22POPULARITY%22%2C" +
                "%22sortOrder%22%3A%22ASC%22%2C%22titleTypeConstraint%22%3A%7B" +
                "%22anyTitleTypeIds%22%3A%5B%22movie%22%5D%2C%22excludeTitleTypeIds%22%3A%5B%5D%7D%2C" +
                "%22userRatingsConstraint%22%3A%7B%22ratingsCountRange%22%3A%7B%22min%22%3A$COUNT%7D%7D%7D" +
                "&extensions=%7B%22persistedQuery%22%3A%7B%22sha256Hash%22%3A%2281b46290a78cc1e8b3d713e6a43c191c55b4dccf3e1945d6b46668945846d832%22%2C" +
                "%22version%22%3A1%7D%7D";

        template = template.replace("$GENRE", ofNullable(request.getGenre()).map(gen -> "%22" + gen + "%22").orElse(""));
        template = template.replace("$COUNTRY", ofNullable(request.getCountry()).map(cnt -> "%22" + cnt + "%22").orElse(""));
        template = template.replace("$RELEASE_DATE", ofNullable(request.getReleaseDate()).map(date -> "%22" + date + "%22").orElse(""));
        template = template.replace("$COUNT", String.valueOf(request.getMinVotes()));
        return template;
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof IMDBRequest;
    }
}
