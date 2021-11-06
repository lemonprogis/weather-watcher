package com.watcher;

import com.watcher.model.GeocodeResponse;
import com.watcher.model.POI;
import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class WatcherService {
    private final WebClient weatherGovApiWebClient;
    private final GeocodeService geocodeService;

    public WatcherService(WebClient weatherGovApiWebClient, GeocodeService geocodeService) {
        this.weatherGovApiWebClient = weatherGovApiWebClient;
        this.geocodeService = geocodeService;
    }

    public Flux<Feature> getAlertsByLatLng(String lat, String lng) {
        return weatherGovApiWebClient.get()
                .uri(String.format("/alerts/active?point=%s,%s", lat,lng))
                .retrieve()
                .bodyToMono(FeatureCollection.class)
                .map(FeatureCollection::getFeatures)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<POI> geocodePOIs(List<POI> pois) {
        return geocodeService.geocodePOIs(pois);
    }

}
