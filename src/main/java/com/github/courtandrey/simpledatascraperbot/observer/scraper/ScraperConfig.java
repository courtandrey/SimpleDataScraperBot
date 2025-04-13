package com.github.courtandrey.simpledatascraperbot.observer.scraper;

import com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndName;
import com.github.courtandrey.simpledatascraperbot.entity.data.Data;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.lv.LatvijasPastsScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.movie.ImdbScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.vacancy.HHScraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.vacancy.HabrCareerScraper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode.EXTRA;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.ParsingMode.MAIN;

@Configuration
public class ScraperConfig {
    @Value("${app.vacancy.parsing-mode}")
    private String parsingMode;

    public HabrCareerScraper habrCareerScraper() {
        if (parsingMode.equals("extra")) {
            return new HabrCareerScraper(EXTRA);
        }
        return new HabrCareerScraper(MAIN);
    }

    public HHScraper hhScraper() {
        if (parsingMode.equals("extra")) {
            return new HHScraper(EXTRA);
        }
        return new HHScraper(MAIN);
    }

    public List<Scraper<? extends Data>> getScrapers() {
        List<Scraper<? extends Data>> scrapers = new ArrayList<>();
        scrapers.add(hhScraper());
        scrapers.add(habrCareerScraper());
        scrapers.add(new LatvijasPastsScraper());
        scrapers.add(new ImdbScraper());
        Arrays.stream(NLHousingSite.values()).sorted(Comparator.comparing(HasIdAndName::getId))
                .forEach(nlHousingSite -> scrapers.add(nlHousingSite.getScraper()));
        return scrapers;
    }

}

