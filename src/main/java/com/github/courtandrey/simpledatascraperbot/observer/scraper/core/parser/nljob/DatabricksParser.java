package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DatabricksParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<JobOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json from databricks", exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse databricks jobs", exc))
                .getOrElse(new ArrayList<>());
    }

    private List<JobOffering> mapNode(JsonNode node) {
        List<JobOffering> offerings = new ArrayList<>();

        for (JsonNode job : node.get("result").get("pageContext").get("data").get("allGreenhouseJob").get("nodes")) {
            JobOffering offering = new JobOffering();
            offering.setName(job.get("title").asText());
            offering.setCity(job.get("location").get("name").asText());
            offering.setUrl(job.get("absolute_url").asText());
            offering.setUpdatedAt(job.get("updated_at").asText(null));
            offerings.add(offering);
        }

        return offerings;
    }
}
