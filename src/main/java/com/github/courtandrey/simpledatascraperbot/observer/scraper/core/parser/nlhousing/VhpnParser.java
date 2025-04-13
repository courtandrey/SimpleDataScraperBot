package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class VhpnParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        int postNumber = 1;
        List<RentalOffering> offerings = new ArrayList<>();

        while (!document.getElementsByClass("post-" + postNumber).isEmpty()) {
            for (Element rental : document.getElementsByClass("post-" + postNumber)) {
                RentalOffering rentalOffering = new RentalOffering();
                rentalOffering.setUrl("https://www.vhpn.nl/" + rental.getElementsByAttributeValue("rel", "bookmark").attr("href"));
                rentalOffering.setName(rental.getElementsByAttribute("title").attr("title"));
                rentalOffering.setDate(rental.getElementsByClass("details-transfer-text").text());
                rentalOffering.setPrice(rental.getElementsByClass("details-price").text());
                offerings.add(rentalOffering);
            }
            postNumber += 1;
        }

        return offerings;
    }
}
