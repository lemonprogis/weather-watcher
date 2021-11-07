package com.lemonprogis.configuration;

import com.lemonprogis.properties.GeocodeApiProperties;
import com.lemonprogis.properties.WeatherGovApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    @Bean
    public WebClient weatherGovApiWebClient(WeatherGovApiProperties weatherGovApiProperties) {
        return WebClient.builder()
                .baseUrl(weatherGovApiProperties.getBaseUrl())
                .defaultHeader("User-Agent", weatherGovApiProperties.getUserAgent())
                .build();
    }

    @Bean
    public WebClient geocodeApiWebClient(GeocodeApiProperties geocodeApiProperties) {
        return WebClient.builder()
                .baseUrl(geocodeApiProperties.getBaseUrl())
                .build();
    }
}
