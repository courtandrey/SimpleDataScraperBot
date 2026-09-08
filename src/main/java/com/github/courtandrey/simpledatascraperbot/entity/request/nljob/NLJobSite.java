package com.github.courtandrey.simpledatascraperbot.entity.request.nljob;

import com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndName;
import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nljob.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NLJobSite implements HasIdAndName {
    JUST_EAT_TAKEAWAY("Just Eat Takeaway", new JustEatTakeawayScraper()),
    ALBERT_HEIJN("Albert Heijn", new AlbertHeijnScraper()),
    DATABRICKS("Databricks", new DatabricksScraper());

    private final String displayName;
    private final Scraper<JobOffering> scraper;

    @Override
    public int getId() {
        return this.ordinal() + 1;
    }
}
