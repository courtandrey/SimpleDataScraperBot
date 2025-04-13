package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class KolpavvsParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("property-cards__single");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByClass("property__title").text());
            rentalOffering.setPrice(rental.getElementsByClass("property__price").text());
            rentalOffering.setDate(rental.getElementsByClass("property__label").text());
            String[] locationParts = rental.getElementsByClass("property__location").text().split(" ");
            rentalOffering.setCity(locationParts[locationParts.length - 1]);
            rentalOffering.setUrl("https://www.kolpavvs.nl/aanbod" + rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
