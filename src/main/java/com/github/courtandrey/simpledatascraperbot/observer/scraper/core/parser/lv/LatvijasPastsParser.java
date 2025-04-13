package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.lv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.LatvijasPastsStatus;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class LatvijasPastsParser {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public List<LatvijasPastsStatus> parsePage(String docToParse) {
        try {
            JsonNode node = MAPPER.readValue(docToParse, JsonNode.class);
            JsonNode data = node.get("data");

            List<LatvijasPastsStatus> statuses = new ArrayList<>();
            for (JsonNode statusNode : data) {
                LatvijasPastsStatus status = new LatvijasPastsStatus();
                status.setDate(parseTime(statusNode.get("event_timestamp").asText()));
                status.setStatus(statusNode.get("event").asText());
                status.setGeneralStatus(statusNode.get("event_group_name").asText());
                status.setPlace(getPlace(statusNode));
                statuses.add(status);
            }
            return statuses.stream().sorted(Comparator.comparing(LatvijasPastsStatus::getDate)).toList();
        } catch (Exception e) {
            log.error("Could not read json from lv pasts api", e);
            return new ArrayList<>();
        }
    }

    private String getPlace(JsonNode node) {
        return Try.of(() -> node.get("country").asText())
                .orElse(Try.of(() -> node.get("office").asText()))
                .onFailure(exc -> log.error("Could not find place, defaulting to null {}", node.asText(), exc))
                .getOrElse(() -> null);
    }

    private LocalDateTime parseTime(String time) {
        return Try.of(() -> LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                .orElse(Try.of(() -> LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .onFailure(exc -> log.error("Could not parse time from string {}, will fallback to current time", time, exc))
                .getOrElse(LocalDateTime::now);
    }
}
