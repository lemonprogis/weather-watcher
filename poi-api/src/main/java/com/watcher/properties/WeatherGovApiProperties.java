package com.watcher.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("weathergov.api")
public class WeatherGovApiProperties {
    private String userAgent;
    private String baseUrl;
}
