package com.github.courtandrey.simpledatascraperbot.utility;

import com.github.courtandrey.simpledatascraperbot.entity.request.vacancy.Region;
import com.github.courtandrey.simpledatascraperbot.exception.RequestMappingException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMapper {
    public int mapRegionToHHInteger(Region region) {
        switch (region) {
            case UZBEKISTAN -> {
                return 97;
            }
            case KYRGYZSTAN -> {
                return 48;
            }
            case GEORGIA -> {
                return 28;
            }
            case BELARUS -> {
                return 16;
            }
            case AZERBAIJAN -> {
                return 9;
            }
            case KAZAKHSTAN -> {
                return 40;
            }
            case UKRAINE -> {
                return 5;
            }
            case RUSSIA -> {
                return 113;
            }
            case OTHER -> {
                return 1001;
            }
            default -> throw new RequestMappingException("Unknown region type");
        }
    }
}
