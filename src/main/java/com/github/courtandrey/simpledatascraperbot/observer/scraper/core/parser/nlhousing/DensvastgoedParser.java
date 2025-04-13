package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingUtil.textFromElements;

public class DensvastgoedParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("property vastgoed");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(textFromElements(rental.getElementsByTag("h3")));
            rentalOffering.setPrice(textFromElements(rental.getElementsByClass("price")));
            rentalOffering.setStatus(textFromElements(rental.getElementsByClass("label")));
            rentalOffering.setUrl(rental.attr("href"));
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
