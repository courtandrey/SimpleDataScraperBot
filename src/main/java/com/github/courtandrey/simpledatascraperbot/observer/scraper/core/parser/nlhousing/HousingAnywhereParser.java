package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HousingAnywhereParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        return Try.of(() -> document.getElementsByAttributeValue("nonce", "the-nonce").html().split("JSON\\.parse\\(\"")[1])
                .mapTry(preparedJson -> MAPPER.readValue(preparedJson.substring(0, preparedJson.length() - 2).replace("\\", ""), JsonNode.class))
                .onFailure(exc -> log.error("Could not find json on page", exc))
                .mapTry(this::extractOfferings)
                .onFailure(exc -> log.error("Could not parse json", exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> extractOfferings(JsonNode rootNode) {
        List<RentalOffering> offerings = new ArrayList<>();
        rootNode.get("loaderData").elements().forEachRemaining(element -> {
            if (element.get("listings") != null) {
                for (JsonNode node : element.get("listings")) {
                    RentalOffering offering = new RentalOffering();
                    offering.setDate(node.get("bookableDateFromStart").asText());
                    offering.setPrice(node.get("priceEUR").asText());
                    offering.setUrl("https://housinganywhere.com" + node.get("path").asText());
                    offering.setName(node.get("advertiserFirstName").asText());
                    offerings.add(offering);
                }
            }
        });
        return offerings;
    }
}
