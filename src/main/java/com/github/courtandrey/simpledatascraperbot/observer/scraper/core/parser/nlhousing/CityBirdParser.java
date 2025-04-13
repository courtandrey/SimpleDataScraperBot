package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingUtil.textFromElements;

public class CityBirdParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("col-xl-3 col-lg-4 col-md-6");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            if (!rental.getElementsByClass("status-rented").isEmpty()) {
                rentalOffering.setStatus("Rented");
            } else if (!rental.getElementsByClass("status-available").isEmpty()) {
                rentalOffering.setStatus("Available");
                rentalOffering.setDate(textFromElements(rental.getElementsByClass("status-available")));
            }
            rentalOffering.setName(textFromElements(rental.getElementsByClass("cr-card-title")));

            Elements ps = rental.getElementsByTag("p");
            if (ps.size() == 3) {
                rentalOffering.setCity(ps.get(1).text().split("\\(")[0].trim());
                rentalOffering.setPrice(ps.get(2).text());
            }

            rentalOffering.setUrl("https://www.citybird-rentals.com" + rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
