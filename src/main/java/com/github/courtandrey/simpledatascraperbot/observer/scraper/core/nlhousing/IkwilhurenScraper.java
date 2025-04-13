package com.github.courtandrey.simpledatascraperbot.observer.scraper.core.nlhousing;

import com.github.courtandrey.simpledatascraperbot.entity.data.RentalOffering;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLHousingSite;
import com.github.courtandrey.simpledatascraperbot.observer.Pair;
import com.github.courtandrey.simpledatascraperbot.observer.Processee;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.PageScrapingFunction;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.Scraper;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.GETClientConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.IConnector;
import com.github.courtandrey.simpledatascraperbot.observer.scraper.core.parser.nlhousing.IkwilhurenParser;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.AMSTERDAM;
import static com.github.courtandrey.simpledatascraperbot.entity.request.nlhousing.NLCity.ROTTERDAM;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.ConnectionUtil.getAllCookies;
import static com.github.courtandrey.simpledatascraperbot.observer.scraper.core.connector.StreamUtil.writeToString;
import static java.util.Optional.ofNullable;

@Slf4j
public class IkwilhurenScraper implements Scraper<RentalOffering> {
    private final IkwilhurenParser parser = new IkwilhurenParser();

    @Override
    public List<Pair<Request, Processee<RentalOffering>>> scrap(List<Request> reqs) {
        return Try.of(() -> new PageScrapingFunction<>(
                getConnector(),
                parser::parsePage,
                NLHousingRequest.class
        )
                .processReqsInParallel(true)
                .withPostProcessing(cityFromUrl())
                .withFilteringCondition(new RentalOfferingRequestSatisfied())
                .withFilteringCondition((__, off) -> !off.getUrl().contains("wonenbijbouwinvest"))
                .apply(reqs))
                .onFailure(exc -> log.error("Could not scrap requests", exc))
                .getOrElse(new ArrayList<>());
    }

    @Override
    public boolean rightScraperToRequest(Request request) {
        return request instanceof NLHousingRequest nlHousingRequest
                && nlHousingRequest.getSites().contains(NLHousingSite.IKWILHUREN);
    }

    private Consumer<RentalOffering> cityFromUrl() {
        return rentalOffering -> {
            if (rentalOffering.getUrl().contains("wonenbijbouwinvest")) {
                return;
            }
            Try.run(() -> rentalOffering.setCity(rentalOffering.getUrl().split("object/")[1]
                            .split("\\d")[0].replace("-", " ").trim()))
                    .orElseRun((exc) -> log.error("Could not set city from url {}", rentalOffering.getUrl(), exc));
        };
    }

    private Function<NLHousingRequest, IConnector> getConnector() {
        return req -> {
            HttpGet get = new HttpGet("https://ikwilhuren.nu/aanbod/");
            CloseableHttpClient client = HttpClientBuilder.create()
                    .disableCookieManagement().setRedirectStrategy(new LaxRedirectStrategy()).build();
            return Try.of(() -> client.execute(get))
                    .mapTry(response -> {
                        String cookie = getAllCookies(response, "");
                        HttpResponse httpResponse = executePost(client, req, response, cookie, "frmFilter");
                        HttpResponse newResponse = executePost(client, req, httpResponse, getAllCookies(httpResponse, cookie), "frmZoek");
                        return new GETClientConnector("https://ikwilhuren.nu/aanbod/?page=%d&lang=en",
                                Map.of("Cookie", getAllCookies(newResponse, cookie))
                        );
                    })
                    .andFinallyTry(client::close)
                    .getOrElseThrow(exc -> new RuntimeException(exc));
        };
    }

    private HttpResponse executePost(HttpClient client, NLHousingRequest req, HttpResponse response, String cookie, String tokenId) throws IOException {
        HttpPost post = new HttpPost("https://ikwilhuren.nu/aanbod/?page=1&lang=en");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("postrequest", "doeFilter"));
        nameValuePairs.add(new BasicNameValuePair("selAfstand", "5"));
        nameValuePairs.add(new BasicNameValuePair("selAdres", getCity(req.getCity())));
        nameValuePairs.add(new BasicNameValuePair("objSearch", getObjSearch(req.getCity())));
        nameValuePairs.add(new BasicNameValuePair("selPrijsVan", String.valueOf(req.getLowestPrice())));
        nameValuePairs.add(new BasicNameValuePair("selPrijsTot", ofNullable(req.getHighestPrice()).map(String::valueOf).orElse("")));
        String responseT = writeToString(response.getEntity().getContent());
        Document document = Jsoup.parse(responseT);
        nameValuePairs.add(new BasicNameValuePair("_token", Objects.requireNonNull(document.getElementById(tokenId))
                .getElementsByAttributeValue("name", "_token")
                .attr("value")));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, Charset.defaultCharset());
        post.setEntity(entity);
        post.setHeader("Cookie", cookie);
        return client.execute(post);
    }

    private String getCity(String city) {
        if (AMSTERDAM.name().equalsIgnoreCase(city)) {
            return "Amsterdam, Amsterdam, Noord-Holland";
        }

        if (ROTTERDAM.name().equalsIgnoreCase(city)) {
            return "Rotterdam, Rotterdam, Zuid-Holland";
        }

        return city;
    }

    private String getObjSearch(String city) {
        if (AMSTERDAM.name().equalsIgnoreCase(city)) {
            return """
                    {"weergavenaam":"Amsterdam, Amsterdam, Noord-Holland",
                    "lat":52.37344272,
                    "lng":4.9045379}""";
        }

        if (ROTTERDAM.name().equalsIgnoreCase(city)) {
            return "{\"weergavenaam\":\"Rotterdam, Rotterdam, Zuid-Holland\",\"lat\":51.922488059999999,\"lng\":4.4865357100000001}";
        }

        return "";
    }

}
