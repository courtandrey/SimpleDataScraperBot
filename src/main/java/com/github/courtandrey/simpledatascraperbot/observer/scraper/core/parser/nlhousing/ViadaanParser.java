package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ViadaanParser {
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

        Document doc = Jsoup.parse(node.get("components").get(0).get("effects").get("html").asText());

        for (Element value : doc.getElementsByClass("relative grid gap-8 py-8 md:grid-cols-3")) {
            RentalOffering rentalOffering  = new RentalOffering();
            rentalOffering.setName(value.getElementsByTag("h3").text());
            rentalOffering.setCity(value.getElementsByClass("text-sm text-gray-600").text());
            rentalOffering.setPrice(value.getElementsByTag("b").text());
            rentalOffering.setUrl("https://viadaan.nl" + value.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }

        return offerings;
    }
}