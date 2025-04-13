package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.IConnector;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.AMSTERDAM;
import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.ROTTERDAM;

public class NLHousingFunctions {

    public static Consumer<RentalOffering> visitRentalOffering(BiConsumer<String, RentalOffering> parseOffering,
                                                               Function<String, IConnector> connectorFromUrl) {
        return rentalOffering ->
                parseOffering.accept(connectorFromUrl.apply(rentalOffering.getUrl()).connect(0), rentalOffering);
    }

    public static BiConsumer<RentalOffering, NLHousingRequest> cityFromRequest() {
        return (offering, req) -> offering.setCity(req.getCity());
    }

    public static Function<String, String> parseCity() {
        return city -> {
            if (AMSTERDAM.name().equalsIgnoreCase(city)) {
                return "Amsterdam";
            }
            if (ROTTERDAM.name().equalsIgnoreCase(city)) {
                return "Rotterdam";
            }
            return city.replace(" ", "-");
        };
    }

    public static Function<String, String> parseCityLowerCased() {
        return parseCity().andThen(String::toLowerCase);
    }
}
