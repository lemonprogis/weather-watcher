package com.watcher.configuration;

import com.watcher.properties.WeatherGovApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WeatherGovApiConfiguration {
    @Bean
    public WebClient weatherGovApiWebClient(WeatherGovApiProperties weatherGovApiProperties) {
        return WebClient.builder()
                .baseUrl(weatherGovApiProperties.getBaseUrl())
                .defaultHeader("User-Agent", weatherGovApiProperties.getUserAgent())
                .build();
    }
}
