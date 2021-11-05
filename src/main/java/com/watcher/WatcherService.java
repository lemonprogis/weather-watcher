package com.watcher;

import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class WatcherService {
    private final WebClient weatherGovApiWebClient;

    public WatcherService(WebClient weatherGovApiWebClient) {
        this.weatherGovApiWebClient = weatherGovApiWebClient;
    }

    public Flux<Feature> getAlertsByLatLng(String lat, String lng) {
        return weatherGovApiWebClient.get()
                .uri(String.format("/alerts/active?point=%s,%s", lat,lng))
                .retrieve()
                .bodyToMono(FeatureCollection.class)
                .map(FeatureCollection::getFeatures)
                .flatMapMany(Flux::fromIterable);

    }

}
