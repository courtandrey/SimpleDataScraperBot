package com.github.courtandrey.simpledatascraperbot.view;

import com.github.courtandrey.simpledatascraperbot.data.Data;

import java.util.ArrayList;
import java.util.List;

class Retriever<T extends Data> {
    private final Class<T> clazz;

    public Retriever(Class<T> clazz) {
        this.clazz = clazz;
    }

    private boolean needToRetrieve(Data data) {
        return data.getClass().equals(clazz);
    }
    List<T> retrieve(List<Data> data) {
        List<T> vacancies = new ArrayList<>();
        for (Data d:data) {
            if (this.needToRetrieve(d)) {
                vacancies.add(clazz.cast(d));
            }
        }
        return vacancies;
    }
}
