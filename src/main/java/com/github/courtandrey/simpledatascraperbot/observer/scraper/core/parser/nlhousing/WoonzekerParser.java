package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WoonzekerParser {
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

        node.get("data").elements().forEachRemaining(
                listing -> Try.of(() -> {
                    RentalOffering offering = new RentalOffering();
                    offering.setUrl("https://woonzeker.com/huur/woningen/" + listing.get("slug").asText());
                    offering.setName(listing.get("title").asText());
                    offering.setStatus(listing.get("status").get("label").asText());
                    offering.setPrice(listing.get("handover").get("price").asText());
                    offering.setDeposit(listing.get("handover").get("deposit").asText());
                    return offering;
                })
                        .onFailure(exc -> log.error("Could not parse listing {}", listing, exc))
                        .onSuccess(offerings::add)
        );
        return offerings;
    }
}
