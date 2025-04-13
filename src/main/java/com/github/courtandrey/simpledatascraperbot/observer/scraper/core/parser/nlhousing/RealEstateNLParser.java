package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RealEstateNLParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : document.getElementsByClass("aw-card")) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(rental.getElementsByTag("h5").text());
            rentalOffering.setUrl(rental.getElementsByAttribute("href").attr("href"));
            rentalOffering.setCity(rental.getElementsByTag("p").text());
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        for (Element el : document.getElementsByClass("aw-prop-atts-list-item")) {
            Elements pair = el.getElementsByTag("p");
            if (pair.size() != 2) continue;

            if ("Vraagprijs".equalsIgnoreCase(pair.get(0).text())) {
                data.setPrice(pair.get(1).text());
            }

            if ("Status".equalsIgnoreCase(pair.get(0).text())) {
                data.setStatus(pair.get(1).text());
            }

            if ("Aanvaarding".equalsIgnoreCase(pair.get(0).text())) {
                data.setDate(pair.get(1).text());
            }
        }
    }
}
