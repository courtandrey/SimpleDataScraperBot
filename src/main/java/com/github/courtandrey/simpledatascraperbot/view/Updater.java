package com.github.courtandrey.simpledatascraperbot.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.data.Data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Updater<T extends Data> {
    private final Class<T> clazz;
    private final String PATH_TO_SCRAPED;
    private final Retriever<T> retriever;

    public Updater(Class<T> clazz, String PATH_TO_SCRAPED) {
        this.clazz = clazz;
        this.PATH_TO_SCRAPED = PATH_TO_SCRAPED;
        this.retriever = new Retriever<>(clazz);
    }

    private void writeData(Set<Data> allVacancies) throws IOException {
        (new ObjectMapper()).writeValue(new FileWriter(PATH_TO_SCRAPED), allVacancies);
    }

    private List<T> getOldData() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.notExists(Path.of(PATH_TO_SCRAPED))) return new ArrayList<>();
        try (FileReader reader = new FileReader(PATH_TO_SCRAPED)) {
            return mapper.readerForListOf(clazz).readValue(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Data> update(List<Data> data) throws IOException {
        List<T> ts = retriever.retrieve(data);
        Set<Data> dataSet = new HashSet<>(ts);
        List<T> oldData = getOldData();
        dataSet.addAll(oldData);
        writeData(dataSet);
        data.removeAll(oldData);
        return new HashSet<>(data);
    }
}
