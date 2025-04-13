package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ShepvastgoedmanagersParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("object col-xs-12");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            String href = rental.attr("href");
            if (!href.startsWith("http")) {
                href = "https://zoeken.schepvastgoedmanagers.nl" + href;
            }
            rentalOffering.setUrl(href);
            rentalOffering.setName(rental.getElementsByClass("straat").text());
            rentalOffering.setCity(rental.getElementsByClass("plaats").text());
            rentalOffering.setDate(rental.getElementsByClass("Beschikbaar").text());
            rentalOffering.setStatus(rental.getElementsByClass("statusbutton").text());
            rentalOffering.setPrice(rental.getElementsByClass("prijs").text());
            offerings.add(rentalOffering);
        }
        return offerings;
    }

}
