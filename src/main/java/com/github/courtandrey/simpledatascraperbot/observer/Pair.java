package com.github.courtandrey.simpledatascraperbot.observer;

import java.util.function.Function;

public final class Pair<F,S> {
    private final F firstEntity;
    private final S secondEntity;

    private Pair(F firstEntity, S secondEntity) {
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
    }

    public static <F,S> Pair<F,S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public F getFirst() {
        return firstEntity;
    }

    public S getSecond() {
        return secondEntity;
    }

    public <T> Pair<T,S> mapFirst(Function<F,T> convertingFunction) {
        return new Pair<>(convertingFunction.apply(firstEntity), secondEntity);
    }

    public <T> Pair<F,T> mapSecond(Function<S,T> convertingFunction) {
        return new Pair<>(firstEntity, convertingFunction.apply(secondEntity));
    }
}
