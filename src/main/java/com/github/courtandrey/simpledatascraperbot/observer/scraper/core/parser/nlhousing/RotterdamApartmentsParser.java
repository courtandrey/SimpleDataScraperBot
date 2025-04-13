package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RotterdamApartmentsParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("house-item");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByClass("house-item__title").text());
            rentalOffering.setPrice(rental.getElementsByClass("house-item__price").text());
            rentalOffering.setDate(rental.getElementsByClass("house-item__info").text());
            rentalOffering.setUrl("https://rotterdamapartments.com" + rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
