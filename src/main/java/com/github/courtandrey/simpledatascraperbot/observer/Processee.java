package com.github.courtandrey.simpledatascraperbot.observer;


import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

public final class Processee<T> {
    private Processee(T data) {
        this.data = data;
    }

    private final T data;

    private Consumer<T> action = data -> {};

    private Predicate<T> isValid = data -> true;

    public static <T> Processee<T> of(T data) {
        return new Processee<>(data);
    }

    public Processee<T> validIf(Predicate<T> isValid) {
        this.isValid = this.isValid.and(isValid);
        return this;
    }

    public Processee<T> withCallback(Consumer<T> action) {
        this.action = this.action.andThen(action);
        return this;
    }

    public void accept(Consumer<T> action) {
        action.accept(data);
    }

    public Pair<T, Processee<T>> toPair() {
        return Pair.of(data, this);
    }

    public T unwrap() {
        return data;
    }

    public void callAndReset() {
        action.accept(data);
        action = dt -> {};
    }

    public Optional<T> getValid() {
        Optional<T> dataOpt = Optional.ofNullable(data);
        dataOpt.ifPresent(action);
        return dataOpt.filter(isValid);
    }

    public <R> R transform(Function<T,R> function) {
        return function.apply(data);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Processee<?> processee = (Processee<?>) o;
        return Objects.equals(data, processee.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
