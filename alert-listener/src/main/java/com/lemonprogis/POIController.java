package com.lemonprogis;

import com.lemonprogis.model.POI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/poi")
public class POIController {
    private final PoiRepository poiRepository;

    public POIController(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    @GetMapping("/{id}")
    public Mono<POI> getPOI(@PathVariable String id) {
        return poiRepository.findById(id);
    }

    @PostMapping
    public Flux<POI> savePOIs(@RequestBody List<POI> pois) {
        return poiRepository.saveAll(pois);
    }

    @PutMapping("/{id}")
    public Mono<POI> updatePOI(@PathVariable String id, @RequestParam boolean track) {
        return poiRepository.findById(id)
                .log()
                .map(poi -> mapPOI(poi, track))
                .log()
                .flatMap(poiRepository::save);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePOI(@PathVariable String id) {
        return poiRepository.deleteById(id);
    }

    private POI mapPOI(POI poi, boolean track) {
        return POI.builder()
                .id(poi.getId())
                .title(poi.getTitle())
                .lat(poi.getLat())
                .lng(poi.getLng())
                .track(track)
                .build();
    }
}
