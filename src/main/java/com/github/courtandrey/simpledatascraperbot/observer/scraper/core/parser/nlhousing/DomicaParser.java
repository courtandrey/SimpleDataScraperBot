package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DomicaParser {
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

        JsonNode values = node.get("values");

        for (JsonNode value : values) {
          JsonNode data = value.get("data");
          RentalOffering rentalOffering  = new RentalOffering();
          rentalOffering.setCity(data.get("tmp_city").asText());
          rentalOffering.setName(data.get("tmp_streetAddress").asText());
          rentalOffering.setStatus(data.get("tmp_label").asText());
          rentalOffering.setPrice(data.get("tmp_price").asText());
          rentalOffering.setDeposit(data.get("financials").get("deposit").asText());
          rentalOffering.setDate(data.get("property_offer").get("acceptance_type").asText());
          rentalOffering.setUrl("https://www.domica.nl/woning/" + value.get("page_item_url").asText());
          offerings.add(rentalOffering);
        }

        return offerings;
    }
}
