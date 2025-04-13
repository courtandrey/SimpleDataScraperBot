package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

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
public class VanderlindenParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("zoekresultaat zoekresultaat3kol");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            Elements title = rental.getElementsByClass("objectgegevens");
            rentalOffering.setName(!title.isEmpty() ? title.get(0).text() : null);
            rentalOffering.setUrl("https://vanderlinden.nl" + rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        data.setPrice(document.getElementsByClass("vraagprijs").text().split("-")[0]);

        Try.of(() -> document.getElementsByClass("info").get(0))
                .onFailure(exc -> log.error("Could not set status for {}", data, exc))
                .peek(element -> element.getElementsByTag("li")
                        .stream().filter(el -> el.text().contains("Status")).findFirst()
                        .ifPresent(el -> data.setStatus(el.text())));
    }
}
