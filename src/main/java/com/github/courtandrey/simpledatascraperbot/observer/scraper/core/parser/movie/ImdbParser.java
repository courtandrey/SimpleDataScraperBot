package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class ImdbParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<Movie> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .map(node -> node.get("data").get("advancedTitleSearch").get("edges"))
                .map(parseMovies())
                .onFailure(exc -> log.error("Could not parse movies from imdb!", exc))
                .getOrElse(new ArrayList<>());
    }

    private Function<JsonNode, List<Movie>> parseMovies() {
        return node -> {
            List<Movie> movies = new ArrayList<>();
            node.iterator()
                    .forEachRemaining(child ->
                            Try.of(() -> processNode(child))
                                    .onFailure(exc -> log.error("Could not parse movies from imdb! node {}", child, exc))
                                    .map(movies::add)
                    );
            return movies;
        };
    }

    private Movie processNode(JsonNode node) {
        Movie movie = new Movie();
        movie.setName(node.get("node").get("title").get("originalTitleText").get("text").asText());
        movie.setDuration(getDuration(node));
        movie.setUrl("https://www.imdb.com/title/" + node.get("node").get("title").get("id").asText() + "/");
        movie.setReleaseYear(node.get("node").get("title").get("releaseYear").get("year").asText());
        movie.setRating(node.get("node").get("title").get("ratingsSummary").get("aggregateRating").asText());
        return movie;
    }

    private String getDuration(JsonNode node) {
        return Try.of(() -> node.get("node").get("title").get("runtime").get("seconds").asInt() / 60 + " mins")
                .getOrElse(() -> null);
    }

    public Optional<String> getAfterToken(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .mapTry(node -> node.get("data").get("advancedTitleSearch").get("pageInfo").get("endCursor").asText())
                .map(Optional::of)
                .onFailure(exc -> log.error("Could not get endCursor token!", exc))
                .getOrElse(Optional::empty);
    }

    public boolean hasNextPage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .mapTry(node -> node.get("data").get("advancedTitleSearch").get("pageInfo").get("hasNextPage").asBoolean())
                .onFailure(exc -> log.error("Could not get endCursor token!", exc))
                .getOrElse(false);
    }
}
