package com.github.courtandrey.simpledatascraperbot.graphmovie;

import com.github.courtandrey.simpledatascraperbot.entity.data.Movie;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.RequestToData;
import com.github.courtandrey.simpledatascraperbot.entity.request.movie.IMDBRequest;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.IntFilter;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.MovieFilter;
import com.github.courtandrey.simpledatascraperbot.graphmovie.MovieQueryInputs.StringFilter;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class MovieSpecifications {

    private MovieSpecifications() { }

    public static Specification<Movie> from(MovieFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            collect(filter, root, query, cb, predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void collect(MovieFilter f,
                                jakarta.persistence.criteria.Root<Movie> root,
                                jakarta.persistence.criteria.CriteriaQuery<?> query,
                                jakarta.persistence.criteria.CriteriaBuilder cb,
                                List<Predicate> out) {
        if (f == null) {
            return;
        }

        addString(cb, root.get("name"), f.name(), out);
        addString(cb, root.get("country"), f.country(), out);
        addString(cb, root.get("rating"), f.rating(), out);
        addString(cb, root.get("releaseYear"), f.releaseYear(), out);
        addString(cb, root.get("duration"), f.duration(), out);

        boolean needsRequestJoin = f.requestedByUserId() != null || f.genre() != null || f.minVotes() != null;
        if (needsRequestJoin) {
            query.distinct(true);
            Join<Movie, RequestToData> link = root.join("requestToData", JoinType.INNER);
            Join<RequestToData, Request> req = link.join("request", JoinType.INNER);

            if (f.requestedByUserId() != null) {
                out.add(cb.equal(req.join("user").get("userId"), f.requestedByUserId()));
            }
            if (f.genre() != null || f.minVotes() != null) {
                Join<RequestToData, IMDBRequest> imdb = cb.treat(req, IMDBRequest.class);
                addString(cb, imdb.get("genre"), f.genre(), out);
                addInt(cb, imdb.get("minVotes"), f.minVotes(), out);
            }
        }

        if (f.and() != null) {
            for (MovieFilter sub : f.and()) {
                collect(sub, root, query, cb, out);
            }
        }
        if (f.or() != null && !f.or().isEmpty()) {
            List<Predicate> ors = new ArrayList<>();
            for (MovieFilter sub : f.or()) {
                List<Predicate> branch = new ArrayList<>();
                collect(sub, root, query, cb, branch);
                ors.add(cb.and(branch.toArray(new Predicate[0])));
            }
            out.add(cb.or(ors.toArray(new Predicate[0])));
        }
        if (f.not() != null) {
            List<Predicate> negated = new ArrayList<>();
            collect(f.not(), root, query, cb, negated);
            out.add(cb.not(cb.and(negated.toArray(new Predicate[0]))));
        }
    }

    private static void addString(jakarta.persistence.criteria.CriteriaBuilder cb,
                                  Path<String> path, StringFilter f, List<Predicate> out) {
        if (f == null) {
            return;
        }
        if (f.eq() != null)         out.add(cb.equal(path, f.eq()));
        if (f.ne() != null)         out.add(cb.notEqual(path, f.ne()));
        if (f.contains() != null)   out.add(cb.like(path, "%" + escape(f.contains()) + "%", '\\'));
        if (f.startsWith() != null) out.add(cb.like(path, escape(f.startsWith()) + "%", '\\'));
        if (f.endsWith() != null)   out.add(cb.like(path, "%" + escape(f.endsWith()), '\\'));
        if (f.in() != null && !f.in().isEmpty()) out.add(path.in(f.in()));
        if (f.isNull() != null)     out.add(f.isNull() ? path.isNull() : path.isNotNull());
    }

    private static void addInt(jakarta.persistence.criteria.CriteriaBuilder cb,
                               Path<Integer> path, IntFilter f, List<Predicate> out) {
        if (f == null) {
            return;
        }
        if (f.eq() != null)  out.add(cb.equal(path, f.eq()));
        if (f.ne() != null)  out.add(cb.notEqual(path, f.ne()));
        if (f.gt() != null)  out.add(cb.gt(path, f.gt()));
        if (f.gte() != null) out.add(cb.ge(path, f.gte()));
        if (f.lt() != null)  out.add(cb.lt(path, f.lt()));
        if (f.lte() != null) out.add(cb.le(path, f.lte()));
        if (f.in() != null && !f.in().isEmpty()) out.add(path.in(f.in()));
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
