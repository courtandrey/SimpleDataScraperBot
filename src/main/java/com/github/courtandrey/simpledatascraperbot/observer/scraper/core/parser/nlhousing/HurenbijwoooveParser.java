package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HurenbijwoooveParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("col-xs-12 col-sm-6 col-md-4");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByClass("straat").text());
            rentalOffering.setCity(rental.getElementsByClass("plaats").text());
            rentalOffering.setUrl("https://hurenbijwooove.nl" + rental.attr("href"));
            rentalOffering.setStatus(rental.getElementsByClass("statusbutton").text());
            Optional.of(rental.getElementsByClass("prijs").text())
                    .map(str -> str.replace("Huurprijs:", ""))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .ifPresent(rentalOffering::setPrice);
            offerings.add(rentalOffering);
        }
        return offerings;
    }

}
