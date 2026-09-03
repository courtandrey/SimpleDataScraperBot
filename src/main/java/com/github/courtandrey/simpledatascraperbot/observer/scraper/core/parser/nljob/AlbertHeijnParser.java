package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AlbertHeijnParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<JobOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json {}", docToParse, exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
    }

    private List<JobOffering> mapNode(JsonNode node) {
        List<JobOffering> offerings = new ArrayList<>();

        for (JsonNode vacancy : node.get("vacancies")) {
            JobOffering offering = new JobOffering();
            offering.setName(vacancy.get("title").asText());
            offering.setCity(vacancy.get("city").asText());
            offering.setUrl("https://werk.ah.nl/vacature/" + vacancy.get("id").asText());
            offerings.add(offering);
        }

        return offerings;
    }
}
