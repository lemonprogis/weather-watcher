package com.watcher;

import com.watcher.model.POI;
import org.geojson.Feature;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class WatcherController {
    private final WatcherService watcherService;

    public WatcherController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @GetMapping("/alerts")
    public Flux<Feature> getAlertsByLatLng(@RequestParam String lat,
                                           @RequestParam String lng) {
        return watcherService.getAlertsByLatLng(lat, lng);
    }

    @PostMapping("/track")
    public Flux<POI> trackPointsOfInterest(@RequestBody List<POI> pois) {
        return watcherService.geocodePOIs(pois);
    }
}
