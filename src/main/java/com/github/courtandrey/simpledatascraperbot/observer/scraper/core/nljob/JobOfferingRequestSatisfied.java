package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nljob;

import com.github.courtandrey.simpledatascraperbot.entity.data.JobOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobRequest;

import java.util.function.BiPredicate;

public class JobOfferingRequestSatisfied implements BiPredicate<NLJobRequest, JobOffering> {

    @Override
    public boolean test(NLJobRequest nlJobRequest, JobOffering jobOffering) {
        return nlJobRequest.getKeyword() == null || jobOffering.getName() == null
                || jobOffering.getName().toLowerCase().contains(nlJobRequest.getKeyword().toLowerCase());
    }
}
