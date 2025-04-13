package com.github.courtandrey.simpledatascraperbot.bot.render;

import io.vavr.control.Try;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HasIdAndNameRenderingUtil {
    private HasIdAndNameRenderingUtil() {}

    public static <T extends HasIdAndName> String renderText(T[] hasIdAndNames) {
        return Stream.of(hasIdAndNames).sorted(Comparator.comparing(HasIdAndName::getId))
                .map(hasIdAndName -> String.format("%d. %s", hasIdAndName.getId(), hasIdAndName.getDisplayName()))
                .collect(Collectors.joining("\n"));
    }

    public static <T extends HasIdAndName> int getMax(T[] hasIdAndNames) {
        return Stream.of(hasIdAndNames).max(Comparator.comparing(HasIdAndName::getId)).map(HasIdAndName::getId).orElse(0);
    }

    public static <T extends HasIdAndName> boolean isValid(T[] hasIdAndNames, int number) {
        return Try.of(() -> fromValue(hasIdAndNames, number)).map(res -> true).getOrElse(() -> false);
    }

    public static <T extends HasIdAndName> T fromValue(T[] hasIdAndNames, int number) {
        return Arrays.stream(hasIdAndNames).filter(hasIdAndName -> hasIdAndName.getId() == number)
                .findAny().orElseThrow(() -> new UnsupportedOperationException("Unknown selection value!"));
    }
}
