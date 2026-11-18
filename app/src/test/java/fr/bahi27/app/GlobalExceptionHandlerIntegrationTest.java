package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import fr.bahi27.domain.common.RegleMetierException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(GlobalExceptionHandlerIntegrationTest.TestErrorController.class)
@Testcontainers
class GlobalExceptionHandlerIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @LocalServerPort private int port;

  @Test
  void regleMetierExceptionRenvoieFormatJson400() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/test/erreur-metier"))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(response.body()).contains("\"status\":400");
    assertThat(response.body()).contains("Regle metier test violee");
  }

  @Test
  void erreurTechniqueRenvoieMessagePropreSansDetailsTechniques() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/test/erreur-technique"))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(500);
    assertThat(response.body()).contains("\"status\":500");
    assertThat(response.body()).contains("Une erreur interne est survenue");
    assertThat(response.body()).doesNotContain("NullPointerException");
    assertThat(response.body()).doesNotContain("Detail technique critique");
  }

  @RestController
  static class TestErrorController {

    @GetMapping("/test/erreur-metier")
    public void leverErreurMetier() {
      throw new RegleMetierException("Regle metier test violee");
    }

    @GetMapping("/test/erreur-technique")
    public void leverErreurTechnique() {
      throw new NullPointerException("Detail technique critique secret");
    }
  }
}
