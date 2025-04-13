package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.AMSTERDAM;
import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.ROTTERDAM;

public class NLHousingRequestFunction implements RequestTransformation<NLHousingRequest>  {

    @Override
    public NLHousingRequest apply(Map<Integer, Message> dialogChain) {
        NLHousingRequest request = new NLHousingRequest();

        String city = switch (dialogChain.get(30).getText()) {
            case "1" -> AMSTERDAM.name();
            case "2" -> ROTTERDAM.name();
            case "3" -> dialogChain.get(31).getText();
            default -> null;
        };

        request.setCity(city);
        request.setLowestPrice(Integer.parseInt(dialogChain.get(32).getText()));
        String highestPrice = dialogChain.get(33).getText();
        request.setHighestPrice("UB".equalsIgnoreCase(highestPrice) ? null : Integer.parseInt(highestPrice));

        Boolean petsAllowed = switch (dialogChain.get(34).getText()) {
            case "1" -> true;
            case "2" -> false;
            default -> null;
        };

        request.setPetsAllowed(petsAllowed);

        Set<NLHousingSite> nlSites =
                Arrays.stream(dialogChain.get(35).getText().trim().split(","))
                        .map(String::trim)
                        .filter(x -> !x.isEmpty())
                        .map(Integer::parseInt)
                        .filter(number -> HasIdAndNameRenderingUtil.isValid(NLHousingSite.values(), number))
                        .map(number -> HasIdAndNameRenderingUtil.fromValue(NLHousingSite.values(), number))
                        .collect(Collectors.toSet());

        request.getSites().addAll(nlSites);

        return request;
    }
}
