package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NationaalGrondbesitParser {

    public List<RentalOffering> parsePage(String docToParse) {
        return Try.of(() -> docToParse.split(",filterData")[0].split(",rent:")[1])
                .mapTry(this::parseDoc)
                .onFailure(exc -> log.error("Could not parse doc", exc))
                .getOrElse(new ArrayList<>());
    }

    private List<RentalOffering> parseDoc(String doc) {
        List<RentalOffering> offerings = new ArrayList<>();
        String docToParse = doc.replace("[{id:", "");
        for (String node : docToParse.split(",\\{id:")) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setUrl("https://www.nationaalgrondbezit.nl/huuraanbod/" + node.split(",external_id:\"")[0]);
            rentalOffering.setName(node.split("title:\"")[1].split("\"")[0]);
            rentalOffering.setPrice(node.split("\\{price:")[1].split(",")[0]);
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
