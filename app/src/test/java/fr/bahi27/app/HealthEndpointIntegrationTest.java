package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class HealthEndpointIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private HealthEndpoint healthEndpoint;

  @LocalServerPort private int port;

  @Test
  void actuatorHealthEndpointRenvoieStatusUp() {
    HealthDescriptor health = healthEndpoint.health();
    assertThat(health.getStatus().getCode()).isEqualTo("UP");
  }

  @Test
  void healthEstAccessibleEnHttpEtEnvNonExpose() throws Exception {
    HttpClient client = HttpClient.newHttpClient();

    HttpResponse<String> sante =
        client.send(requete("/actuator/health"), HttpResponse.BodyHandlers.ofString());
    HttpResponse<String> environnement =
        client.send(requete("/actuator/env"), HttpResponse.BodyHandlers.ofString());

    assertThat(sante.statusCode()).isEqualTo(200);
    assertThat(sante.body()).contains("\"status\":\"UP\"");
    assertThat(environnement.statusCode()).isEqualTo(404);
  }

  private HttpRequest requete(String chemin) {
    return HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + port + chemin))
        .GET()
        .build();
  }
}
