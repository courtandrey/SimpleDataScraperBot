package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BookingParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String JOB_URL = "https://jobs.booking.com/booking/jobs/$SLUG";

    public List<JobOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json from booking", exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse booking jobs", exc))
                .getOrElse(new ArrayList<>());
    }

    private List<JobOffering> mapNode(JsonNode node) {
        List<JobOffering> offerings = new ArrayList<>();

        for (JsonNode wrapper : node.get("jobs")) {
            JsonNode job = wrapper.get("data");
            JobOffering offering = new JobOffering();
            offering.setName(job.get("title").asText());
            offering.setCity(job.path("full_location").asText(job.path("city").asText(null)));
            offering.setUrl(JOB_URL.replace("$SLUG", job.get("slug").asText()));
            offering.setUpdatedAt(job.path("update_date").asText(null));
            offerings.add(offering);
        }

        return offerings;
    }
}
