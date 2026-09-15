package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.movie.IMDBRequest;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.IConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.POSTConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.RequestPagingContext;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.movie.ImdbParser;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

public class ImdbScraper implements Scraper<Movie> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String GRAPHQL_URL = "https://caching.graphql.imdb.com/";
    private static final Map<String, String> HEADERS = Map.of(
            "Content-type", "application/json",
            "User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
            "x-imdb-client-name", "imdb-web-next"
    );

    private static final String SEARCH_QUERY = """
            query AdvancedTitleSearch($first: Int!, $after: String, $titleTypeConstraint: TitleTypeSearchConstraint,
                    $genreConstraint: GenreSearchConstraint, $userRatingsConstraint: UserRatingsSearchConstraint,
                    $releaseDateConstraint: ReleaseDateSearchConstraint, $originCountryConstraint: OriginCountrySearchConstraint,
                    $sortBy: AdvancedTitleSearchSortBy!, $sortOrder: SortOrder!) {
              advancedTitleSearch(
                first: $first
                after: $after
                constraints: {titleTypeConstraint: $titleTypeConstraint, genreConstraint: $genreConstraint,
                  userRatingsConstraint: $userRatingsConstraint, releaseDateConstraint: $releaseDateConstraint,
                  originCountryConstraint: $originCountryConstraint}
                sort: {sortBy: $sortBy, sortOrder: $sortOrder}
              ) {
                pageInfo { hasNextPage endCursor }
                edges { node { title { id originalTitleText { text } releaseYear { year } ratingsSummary { aggregateRating } runtime { seconds } } } }
              }
            }
            """;

    private final ImdbParser parser = new ImdbParser();

    @Override
    public List<Pair<Request, Processee<Movie>>> scrap(List<Request> reqs) {
       return new PageScrapingFunction<>(
               getConnector(),
               parser::parsePage,
               IMDBRequest.class
       )
               .withReqDataPostProcessing((movie, req) -> movie.setCountry(req.getCountry()))
               .withStopPaginationPredicate(Predicate.not(parser::hasNextPage))
               .apply(reqs);
    }

    private Function<IMDBRequest, IConnector> getConnector() {
        return req -> new POSTConnector(
                GRAPHQL_URL,
                getPost(req, null),
                (ctx, post) -> getAfterToken(ctx).map(after -> getPost(req, after)).orElse(post),
                HEADERS
        );
    }

    private Optional<String> getAfterToken(RequestPagingContext ctx) {
        return ofNullable(ctx.getPreviousResponse())
                .filter(StringUtils::hasText)
                .flatMap(parser::getAfterToken);
    }

    private String getPost(IMDBRequest request, String after) {
        ObjectNode variables = MAPPER.createObjectNode()
                .put("first", 50)
                .put("sortBy", "POPULARITY")
                .put("sortOrder", "ASC");
        variables.putObject("titleTypeConstraint").putArray("anyTitleTypeIds").add("movie");
        variables.putObject("userRatingsConstraint").putObject("ratingsCountRange").put("min", request.getMinVotes());
        ofNullable(after).ifPresent(token -> variables.put("after", token));
        ofNullable(request.getGenre()).ifPresent(genre ->
                variables.putObject("genreConstraint").putArray("allGenreIds").add(genre));
        ofNullable(request.getCountry()).ifPresent(country ->
                variables.putObject("originCountryConstraint").putArray("allCountries").add(country));
        ofNullable(request.getReleaseDate()).ifPresent(date ->
                variables.putObject("releaseDateConstraint").putObject("releaseDateRange").put("start", date.toString()));

        ObjectNode body = MAPPER.createObjectNode()
                .put("operationName", "AdvancedTitleSearch")
                .put("query", SEARCH_QUERY);
        body.set("variables", variables);
        return body.toString();
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof IMDBRequest;
    }
}
