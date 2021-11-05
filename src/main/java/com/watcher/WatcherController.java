package com.watcher;

import org.geojson.Feature;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
