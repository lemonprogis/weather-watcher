package com.lemonprogis;


import com.lemonprogis.model.POI;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PoiRepository extends ReactiveMongoRepository<POI, String> {
}
