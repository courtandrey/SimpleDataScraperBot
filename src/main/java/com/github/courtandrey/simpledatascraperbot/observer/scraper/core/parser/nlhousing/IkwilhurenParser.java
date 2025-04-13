package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class IkwilhurenParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("card-woning");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByClass("stretched-link").text());
            String url = rental.getElementsByAttribute("href").attr("href");
            if (!url.startsWith("https")) {
                url = "https://ikwilhuren.nu" + url;
            }
            rentalOffering.setUrl(url);
            rentalOffering.setPrice(rental.getElementsByClass("fw-bold").text());
            rentalOffering.setDate(rental.getElementsByClass("d-flex gap-1").text());
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
