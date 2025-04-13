package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VbtverhuurmakelaarsParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RentalOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not find json on page", exc))
                .mapTry(this::extractOfferings)
                .onFailure(exc -> log.error("Could not parse json", exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> extractOfferings(JsonNode rootNode) {
        List<RentalOffering> offerings = new ArrayList<>();
        for (JsonNode node : rootNode.get("houses")) {
            RentalOffering offering = new RentalOffering();
            offering.setName(node.get("address").get("house").asText());
            offering.setDate(node.get("acceptance").asText());
            offering.setStatus(node.get("status").get("name").asText());
            offering.setPrice(node.get("prices").get("rental").get("price").asText());
            offering.setUrl("https://vbtverhuurmakelaars.nl/en" + node.get("url").asText());
            offerings.add(offering);
        }
        return offerings;
    }
}
