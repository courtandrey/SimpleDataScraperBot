package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Slf4j
public class RentalOfferingRequestSatisfied implements BiPredicate<NLHousingRequest, RentalOffering> {

    @Override
    public boolean test(NLHousingRequest nlHousingRequest, RentalOffering rentalOffering) {
        if (nlHousingRequest.getPetsAllowed() != null && rentalOffering.getPetsAllowed() != null
             && !nlHousingRequest.getPetsAllowed().equals(rentalOffering.getPetsAllowed())) {
           return false;
        }

        if (rentalOffering.getPrice() != null) {
            boolean isViolated = Try.of(() -> parseDouble(rentalOffering.getPrice(), nlHousingRequest))
                    .onFailure(exc -> log.error("Could not parse string: {}. Price won't be filtered out. URL: {}", rentalOffering.getPrice(), rentalOffering.getUrl(), exc))
                    .filter(price -> price < nlHousingRequest.getLowestPrice() || price > nlHousingRequest.getHighestPrice())
                    .map(dbl -> true)
                    .getOrElse(false);
            if (isViolated) {
                return false;
            }
        }

        return nlHousingRequest.getCity() == null || rentalOffering.getCity() == null
                || nlHousingRequest.getCity().equalsIgnoreCase(rentalOffering.getCity());
    }

    private Double parseDouble(String dbl, NLHousingRequest request) {
        return Try.of(() -> {
                    String parsed = dbl.replaceAll("[^\\d.]", "");
                    if (parsed.startsWith(".")) parsed = parsed.substring(1);
                    if (parsed.endsWith(".")) parsed = parsed.substring(0, parsed.length() - 1);
                    return Double.parseDouble(parsed);
                })
                .filter(isAdequate(dbl, request))
                .fold(exc -> Optional.<Double>empty(), Optional::of)
                .or(() ->
                        Try.of(() -> Double.parseDouble(dbl.replaceAll("\\D", "")))
                                .filter(isAdequate(dbl, request))
                                .fold(exc -> Optional.empty(), Optional::of)
                )
                .or(() ->
                        Try.of(() -> {
                                    String parsingLine = dbl.replaceAll("\\.", "");
                                    parsingLine = parsingLine.replaceAll(",", ".");
                                    return Double.parseDouble(parsingLine.replaceAll("[^\\d.]", ""));
                                })
                                .filter(isAdequate(dbl, request))
                                .fold(exc -> Optional.empty(), Optional::of)
                ).orElseThrow(() -> new NumberFormatException("Could not adequately parse price: " + dbl));
    }

    private Predicate<Double> isAdequate(String initial, NLHousingRequest request) {
        return ( dbl) -> dbl > 100 && !(request.getHighestPrice() != null && dbl / request.getHighestPrice() > 10 & initial.contains(","));
    }
}
