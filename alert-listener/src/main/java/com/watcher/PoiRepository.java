package com.watcher;


import com.watcher.model.POI;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PoiRepository extends ReactiveMongoRepository<POI, String> {
}
