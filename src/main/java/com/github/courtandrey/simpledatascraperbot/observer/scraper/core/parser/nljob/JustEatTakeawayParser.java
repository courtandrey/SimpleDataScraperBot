package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JustEatTakeawayParser {
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

        for (JsonNode job : node.get("refineSearch").get("data").get("jobs")) {
            JobOffering offering = new JobOffering();
            offering.setName(job.get("title").asText());
            offering.setCity(job.get("city").asText());
            offering.setUrl(job.get("applyUrl").asText());
            offerings.add(offering);
        }

        return offerings;
    }
}
