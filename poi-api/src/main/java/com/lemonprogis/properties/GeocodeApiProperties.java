package com.lemonprogis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("geocode.api")
public class GeocodeApiProperties {
    private String baseUrl;
}
