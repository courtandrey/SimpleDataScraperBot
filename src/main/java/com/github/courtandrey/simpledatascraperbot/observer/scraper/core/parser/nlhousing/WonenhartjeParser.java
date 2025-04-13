package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingUtil.textFromElements;
import static java.util.Optional.ofNullable;

public class WonenhartjeParser {
    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements list = document.getElementsByClass("object-list-page");
        if (list.isEmpty()) return new ArrayList<>();
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element rental : list.get(0).getElementsByTag("li")) {
            if (!"Te huur".equalsIgnoreCase(textFromElements(rental.getElementsByClass("gpoStatus")))){
                continue;
            }
            RentalOffering rentalOffering = new RentalOffering();
            rentalOffering.setName(textFromElements(rental.getElementsByTag("strong")));
            rentalOffering.setPrice(textFromElements(rental.getElementsByClass("gpoObjectPrice")));
            rentalOffering.setUrl(rental.getElementsByAttribute("href").attr("href"));
            offerings.add(rentalOffering);
        }
        return offerings;
    }

    public void addDetails(String docToParse, RentalOffering data) {
        Document document = Jsoup.parse(docToParse);

        for (Element item : document.getElementsByClass("mkdf-spec-item")) {
            if (ofNullable(textFromElements(item.getElementsByClass("mkdf-spec-item-label")))
                    .filter(text -> text.contains("Beschikbaar per")).isPresent()) {
                data.setDate(textFromElements(item.getElementsByClass("mkdf-spec-item-value")));
            }
        }

        data.setName(data.getName() + ": " + textFromElements(document.getElementsByClass("mkdf-property-status")));
    }
}
