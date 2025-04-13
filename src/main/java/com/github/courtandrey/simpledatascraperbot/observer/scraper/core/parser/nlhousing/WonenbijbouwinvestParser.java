package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

@Slf4j
public class WonenbijbouwinvestParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Collection<RentalOffering> parse(String docToParse) {
        return Try.of(() -> Jsoup.parse(docToParse))
                .onFailure(exc -> log.error("Could not parse doc {}", docToParse, exc))
                .mapTry(this::mapDocument)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
    }

    private Collection<RentalOffering> mapDocument(Document document) {
        Set<RentalOffering> offerings = new HashSet<>();
        for (Element el : document.getElementsByAttribute("data-bindkey")) {
            RentalOffering offering = new RentalOffering();
            offering.setName(el.getElementsByTag("h3").text());
            offering.setPrice(el.getElementsByClass("price-tag").text());
            offering.setUrl(el.getElementsByAttribute("href").attr("href"));
            offering.setDate(el.getElementsByClass("sticker-bar__top").text());
            offerings.add(offering);
        }
        return offerings;
    }

    public List<RentalOffering> parsePage(String docToParse) {
        return Try.of(() -> MAPPER.readValue(docToParse, JsonNode.class))
                .onFailure(exc -> log.error("Could not parse json {}", docToParse, exc))
                .mapTry(this::mapNode)
                .onFailure(exc -> log.error("Could not parse {}", docToParse, exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> mapNode(JsonNode node) {
        List<RentalOffering> offerings = new ArrayList<>();

        for (JsonNode listing : node.get("data")) {
            RentalOffering offering = new RentalOffering();
            offering.setUrl(listing.get("url").asText().replace("\\", ""));
            offering.setName(listing.get("name").asText());
            offering.setPrice(getPrice(listing));
            offerings.add(offering);
        }

        return offerings;
    }

    private String getPrice(JsonNode node) {
        return Try.of(() -> node.get("price").get("price").asInt())
                .orElse(Try.of(() -> node.get("price").get("till").asInt())
                        .filter(price -> price > 0)
                        .orElse(Try.of(() -> node.get("price").get("from").asInt())))
                .onFailure(exc -> log.error("Could not parse price {}", node, exc))
                .map(String::valueOf)
                .getOrElse(() -> null);
    }
}
