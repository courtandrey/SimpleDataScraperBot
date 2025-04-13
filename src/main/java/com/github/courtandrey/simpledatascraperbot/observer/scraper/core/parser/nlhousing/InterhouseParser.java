package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class InterhouseParser {
    private final static List<String> AVAILABILITY = List.of("Beschikbaarheid:", "Availability:");

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByTag("li");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByClass("c-result-item__title-address").text());
            rentalOffering.setPrice(rental.getElementsByClass("c-result-item__price-label").text());
            rentalOffering.setStatus(rental.getElementsByClass("building-status").text());
            rentalOffering.setUrl(rental.getElementsByAttribute("href").attr("href"));
            for (Element el : rental.getElementsByClass("c-result-item__data-table-item")) {
                if (AVAILABILITY.contains(el.getElementsByClass("c-result-item__data-header").text())) {
                    rentalOffering.setDate(el.getElementsByClass("c-result-item__data-value").text());
                }
            }
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
