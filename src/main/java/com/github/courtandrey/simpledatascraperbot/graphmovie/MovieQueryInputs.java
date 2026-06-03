package com.github.courtandrey.simpledatascraperbot.graphmovie;

import java.util.List;

public final class MovieQueryInputs {

    private MovieQueryInputs() { }

    public record StringFilter(
            String eq, String ne, String contains,
            String startsWith, String endsWith,
            List<String> in, Boolean isNull) { }

    public record IntFilter(
            Integer eq, Integer ne,
            Integer gt, Integer gte, Integer lt, Integer lte,
            List<Integer> in) { }

    public record MovieFilter(
            StringFilter name,
            StringFilter country,
            StringFilter rating,
            StringFilter releaseYear,
            StringFilter duration,
            Long requestedByUserId,
            StringFilter genre,
            IntFilter minVotes,
            List<MovieFilter> and,
            List<MovieFilter> or,
            MovieFilter not) { }

    public record MovieOrder(MovieSortField field, SortDirection direction) { }

    public enum MovieSortField { NAME, COUNTRY, RATING, RELEASE_YEAR, DURATION, ID }

    public enum SortDirection { ASC, DESC }
}
