package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.utility.YesNoParsingFunction;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ParariusParser {
    private static final YesNoParsingFunction PETS_ALLOWED_PARSING_FUNCTION = new YesNoParsingFunction();

    public List<RentalOffering> parsePage(String docToParse) {
        if (docToParse.contains("Rental homes in the area")) return new ArrayList<>();
        Document document = Jsoup.parse(docToParse);
        Elements rentals = document.getElementsByClass("search-list__item search-list__item--listing");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : rentals) {
            RentalOffering rentalOffering = new RentalOffering();
            Elements title = rental.getElementsByClass("listing-search-item__link--title");
            rentalOffering.setName(!title.isEmpty() ? title.get(0).text() : null);
            rentalOffering.setUrl("https://pararius.com" + title.attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        Elements dts = document.getElementsByTag("dt");
        Elements dds = document.getElementsByTag("dd");

        for (int i = 0; i < dts.size(); i++) {
            if (dts.get(i).text().contains("Rental price")) {
                Element dd = dds.get(i);
                String price = dd.getElementsByClass("listing-features__main-description").text();
                if (!StringUtils.hasText(price)) price = dds.get(i).text();
                data.setPrice(price);
            }
            if (dts.get(i).text().contains("Available")) {
                data.setDate(dds.get(i).text());
            }
            if (dts.get(i).text().contains("Deposit")) {
                data.setDeposit(dds.get(i).text());
            }
            if (dts.get(i).text().contains("Status")) {
                data.setName(data.getName() + ": " + dds.get(i).text());
            }
            if (dts.get(i).text().contains("Pets allowed")) {
                data.setPetsAllowed(PETS_ALLOWED_PARSING_FUNCTION.apply(dds.get(i).text()));
            }
        }
    }

}
