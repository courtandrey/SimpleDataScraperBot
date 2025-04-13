package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class VerhuurtbeterParser {

    public List<RentalOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        Elements allElements = document.getElementsByAttribute("data-object-id");
        List<RentalOffering> offerings = new ArrayList<>();
        for (Element el : allElements) {
            Elements meta = el.getElementsByClass("d-none");
            if (meta.isEmpty()) continue;
            RentalOffering offering = new RentalOffering();
            offering.setCity(meta.get(0).getElementsByClass("filter-plaats").text());
            String price = el.attr("data-price");
            int lengthOfTrimming = 2;
            if (!el.getElementsByClass("infoBtn").isEmpty()) {
                lengthOfTrimming = 3;
            }
            offering.setPrice(price.length() <= lengthOfTrimming ? null : price.substring(0, price.length() - lengthOfTrimming));
            offering.setUrl("https://verhuurtbeter.nl" + el.attr("data-url"));
            Elements name = el.getElementsByClass("f-address");
            offering.setName(name.isEmpty() ? null : name.get(0).text());
            Elements date = el.getElementsByClass("f-available");
            offering.setDate(date.isEmpty() ? null : date.get(0).text());
            offerings.add(offering);
        }
        return offerings;
    }

}
