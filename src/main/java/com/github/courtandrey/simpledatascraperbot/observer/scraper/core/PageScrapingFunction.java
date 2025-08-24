package com.github.courtandrey.simpledatascraperbot.observer.scraper.core;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.IConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.RequestPagingContext;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PageScrapingFunction<T,R extends Request> implements Function<List<Request>, List<Pair<Request, Processee<T>>>> {
    private final Function<R, IConnector> connectorSupplier;
    private final Function<String, List<T>> pageParsing;
    private final Class<R> rClass;

    private Consumer<T> dataCallback = data -> {};
    private BiConsumer<T, R> dataReqPostProcessing = (data, req) -> {};
    private Consumer<T> dataPostProcessing = data -> {};
    private BiPredicate<R,T> validityPredicate = (req, data) -> true;
    private BiPredicate<R,T> filteringCondition = (req, data) -> true;
    private boolean singlePage = false;
    private int startingPage = 1;
    private boolean sameOutputForAllRequests = false;
    private boolean reqsInParallel = false;
    private Function<R, IConnector> fallbackConnector = null;
    private Predicate<String> responsePredicate = response -> false;

    public PageScrapingFunction<T,R> withReqDataPostProcessing(BiConsumer<T,R> dataReqPostProcessing) {
        this.dataReqPostProcessing = this.dataReqPostProcessing.andThen(dataReqPostProcessing);
        return this;
    }

    public PageScrapingFunction<T,R> withPostProcessing(Consumer<T> dataPostProcessing) {
        this.dataPostProcessing = this.dataPostProcessing.andThen(dataPostProcessing);
        return this;
    }

    public PageScrapingFunction<T,R> withStartingPage(int startingPage) {
        this.startingPage = startingPage;
        return this;
    }

    public PageScrapingFunction<T,R> withSendingOutCondition(BiPredicate<R,T> validityPredicate) {
        this.validityPredicate = this.validityPredicate.and(validityPredicate);
        return this;
    }

    public PageScrapingFunction<T,R> withFilteringCondition(BiPredicate<R,T> filteringCondition) {
        this.filteringCondition = this.filteringCondition.and(filteringCondition);
        return this;
    }

    public PageScrapingFunction<T,R> withDataCallback(Consumer<T> callback) {
        this.dataCallback = this.dataCallback.andThen(callback);
        return this;
    }

    public PageScrapingFunction<T,R> singlePage(boolean singlePage) {
        this.singlePage = singlePage;
        return this;
    }

    public PageScrapingFunction<T,R> sameOutput(boolean sameOutputForAllRequests) {
        this.sameOutputForAllRequests = sameOutputForAllRequests;
        return this;
    }

    public PageScrapingFunction<T,R> processReqsInParallel(boolean reqsInParallel) {
        this.reqsInParallel = reqsInParallel;
        return this;
    }

    public PageScrapingFunction<T,R> withFallbackConnector(Function<R, IConnector> fallbackConnector) {
        this.fallbackConnector = fallbackConnector;
        return this;
    }

    public PageScrapingFunction<T,R> withStopPaginationPredicate(Predicate<String> responsePredicate) {
        this.responsePredicate = responsePredicate;
        return this;
    }

    @Override
    public List<Pair<Request, Processee<T>>> apply(List<Request> reqs) {
        List<Pair<Request, Processee<T>>> allData = reqsInParallel ? new CopyOnWriteArrayList<>() : new ArrayList<>();
        List<R> requests = reqs.stream().filter(rClass::isInstance).map(rClass::cast).toList();
        Stream<R> stream = reqsInParallel ? requests.parallelStream() : requests.stream();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        stream.forEach(req -> {
            if (shouldStop.get()) return;

            int pageNum = startingPage;
            IConnector connector = connectorSupplier.apply(req);

            int prevSize;
            int currentSize;
            Set<T> reqData = new HashSet<>();
            String previousPage = null;
            boolean fallbackInUse = false;
            do {
                prevSize = reqData.size();
                RequestPagingContext context = RequestPagingContext.builder().currentPage(pageNum).previousResponse(previousPage).build();
                String page = singlePage ? connector.connect(0) : connector.connectPageSearch(context, 0);
                if (responsePredicate.test(page)) {
                    break;
                }
                previousPage = page;
                List<T> data = pageParsing.apply(page).stream().peek(dt -> dataPostProcessing.accept(dt)).toList();
                reqData.addAll(data);
                currentSize = reqData.size();
                pageNum += 1;
                if (prevSize == currentSize && prevSize == 0 && fallbackConnector != null && !fallbackInUse) {
                    connector = fallbackConnector.apply(req);
                    pageNum = startingPage;
                    previousPage = null;
                    fallbackInUse = true;
                    prevSize = -1;
                }
            } while (prevSize != currentSize && !singlePage);

            if (sameOutputForAllRequests) {
                for (R r2 : requests) {
                    allData.addAll(reqData.stream()
                            .map(dt -> Pair.of((Request) r2, getProcessee(r2, dt)))
                            .toList());
                }
                shouldStop.set(true);
                return;
            }
            allData.addAll(reqData.stream().map(dt -> Pair.of((Request) req, getProcessee(req, dt))).toList());
        });
        return allData.stream().filter(pair -> filteringCondition.test(rClass.cast(pair.getFirst()), pair.getSecond().unwrap())).toList();
    }

    private Processee<T> getProcessee(R req, T data) {
        dataReqPostProcessing.accept(data, req);
        Processee<T> processee = Processee.of(data);
        return processee
                .withCallback(dataCallback)
                .validIf(dt -> validityPredicate.test(req, dt));
    }
}
