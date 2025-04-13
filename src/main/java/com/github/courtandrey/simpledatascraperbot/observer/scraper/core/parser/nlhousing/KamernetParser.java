package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class KamernetParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RentalOffering> parsePage(String docToParse) {
        List<RentalOffering> offerings = Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json {}", docToParse, exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
        return offerings;
    }

    private List<RentalOffering> mapNode(JsonNode node) {
        List<RentalOffering> offerings = new ArrayList<>();

        for (JsonNode listing : node.get("listings")) {
            RentalOffering offering = new RentalOffering();
            offering.setUrl(getUrl(listing));
            offering.setDate(listing.get("availabilityStartDate").asText());
            offering.setName(listing.get("street").asText() + " " + listing.get("city").asText());
            offering.setPrice(listing.get("totalRentalPrice").asText());
            offerings.add(offering);
        }

        return offerings;
    }

    private String getUrl(JsonNode node) {
        int type = node.get("listingType").asInt();

        String typeName;
        if (type == 1) {
            typeName = "room";
        }
        else if (type == 2) {
            typeName = "appartment";
        }
        else if (type == 4) {
            typeName = "studio";
        }
        else {
            throw new RuntimeException("Unknown type " + type);
        }

        return String.format("https://kamernet.nl/en/for-rent/%s-%s/%s/%s-%d",
                typeName,
                node.get("citySlug").asText(),
                node.get("streetSlug").asText(),
                typeName,
                node.get("listingId").asInt());
    }
}
