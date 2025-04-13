package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LivresidentialParser {
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
        for (JsonNode node : rootNode.get("hits")) {
            RentalOffering offering = new RentalOffering();
            offering.setName(node.get("address_1").asText());
            offering.setDate(node.get("available_at").asText());
            offering.setStatus(node.get("status").asText());
            offering.setPrice(String.valueOf(node.get("price").asInt()));
            offering.setUrl("https://livresidential.nl/en/properties/" + node.get("slug_city").asText() +
                    "/" + node.get("slug_neighborhood").asText() + "/" + node.get("slug_street").asText());
            offerings.add(offering);
        }
        return offerings;
    }
}
