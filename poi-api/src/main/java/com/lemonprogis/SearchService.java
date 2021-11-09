package com.lemonprogis;

import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SearchService {
   public void loadStates() throws IOException {
       Resource resource = new ClassPathResource("cb_2018_us_state_500k/cb_2018_us_state_500k.shp");
       resource.getFile();
       Map<String, Object> map = new HashMap<>();
       map.put("url", resource.getFile().toURI().toURL());

       DataStore dataStore = DataStoreFinder.getDataStore(map);
       String typeName = dataStore.getTypeNames()[0];

       FeatureSource<SimpleFeatureType, SimpleFeature> source =
               dataStore.getFeatureSource(typeName);
       Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

       FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
       try (FeatureIterator<SimpleFeature> features = collection.features()) {
           while (features.hasNext()) {
               SimpleFeature feature = features.next();
               System.out.print(feature.getID());
               System.out.print(": ");
               System.out.println(feature.getDefaultGeometryProperty().getValue());
           }
       }
   }
}
