package com.github.courtandrey.simpledatascraperbot.view;

import com.github.courtandrey.simpledatascraperbot.data.Data;
import com.github.courtandrey.simpledatascraperbot.data.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdaterFactory {
    public List<Updater<? extends Data>> updaters;

    public UpdaterFactory() {
        updaters = new ArrayList<>();
        updaters.add(new Updater<>(Vacancy.class, "./src/main/resources/vacancies.json"));
    }

    public Set<Data> update(List<Data> data) throws IOException {
        Set<Data> newData = new HashSet<>();
        for (Updater<? extends Data> updater:updaters) {
            newData.addAll(updater.update(data));
        }
        return newData;
    }
}
