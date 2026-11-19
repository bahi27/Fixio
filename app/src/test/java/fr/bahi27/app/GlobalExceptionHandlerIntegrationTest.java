package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import fr.bahi27.domain.common.RegleMetierException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    HttpResponse<String> response = get("/test/erreur-metier");

    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(response.headers().firstValue("Content-Type").orElse(""))
        .contains("application/json");
    assertThat(response.body()).contains("\"status\":400");
    assertThat(response.body()).contains("\"error\":\"Bad Request\"");
    assertThat(response.body()).contains("\"path\":\"/test/erreur-metier\"");
    assertThat(response.body()).contains("Regle metier test violee");
  }

  @Test
  void erreurTechniqueRenvoieMessagePropreSansDetailsTechniques() throws Exception {
    HttpResponse<String> response = get("/test/erreur-technique");

    assertThat(response.statusCode()).isEqualTo(500);
    assertThat(response.body()).contains("\"status\":500");
    assertThat(response.body()).contains("Une erreur interne est survenue");
    assertThat(response.body()).doesNotContain("NullPointerException");
    assertThat(response.body()).doesNotContain("Detail technique critique");
  }

  @Test
  void ressourceInconnueRenvoieFormatJson404() throws Exception {
    HttpResponse<String> response = get("/test/chemin-inexistant");

    assertThat(response.statusCode()).isEqualTo(404);
    assertThat(response.body()).contains("\"status\":404");
    assertThat(response.body()).contains("Ressource non trouvee");
  }

  @Test
  void methodeNonSupporteeRenvoieFormatJson405() throws Exception {
    HttpResponse<String> response = post("/test/erreur-metier", "{}");

    assertThat(response.statusCode()).isEqualTo(405);
    assertThat(response.body()).contains("\"status\":405");
    assertThat(response.body()).contains("Methode HTTP non supportee");
    assertThat(response.body()).doesNotContain("HttpRequestMethodNotSupportedException");
  }

  @Test
  void jsonInvalideRenvoieFormatJson400() throws Exception {
    HttpResponse<String> response = post("/test/validation", "{");

    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(response.body()).contains("\"status\":400");
    assertThat(response.body()).contains("Requete JSON invalide");
    assertThat(response.body()).doesNotContain("JsonParseException");
  }

  @Test
  void validationRenvoieLesChampsEnErreur() throws Exception {
    HttpResponse<String> response = post("/test/validation", "{\"nom\":\"\"}");

    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(response.body()).contains("\"status\":400");
    assertThat(response.body()).contains("\"field\":\"nom\"");
    assertThat(response.body()).contains("Le nom est obligatoire");
  }

  private HttpResponse<String> get(String chemin) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + chemin)).GET().build();
    return envoyer(request);
  }

  private HttpResponse<String> post(String chemin, String corps) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + chemin))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(corps))
            .build();
    return envoyer(request);
  }

  private HttpResponse<String> envoyer(HttpRequest request) throws Exception {
    return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
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

    @PostMapping("/test/validation")
    public void valider(@Valid @RequestBody CorpsValide corps) {}

    record CorpsValide(@NotBlank(message = "Le nom est obligatoire") String nom) {}
  }
}
