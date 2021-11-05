package com.watcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 0)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationIT {

	@LocalServerPort
	private int port;

	@Autowired
	public WireMockServer wireMockServer;

	private static WebTestClient webTestClient;

	@BeforeEach
	void beforeEach() {
		String baseUrl = String.format("http://localhost:%d", port);
		webTestClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
	}

	@Test
	public void getAlertsFromLatLng_happyPath() {
		// ARRANGE
		String lat = "35.020262";
		String lng = "-92.475396";

		wireMockServer.stubFor(
				WireMock.get(urlEqualTo(String.format("/alerts/active?point=%s,%s",lat,lng)))
						.willReturn(WireMock.aResponse()
								.withHeader("Content-Type", "application/json")
								.withBodyFile("alerts_point_weathergov_api_response.json")));

		// ACT and ASSERT
		// create first then retrieve it, then delete it
		webTestClient.get()
				.uri(String.format("/alerts?lat=%s&lng=%s",lat,lng))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(Feature.class)
				.consumeWith( r -> {
					List<Feature> actual = Optional.ofNullable(r.getResponseBody()).orElseThrow();
					assertThat(actual).isNotNull();
					log.info("{}", actual);
				});
	}
}
