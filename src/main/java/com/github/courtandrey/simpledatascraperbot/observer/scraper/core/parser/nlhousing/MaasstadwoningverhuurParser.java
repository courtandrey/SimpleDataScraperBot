package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingUtil.textFromElements;

public class MaasstadwoningverhuurParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("item-wrap-v1");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(textFromElements(rental.getElementsByClass("item-title")));
            rentalOffering.setStatus(textFromElements(rental.getElementsByClass("label-status")));
            rentalOffering.setPrice(textFromElements(rental.getElementsByClass("item-price")));
            rentalOffering.setUrl(rental.getElementsByClass("item-title").get(0)
                    .getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }
}
