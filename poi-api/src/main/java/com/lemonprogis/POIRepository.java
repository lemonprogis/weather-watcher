package com.lemonprogis;


import com.lemonprogis.model.POI;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface POIRepository extends ReactiveMongoRepository<POI, String> {
    Flux<POI> findPOISByTrackEquals(boolean track);
}
