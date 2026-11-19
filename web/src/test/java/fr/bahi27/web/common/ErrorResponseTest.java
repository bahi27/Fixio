package fr.bahi27.web.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void ofCreeUneErreurSansChampDeValidation() {
    ErrorResponse erreur = ErrorResponse.of(400, "Bad Request", "Message", "/test");

    assertThat(erreur.status()).isEqualTo(400);
    assertThat(erreur.error()).isEqualTo("Bad Request");
    assertThat(erreur.message()).isEqualTo("Message");
    assertThat(erreur.path()).isEqualTo("/test");
    assertThat(erreur.timestamp()).isNotNull();
    assertThat(erreur.validationErrors()).isEmpty();
  }

  @Test
  void ofValidationConserveLesChampsEnErreur() {
    List<ErrorResponse.ValidationError> champs =
        List.of(new ErrorResponse.ValidationError("nom", "Le nom est obligatoire"));

    ErrorResponse erreur =
        ErrorResponse.ofValidation(400, "Bad Request", "Invalide", "/test", champs);

    assertThat(erreur.validationErrors()).containsExactlyElementsOf(champs);
  }
}
