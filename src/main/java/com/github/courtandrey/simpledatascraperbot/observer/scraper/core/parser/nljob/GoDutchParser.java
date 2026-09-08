package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nljob;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GoDutchParser {

    public List<JobOffering> parsePage(String docToParse) {
        Document document = Jsoup.parse(docToParse);
        List<JobOffering> offerings = new ArrayList<>();
        for (Element card : document.select("div[data-framer-name=Variant 1]")) {
            Elements links = card.select("a[href*=teamtailor.com/jobs/]");
            if (links.isEmpty()) continue;
            JobOffering offering = new JobOffering();
            offering.setName(card.select("h4").text());
            Elements details = card.select("p:not(a p)");
            offering.setCity(details.isEmpty() ? null : details.last().text());
            offering.setUrl(links.get(0).attr("href"));
            offerings.add(offering);
        }
        return offerings;
    }
}
