package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.Region;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HHVacancyRequestFunction implements RequestTransformation<HHVacancyRequest> {

    @Override
    public HHVacancyRequest apply(Map<Integer, Message> dialogChain) {
        HHVacancyRequest request = new HHVacancyRequest();
        request.setSearchText(dialogChain.get(10).getText());

        switch (dialogChain.get(11).getText()) {
            case "1" -> request.setExperience(HHVacancyRequest.Experience.NO);
            case "2" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_1_AND_3);
            case "3" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_3_AND_6);
            case "4" -> request.setExperience(HHVacancyRequest.Experience.MORE_THAN_6);
            case "5" -> request.setExperience(HHVacancyRequest.Experience.DOESNT_MATTER);
        }

        Set<Integer> regionInts =
                Arrays.stream(dialogChain.get(12).getText().trim().split(","))
                        .map(String::trim)
                        .filter(x -> x.length() > 0)
                        .map(Integer::parseInt)
                        .filter(x -> (x > 0) && (x < 11))
                        .collect(Collectors.toSet());
        Set<Region> regions = new HashSet<>();

        if (!regionInts.contains(1)) {
            for (int regionInt:regionInts) {
                switch (regionInt) {
                    case 2 -> regions.add(Region.RUSSIA);
                    case 3 -> regions.add(Region.UKRAINE);
                    case 4 -> regions.add(Region.AZERBAIJAN);
                    case 5 -> regions.add(Region.BELARUS);
                    case 6 -> regions.add(Region.KAZAKHSTAN);
                    case 7 -> regions.add(Region.KYRGYZSTAN);
                    case 8 -> regions.add(Region.UZBEKISTAN);
                    case 9 -> regions.add(Region.GEORGIA);
                    case 10 -> regions.add(Region.OTHER);
                }
            }
        }

        request.setRegions(regions);

        if (dialogChain.get(13).getText().equals("y")) {
            request.setRemote(true);
        }

        return request;
    }
}
