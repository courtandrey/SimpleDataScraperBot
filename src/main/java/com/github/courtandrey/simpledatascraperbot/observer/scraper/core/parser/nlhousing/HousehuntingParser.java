package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HousehuntingParser {
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

        JsonNode listings = node.get("posts");
        if (listings == null) return new ArrayList<>();

        for (JsonNode listing : listings) {
            RentalOffering offering = new RentalOffering();
            offering.setUrl(listing.get("url").asText());
            offering.setPrice(listing.get("price").asText());
            offering.setCity(listing.get("city").asText());
            offering.setName(listing.get("title").asText());
            offerings.add(offering);
        }

        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        Elements details = document.getElementsByClass("details");

        if (details.isEmpty()) return;

        for (Element el : details.get(0).getElementsByTag("li")) {
            if (el.text().contains("Available") || el.text().contains("Beschikbaar")) {
                data.setDate(el.text());
            }
        }
    }
}
