package com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing;

import com.github.courtandrey.simpledatascraperbot.bot.render.HasIdAndName;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NLHousingSite implements HasIdAndName {
    ROOMS_AND_HOUSES("Rooms and Houses (very slow and not useful, try to avoid)", new RoomsAndHousesScraper()),
    FUNDA("Funda", new FundaScraper()),
    PARARIUS("Pararius", new ParariusScraper()),
    KAMERNET("Kamernet (some radius is applied)", new KamernetScraper()),
    HOUSING_ANYWHERE("Housing Anywhere (some radius is applied)", new HousingAnywhereScraper()),
    VERHUURTBETER("Verhuubrtbeter", new VerhuurtbeterScraper()),
    WONENBIJBOUWINVEST("Wonenbijbouwinvest", new WonenbijbouwinvestScraper()),
    VBTVERHUUTMAKELAARS("Vbtverhuutmakelaars", new VbtverhuurmakelaarsScraper()),
    LIVRESIDENTIAL("Liveresidential", new LivresidentialScraper()),
    VANDERLINDEN("Vanderlinden (only RT/AM)", new VanderlindenScraper()),
    SHEPVASTGOEDMANAGERS("Shepvastgoedmanagers", new ShepvastgoedmanagersScraper()),
    IKWILHUREN("Ikwilhuren", new IkwilhurenScraper()),
    ROTTERDAMAPARTMENTS("Rotterdam Apartments", new RotterdamApartmentsScraper()),
    VHPN("Vhpn", new VhpnScraper()),
    KOLPAVVS("Kolpavvs", new KolpavvsScraper()),
    INTERHOUSE("Interhouse (only RT/AM)", new InterhouseScraper()),
    WOONZEKER("Woonzeker", new WoonzekerScraper()),
    NATIONAALGRONDBEZIT("Nationall Grondbezit (no city filtering)", new NationaalGrondbesitScraper()),
    REALESTATENL("realestate.nl", new RealEstateNLScraper()),
    HOUSEHUNTING("Househunting (inefficient scraper)", new HousehuntingScraper()),
    HURENBIJWOOVE("Hurenbijwooove", new HurenbijwoooveScraper()),
    PERFECT_RENT("Perfect Rent", new PerfectRentScraper()),
    RENTAL_ROTTERDAM("Rental Rotterdam", new RentalRotterdamScraper()),
    ROTTERDAM_WONEN("Rotterdam Wonen", new RotterdamWonenScraper()),
    TWEEL_WONEN("Tweel Wonen", new TweelWonenScraper()),
    RIVA_RENTAILS("Riva Rentals", new RivaRentalsScraper()),
    ROTTERDAM_RENTAL_SERVICE("Rotterdam Rental Service", new RotterdamRentalServiceScraper()),
    CITY_BIRD("City Bird", new CityBirdScraper()),
    RENT_AN_APARTMENT("Rent an apartment", new RentAnApartmentScraper()),
    WONENHARTJE("Wonenhartje", new WonenhartjeScraper()),
    DENSVASTGOED("Densvastgoed", new DensvastgoedScraper()),
    DOMICA("Domica", new DomicaScraper()),
    MAX_RENTAL("Max Rental", new MaxRentalScraper()),
    VIADAAN("Viadaan", new ViadaanScraper()),
    GAPPH("Gapph", new GapphScraper()),
    MAASTADWONINGVERHUUR("Maasstadwoningverhuur", new MaasstadwoningverhuurScraper()),
    VGWGROUP("Vgwgroup", new VgwgroupScraper());

    private final String displayName;
    private final Scraper<RentalOffering> scraper;

    @Override
    public int getId() {
        return this.ordinal() + 1;
    }
}
