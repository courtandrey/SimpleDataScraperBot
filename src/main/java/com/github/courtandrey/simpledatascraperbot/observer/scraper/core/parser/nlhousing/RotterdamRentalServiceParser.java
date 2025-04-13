package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RotterdamRentalServiceParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RentalOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json {}", docToParse, exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> mapNode(JsonNode node) {
        List<RentalOffering> offerings = new ArrayList<>();

        for (JsonNode listing : node.get("property")) {
            RentalOffering offering = new RentalOffering();
            offering.setUrl("https://rotterdamrentalservice.com/huren-rotterdam/" + listing.get("property_title").asText());
            offering.setStatus(listing.get("labels").asText());
            offering.setCity(listing.get("city").asText());
            offering.setPrice(listing.get("monthly_rent").asText());
            offering.setDate(listing.get("availability").asText());
            offering.setName(listing.get("address").asText());
            offerings.add(offering);
        }

        return offerings;
    }
}
