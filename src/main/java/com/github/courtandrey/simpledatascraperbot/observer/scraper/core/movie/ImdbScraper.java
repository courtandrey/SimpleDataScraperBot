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
               .withFallbackConnector(getPostConnector())
               .withReqDataPostProcessing((movie, req) -> movie.setCountry(req.getCountry()))
               .withStopPaginationPredicate(parser::hasNextPage)
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

    private Function<IMDBRequest, IConnector> getPostConnector() {
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
            return new POSTConnector("https://caching.graphql.imdb.com/", getPost(req), getPostTransformation(), headers);
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

    private BiFunction<RequestPagingContext, String, String> getPostTransformation() {
        return (ctx, post) -> {
            if (StringUtils.hasText(ctx.getPreviousResponse())) {
                Optional<String> token = parser.getAfterToken(ctx.getPreviousResponse());
                if (token.isEmpty()) return post.replace("$PAGE", "");
                String after = token.get();
                return post.replace("$PAGE", String.format("\"after\":\"%s\",", after));
            }

            return post.replace("$PAGE", "");
        };
    }

    private String getPost(IMDBRequest request) {
        String template = "{\"query\":\"query AdvancedTitleSearch($first: Int!, $after: String, $titleTypeConstraint: TitleTypeSearchConstraint, $interestConstraint: InterestSearchConstraint, " +
                "$genreConstraint: GenreSearchConstraint, $certificateConstraint: CertificateSearchConstraint, $characterConstraint: CharacterSearchConstraint, $userRatingsConstraint: UserRatingsSearchConstraint, " +
                "$titleTextConstraint: TitleTextSearchConstraint, $creditedCompanyConstraint: CreditedCompanySearchConstraint, $explicitContentConstraint: ExplicitContentSearchConstraint, $sortBy: AdvancedTitleSearchSortBy!, " +
                "$sortOrder: SortOrder!, $releaseDateConstraint: ReleaseDateSearchConstraint, $colorationConstraint: ColorationSearchConstraint, $runtimeConstraint: RuntimeSearchConstraint, $watchOptionsConstraint: WatchOptionsSearchConstraint, " +
                "$awardConstraint: AwardSearchConstraint, $rankedTitleListConstraint: RankedTitleListSearchConstraint, $titleCreditsConstraint: TitleCreditsConstraint, $inTheatersConstraint: InTheatersSearchConstraint, $soundMixConstraint: SoundMixSearchConstraint, " +
                "$keywordConstraint: KeywordSearchConstraint, $originCountryConstraint: OriginCountrySearchConstraint, $languageConstraint: LanguageSearchConstraint, $episodicConstraint: EpisodicSearchConstraint, " +
                "$alternateVersionMatchingConstraint: AlternateVersionMatchingSearchConstraint, $crazyCreditMatchingConstraint: CrazyCreditMatchingSearchConstraint, $goofMatchingConstraint: GoofMatchingSearchConstraint, " +
                "$filmingLocationConstraint: FilmingLocationSearchConstraint, $plotMatchingConstraint: PlotMatchingSearchConstraint, $quoteMatchingConstraint: TitleQuoteMatchingSearchConstraint, " +
                "$soundtrackMatchingConstraint: SoundtrackMatchingSearchConstraint, $triviaMatchingConstraint: TitleTriviaMatchingSearchConstraint, $withTitleDataConstraint: WithTitleDataSearchConstraint, $myRatingConstraint: MyRatingSearchConstraint, " +
                "$listConstraint: ListSearchConstraint) {\\n  advancedTitleSearch(\\n    first: $first\\n    after: $after\\n    constraints: {titleTypeConstraint: $titleTypeConstraint, genreConstraint: $genreConstraint, certificateConstraint: " +
                "$certificateConstraint, characterConstraint: $characterConstraint, userRatingsConstraint: $userRatingsConstraint, titleTextConstraint: $titleTextConstraint, creditedCompanyConstraint: $creditedCompanyConstraint, explicitContentConstraint: " +
                "$explicitContentConstraint, releaseDateConstraint: $releaseDateConstraint, colorationConstraint: $colorationConstraint, runtimeConstraint: $runtimeConstraint, watchOptionsConstraint: $watchOptionsConstraint, awardConstraint: " +
                "$awardConstraint, rankedTitleListConstraint: $rankedTitleListConstraint, titleCreditsConstraint: $titleCreditsConstraint, inTheatersConstraint: $inTheatersConstraint, soundMixConstraint: $soundMixConstraint, keywordConstraint: " +
                "$keywordConstraint, originCountryConstraint: $originCountryConstraint, languageConstraint: $languageConstraint, episodicConstraint: $episodicConstraint, alternateVersionMatchingConstraint: " +
                "$alternateVersionMatchingConstraint, crazyCreditMatchingConstraint: $crazyCreditMatchingConstraint, goofMatchingConstraint: $goofMatchingConstraint, filmingLocationConstraint: $filmingLocationConstraint, plotMatchingConstraint: " +
                "$plotMatchingConstraint, quoteMatchingConstraint: $quoteMatchingConstraint, soundtrackMatchingConstraint: $soundtrackMatchingConstraint, triviaMatchingConstraint: $triviaMatchingConstraint, withTitleDataConstraint: " +
                "$withTitleDataConstraint, myRatingConstraint: $myRatingConstraint, listConstraint: $listConstraint, interestConstraint: $interestConstraint}\\n    sort: {sortBy: $sortBy, sortOrder: $sortOrder}\\n  ) " +
                "{\\n    total\\n    pageInfo {\\n      hasPreviousPage\\n      hasNextPage\\n      startCursor\\n      endCursor\\n    }\\n    ...TitleSearchFacetFields\\n    edges {\\n      node {\\n        title {\\n          ...TitleListItemMetadata\\n          " +
                "...TitleListItemMetascore\\n        }\\n      }\\n    }\\n  }\\n}\\n\\nfragment TitleListItemMetadata on Title {\\n  ...TitleListItemMetadataEssentials\\n  latestTrailer {\\n    id\\n  }\\n  plot {\\n    plotText {\\n      plainText\\n    }\\n  }\\n  " +
                "releaseDate {\\n    day\\n    month\\n    year\\n  }\\n  productionStatus {\\n    currentProductionStage {\\n      id\\n      text\\n    }\\n  }\\n}\\n\\nfragment TitleListItemMetadataEssentials on Title " +
                "{\\n  ...BaseTitleCard\\n  series {\\n    series {\\n      id\\n      originalTitleText {\\n        text\\n      }\\n      releaseYear {\\n        endYear\\n        year\\n      }\\n      titleText {\\n        text\\n      }\\n    }\\n  }\\n}\\n\\n" +
                "fragment BaseTitleCard on Title {\\n  id\\n  titleText {\\n    text\\n  }\\n  titleType {\\n    id\\n    text\\n    canHaveEpisodes\\n    displayableProperty {\\n      value {\\n        plainText\\n      }\\n    }\\n  }\\n  " +
                "originalTitleText {\\n    text\\n  }\\n  primaryImage {\\n    id\\n    width\\n    height\\n    url\\n    caption {\\n      plainText\\n    }\\n  }\\n  releaseYear {\\n    year\\n    endYear\\n  }\\n  ratingsSummary {\\n    aggregateRating\\n    " +
                "voteCount\\n  }\\n  runtime {\\n    seconds\\n  }\\n  certificate {\\n    rating\\n  }\\n  canRate {\\n    isRatable\\n  }\\n  titleGenres {\\n    genres(limit: 3) {\\n      genre {\\n        text\\n      }\\n    }\\n  }\\n}\\n\\nfragment " +
                "TitleListItemMetascore on Title {\\n  metacritic {\\n    metascore {\\n      score\\n    }\\n  }\\n}\\n\\nfragment TitleSearchFacetFields on AdvancedTitleSearchConnection {\\n  genres: facet(facetField: GENRES, limit: 30) {\\n    filterId\\n    text\\n    " +
                "total\\n  }\\n  keywords: facet(facetField: KEYWORDS, limit: 100) {\\n    filterId\\n    text\\n    total\\n  }\\n  titleTypes: facet(facetField: TITLE_TYPE, limit: 25) {\\n    filterId\\n    text\\n    total\\n  }\\n  jobCategories: " +
                "facet(facetField: NAME_JOB_CATEGORIES) {\\n    filterId\\n    text\\n    total\\n  }\\n}\",\"operationName\":\"AdvancedTitleSearch\",\"variables\":{\"locale\":\"en-US\",$PAGE\"first\":50,\"sortBy\":\"POPULARITY\",\"sortOrder\":\"ASC\"," +
                "\"titleTypeConstraint\":{\"anyTitleTypeIds\":[\"movie\"]},$START_DATE_PART$MIN_VOTES_PART$GENRE_PART$COUNTRY_PART" +
                "},\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"81b46290a78cc1e8b3d713e6a43c191c55b4dccf3e1945d6b46668945846d832\"}}}";

        String startDateTemplate = "\"releaseDateConstraint\":{\"releaseDateRange\":{\"start\":\"%s\"}},";
        String minVotesTemplate = "\"userRatingsConstraint\":{\"ratingsCountRange\":{\"min\":%d}},";
        String genreTemplate = "\"genreConstraint\":{\"allGenreIds\":[\"%s\"]},";
        String countryTemplate = "\"originCountryConstraint\":{\"allCountries\":[\"%s\"]}";

        template = template.replace("$START_DATE_PART", ofNullable(request.getReleaseDate()).map(date -> String.format(startDateTemplate, date)).orElse(""));
        template = template.replace("$MIN_VOTES_PART", ofNullable(request.getMinVotes()).map(votes -> String.format(minVotesTemplate, votes)).orElse(""));
        template = template.replace("$GENRE_PART", ofNullable(request.getGenre()).map(genre -> String.format(genreTemplate, genre)).orElse(""));
        template = template.replace("$COUNTRY_PART", ofNullable(request.getCountry()).map(country -> String.format(countryTemplate, country)).orElse(""));
        return template;
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
