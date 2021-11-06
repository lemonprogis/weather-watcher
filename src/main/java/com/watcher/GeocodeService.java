package com.watcher;

import com.watcher.model.GeocodeResponse;
import com.watcher.model.POI;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GeocodeService {
    private WebClient geocodeApiWebClient;

    public GeocodeService(WebClient geocodeApiWebClient) {
        this.geocodeApiWebClient = geocodeApiWebClient;
    }

    public GeocodeResponse geocode(POI poi) {
        String urlTemplate = "/search.php?street=%s&city=%s&state=%s&postalcode=%s&polygon_geojson=1&format=jsonv2";
        return geocodeApiWebClient.get()
                .uri(String.format(urlTemplate,
                        poi.getAddress().toLowerCase(),
                        poi.getCity().toLowerCase(),
                        poi.getState().toLowerCase(),
                        poi.getZipCode().toLowerCase()))
                .retrieve()
                .bodyToFlux(GeocodeResponse.class)
                .blockFirst();
    }

    public Flux<POI> geocodePOIs(List<POI>pois) {
        List<CompletableFuture<POI>> geocodes = new ArrayList<>();
        pois.forEach(poi -> geocodes.add(CompletableFuture.supplyAsync(() -> {
            GeocodeResponse response = geocode(poi);
            poi.setLat(response.getLat());
            poi.setLng(response.getLon());
            return poi;
        })));

        return Flux.fromIterable(geocodes
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));

    }
}
