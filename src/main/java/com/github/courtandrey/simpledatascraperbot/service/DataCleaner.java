package com.github.courtandrey.simpledatascraperbot.service;

import com.github.courtandrey.simpledatascraperbot.entity.data.*;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataCleaner {
    private static final int BATCH = 500;

    private final VacancyService vacancyService;
    private final RentalOfferingService rentalOfferingService;
    private final LatvijasPastsService latvijasPastsService;
    private final MovieService movieService;

    public Collection<Pair<Request, Processee<Data>>> getUnique(Collection<Pair<Request, Processee<Data>>> newData) {
        Set<Pair<Request, Processee<Data>>> uniqueData = new HashSet<>();
        Map<Class<? extends Data>, List<Pair<Request, Processee<Data>>>> dataByClass =
                newData.stream().collect(Collectors.groupingBy(pair -> pair.getSecond().transform(Data::getClass)));

        uniqueData.addAll(getUnique(Vacancy.class, dataByClass,
                (requestId, vacancies) -> vacancyService.getVacanciesWithUrlsContainingIn(vacancies.stream()
                        .map(Vacancy::getUrl).toList(), requestId)));
        uniqueData.addAll(getUnique(RentalOffering.class, dataByClass,
                (requestId, offerings) -> rentalOfferingService.getOfferingsWithUrlsContainingIn(offerings.stream()
                        .map(RentalOffering::getUrl).toList(), requestId)));
        uniqueData.addAll(getUnique(LatvijasPastsStatus.class, dataByClass,
                (requestId, statuses) -> latvijasPastsService.getStatuses(statuses.stream().map(LatvijasPastsStatus::getReferenceNumber).distinct().toList(), requestId)));
        uniqueData.addAll(getUnique(Movie.class, dataByClass,
                (requestId, movies) -> movieService.getMovies(movies.stream().map(Movie::getUrl).distinct().toList(), requestId)));

        return uniqueData;
    }

    private <T extends Data> List<Pair<Request, Processee<Data>>> getUnique(Class<T> clazz,
                                                                            Map<Class<? extends Data>, List<Pair<Request, Processee<Data>>>> dataByClass,
                                                                            BiFunction<Long, List<T>, List<T>> getBasedOnT) {
        if (dataByClass.get(clazz) == null) {
            return List.of();
        }

        Map<Long, List<Pair<Request, Processee<Data>>>> data = dataByClass.get(clazz).stream()
                .collect(Collectors.groupingBy(pair -> pair.getFirst().getId()));

        return data.entrySet().stream().map(entry -> process(entry.getKey(), entry.getValue(), getBasedOnT, clazz))
                .flatMap(Collection::stream).toList();
    }

    private <T> List<Pair<Request, Processee<Data>>> process(Long requestId, List<Pair<Request, Processee<Data>>> processeeList,
                                                             BiFunction<Long, List<T>, List<T>> getBasedOnT,
                                                             Class<T> clazz) {
        List<? extends Pair<Request, Pair<T, Processee<Data>>>> pairs = processeeList.stream()
                .map(pair -> pair.mapSecond(processee -> processee.toPair().mapFirst(clazz::cast)))
                .toList();

        List<T> entities = pairs.stream().map(Pair::getSecond).map(Pair::getFirst).collect(Collectors.toCollection(ArrayList::new));

        List<T> oldData = new ArrayList<>();

        for (int start = 0; start < entities.size(); start += BATCH) {
            int end = Math.min(start + BATCH, entities.size());
            List<T> subData = entities.subList(start, end);
            oldData.addAll(getBasedOnT.apply(requestId, subData));
        }

        entities.removeAll(oldData);

        return pairs.stream().filter(pair -> entities.contains(pair.getSecond().getFirst()))
                .map(pair -> pair.mapSecond(Pair::getSecond)).toList();
    }
}
