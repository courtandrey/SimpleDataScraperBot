package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PerfectRentParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RentalOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode[].class))
                .onFailure(exc -> log.error("Could not parse json {}", docToParse, exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> mapNode(JsonNode[] nodes) {
        List<RentalOffering> offerings = new ArrayList<>();

        for (JsonNode listing : nodes) {
            RentalOffering offering = new RentalOffering();
            offering.setUrl("https://www.perfectrent.nl" + listing.get("url").asText());
            offering.setStatus(listing.get("status").asText());
            offering.setCity(listing.get("city").asText());
            offering.setPrice(listing.get("price").asText());
            offering.setDate(listing.get("acceptance").asText());
            offering.setName(listing.get("address").asText());
            offerings.add(offering);
        }

        return offerings;
    }
}
