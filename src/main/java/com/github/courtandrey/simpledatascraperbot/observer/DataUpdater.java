package com.github.courtandrey.simpledatascraperbot.observer;

import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.RequestToData;
import com.github.courtandrey.simpledatascraperbot.service.DataCleaner;
import io.vavr.control.Try;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataUpdater {
    private final DataCleaner cleaner;
    private final EntityManager entityManager;

    @Transactional
    public Collection<Processee<Data>> update(List<Pair<Request, Processee<Data>>> data) {
        Collection<Pair<Request, Processee<Data>>> uniqueData = cleaner.getUnique(data);
        uniqueData.parallelStream().peek(pair -> pair.getSecond().callAndReset())
                .toList()
                .forEach(pair -> Try.run(() -> persist(pair))
                        .onFailure(exc -> log.error("Could not persist data {}", pair.getSecond().unwrap(), exc)));
        return uniqueData.stream().map(Pair::getSecond).toList();
    }

    @Transactional
    private void persist(Pair<Request, Processee<Data>> pair) {
        pair.getSecond().accept(entityManager::persist);
        RequestToData requestToData = new RequestToData();
        requestToData.setRequest(pair.getFirst());
        requestToData.setData(pair.getSecond().unwrap());
        entityManager.persist(requestToData);
    }
}
