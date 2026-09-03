package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndNameRenderingUtil;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nljob.NLJobSite;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.of;

public class NLJobRequestFunction implements RequestTransformation<NLJobRequest> {

    @Override
    public NLJobRequest apply(Map<Integer, Message> dialogChain) {
        NLJobRequest request = new NLJobRequest();

        request.setKeyword(of(dialogChain.get(60).getText()).filter(Predicate.not("none"::equalsIgnoreCase)).orElse(null));

        List<Integer> ints = Arrays.stream(dialogChain.get(61).getText().trim().split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .map(Integer::parseInt)
                .filter(number -> HasIdAndNameRenderingUtil.isValidOrSelectAll(NLJobSite.values(), number))
                .toList();

        Set<NLJobSite> nlJobSites = ints.contains(HasIdAndNameRenderingUtil.getSelectAllId(NLJobSite.values()))
                ? Arrays.stream(NLJobSite.values()).collect(Collectors.toSet())
                : ints.stream()
                        .map(number -> HasIdAndNameRenderingUtil.fromValue(NLJobSite.values(), number))
                        .collect(Collectors.toSet());

        request.getSites().addAll(nlJobSites);

        return request;
    }
}
