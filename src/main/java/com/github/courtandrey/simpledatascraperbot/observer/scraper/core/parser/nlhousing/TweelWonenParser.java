package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingUtil.textFromElements;

public class TweelWonenParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("object");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(textFromElements(rental.getElementsByClass("object_status")));
            rentalOffering.setName(textFromElements(rental.getElementsByClass("obj_address")));
            rentalOffering.setPrice(textFromElements(rental.getElementsByClass("obj_type_price")));
            rentalOffering.setUrl("https://www.tweelwonen.nl" + rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        for (Element tr : document.getElementsByTag("tr")) {
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() != 2) continue;

            if (tds.get(0).text().contains("Obtainable from")) {
                data.setDate(tds.get(1).text());
            }
        }
    }
}
