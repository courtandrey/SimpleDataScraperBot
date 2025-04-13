package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.IConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.POSTConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.ViadaanParser;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.ConnectionUtil.getAllCookies;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.StreamUtil.writeToString;

@Slf4j
public class ViadaanScraper implements Scraper<RentalOffering> {
    private final ViadaanParser parser = new ViadaanParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return Try.of(() -> new PageScrapingFunction<>(
                getConnector(),
                parser::parsePage,
                NLHousingRequest.class
        )
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .sameOutput(true)
                .apply(reqs))
                .onFailure(exc -> log.error("Could not scrap viadaan!", exc))
                .getOrElse(new ArrayList<>());
    }

    private Function<NLHousingRequest, IConnector> getConnector() {
        return req -> Try.of(() -> {
            ObjectMapper mapper = new ObjectMapper();
            HttpClient client = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpResponse response = client.execute(new HttpGet("https://viadaan.nl/aanbod"));
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", "application/json");
            headers.put("Cookie", getAllCookies(response, ""));
            String post = "{\"_token\":\"%s\",\"components\":[{\"snapshot\":\"%s\",\"updates\":{},\"calls\":[{\"path\":\"\",\"method\":\"loadMore\",\"params\":[]}]}]}";
            Document doc = Jsoup.parse(writeToString(response.getEntity().getContent()));
            String token = doc.getElementsByAttribute("data-csrf").attr("data-csrf");
            String initialSnapshot = doc.getElementsByAttribute("wire:snapshot").attr("wire:snapshot").replace("\"", "\\\"").replace("\\\\", "\\\\\\\\");
            return new POSTConnector("https://viadaan.nl/livewire/update",
                    String.format(post, token, initialSnapshot), (context, current) -> {
                try {
                    if (context.getPreviousResponse() == null) return current;
                    return String.format(post, token, mapper.readValue(context.getPreviousResponse(), JsonNode.class).get("components").get(0).get("snapshot").asText()
                            .replace("\"", "\\\"").replace("\\\\", "\\\\\\\\"));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Could not parse json for the next connection", e);
                }
            }, headers);
        }).getOrElseThrow(exc -> new RuntimeException("Could not create a connection to viadaan", exc));
    }
    

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.VIADAAN);
    }
}
