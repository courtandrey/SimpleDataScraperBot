package com.github.courtandrey.simpledatascraperbot.graphmovie;

import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import com.github.courtandrey.simpledatascraperbot.entity.repository.MovieRepository;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.RequestToData;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.MovieFilter;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.MovieOrder;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.MovieSortField;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.SortDirection;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class MovieController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final MovieRepository movieRepository;
    private final RequestLinkRepository requestLinkRepository;

    public MovieController(MovieRepository movieRepository, RequestLinkRepository requestLinkRepository) {
        this.movieRepository = movieRepository;
        this.requestLinkRepository = requestLinkRepository;
    }

    @QueryMapping
    public Movie movie(@Argument Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public Window<Movie> movies(@Argument MovieFilter filter,
                                @Argument List<MovieOrder> orderBy,
                                ScrollSubrange subrange) {
        Specification<Movie> spec = MovieSpecifications.from(filter);
        Sort sort = toSort(orderBy);
        int limit = Math.min(subrange.count().orElse(DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);
        ScrollPosition position = subrange.position().orElse(ScrollPosition.offset());

        return movieRepository.findBy(spec, q -> q.sortBy(sort).limit(limit).scroll(position));
    }

    @BatchMapping(field = "requests")
    public Map<Movie, List<Request>> requests(List<Movie> movies) {
        List<Long> ids = movies.stream().map(Movie::getId).toList();

        Map<Long, List<Request>> byMovieId = requestLinkRepository
                .findWithRequestAndUserByMovieIds(ids).stream()
                .collect(Collectors.groupingBy(
                        rtd -> rtd.getData().getId(),
                        Collectors.mapping(RequestToData::getRequest, Collectors.toList())));

        return movies.stream().collect(Collectors.toMap(
                Function.identity(),
                m -> byMovieId.getOrDefault(m.getId(), List.of())));
    }

    private Sort toSort(List<MovieOrder> orders) {
        List<Sort.Order> list = new ArrayList<>();
        if (orders != null) {
            for (MovieOrder o : orders) {
                Sort.Direction dir = o.direction() == SortDirection.DESC
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                list.add(new Sort.Order(dir, property(o.field())));
            }
        }
        list.add(Sort.Order.asc("id"));
        return Sort.by(list);
    }

    private String property(MovieSortField field) {
        return switch (field) {
            case NAME -> "name";
            case COUNTRY -> "country";
            case RATING -> "rating";
            case RELEASE_YEAR -> "releaseYear";
            case DURATION -> "duration";
            case ID -> "id";
        };
    }
}
