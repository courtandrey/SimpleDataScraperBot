package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.of;

public class RentalRotterdamParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("object__holder");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            String status = of(rental.getElementsByClass("object__status_container").text())
                    .filter(StringUtils::hasText).orElse(null);
            rentalOffering.setStatus(status);
            rentalOffering.setName(rental.getElementsByClass("street").text());
            rentalOffering.setCity(rental.getElementsByClass("locality").text());
            String price = of(rental.getElementsByClass("price").text())
                    .filter(StringUtils::hasText).orElse(null);
            rentalOffering.setPrice(price);
            rentalOffering.setUrl("https://www.rentalrotterdam.nl" + rental.getElementsByClass("object__address-container").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        Elements dts = document.getElementsByTag("dt");
        Elements dds = document.getElementsByTag("dd");

        for (int i = 0; i < dts.size(); i++) {
            if (dts.get(i).text().contains("Aanvaarding")) {
                data.setDate(dds.get(i).text());
            }
        }
    }
}
