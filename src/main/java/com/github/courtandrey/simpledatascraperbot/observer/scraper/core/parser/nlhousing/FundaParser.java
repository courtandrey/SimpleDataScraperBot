package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class FundaParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByAttributeValue("data-testid", "listingDetailsAddress");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            Elements truncate = rental.getElementsByClass("truncate");
            rentalOffering.setName(!truncate.isEmpty() ? truncate.get(0).text() : null);
            rentalOffering.setUrl("https://www.funda.nl" + rental.attr("href"));
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

        Elements prices = document.getElementsByClass("flex flex-col text-xl");
        data.setPrice(prices.isEmpty() ? null : prices.get(0).text());
    }
}
