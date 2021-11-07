package com.lemonprogis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.lemonprogis.model.POI;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 0)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationIT {

	private static final String mongoVersion = "mongo:4.4.4";
	private static final Integer mongoPort = 27017;

	private static final String redisVersion = "redis:5.0.3-alpine";
	private static final Integer redisPort = 6379;

	@LocalServerPort
	private int port;

	@Autowired
	public WireMockServer wireMockServer;

	private static WebTestClient webTestClient;

	@Container
	private static final MongoDBContainer mongoDBContainer =
			new MongoDBContainer(DockerImageName.parse(mongoVersion))
					.withExposedPorts(mongoPort);

	@Container
	private static final GenericContainer redisContainer =
			new GenericContainer(DockerImageName.parse(redisVersion))
					.withExposedPorts(redisPort);

	@Container
	private static final KeycloakContainer keycloakContainer =
			new KeycloakContainer().withRealmImportFile("realm-export.json");

	@SuppressWarnings("unused")
	@DynamicPropertySource
	static void propertySetup(DynamicPropertyRegistry registry) {
		registry.add("mongo.connection.host", mongoDBContainer::getContainerIpAddress);
		registry.add("mongo.connection.port", () -> mongoDBContainer.getFirstMappedPort().toString());
		registry.add("spring.redis.host", redisContainer::getContainerIpAddress);
		registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
		registry.add("KEYCLOAK_URL", keycloakContainer::getAuthServerUrl);
	}

	@BeforeAll
	static void setUpAll() {
		mongoDBContainer.start();
		redisContainer.start();
		keycloakContainer.start();
	}

	@BeforeEach
	void beforeEach() {
		String baseUrl = String.format("http://localhost:%d", port);
		webTestClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
	}

	@AfterAll
	static void tearDownAll() {
		if (!mongoDBContainer.isShouldBeReused()) {
			mongoDBContainer.stop();
		}
		if (!redisContainer.isShouldBeReused()) {
			redisContainer.stop();
		}

		if(!keycloakContainer.isShouldBeReused()) {
			keycloakContainer.stop();
		}
	}

	@Test
	public void poiEndpointTests() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String request = new String(readAllBytes(get("src/test/resources/__files/poi_request.json"))).trim();
		List<POI> pois = mapper.readValue(request, new TypeReference<List<POI>>() {});

		// ACT and ASSERT
		webTestClient.post()
				.uri("/poi")
				.body(BodyInserters.fromValue(pois))
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(POI.class)
				.consumeWith(r -> {
					List<POI> actual = Optional.ofNullable(r.getResponseBody()).orElseThrow();
					assertThat(actual).isNotNull();

					// only have one
					POI poi = actual.get(0);

					// get test
					getTest(poi.getId());

					// update test
					updateTest(poi.getId(), poi.getTrack() ? false : true);

					// delete test
					deleteTest(poi.getId());
				});
	}

	private void getTest(String id) {
		webTestClient.get()
				.uri("/poi/" + id)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(POI.class)
				.consumeWith(r -> {
					POI actual = Optional.ofNullable(r.getResponseBody()).orElseThrow();
					assertThat(actual).isNotNull();
				});
	}

	private void updateTest(String id, boolean value) {
		webTestClient.put()
				.uri("/poi/" + id + "?track=" + value)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(POI.class)
				.consumeWith(r -> {
					POI actual = Optional.ofNullable(r.getResponseBody()).orElseThrow();
					assertThat(actual.getTrack()).isEqualTo(value);
				});
	}

	private void deleteTest(String id) {
		webTestClient.delete()
				.uri("/poi/" + id)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(Void.class)
				.consumeWith(r -> {
					assertThat(r.getResponseBody()).isNull();
				});
	}
}
