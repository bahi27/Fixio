package fr.bahi27.web.common;

import static org.assertj.core.api.Assertions.assertThat;

import fr.bahi27.domain.common.RegleMetierException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
  private final MockHttpServletRequest requete = new MockHttpServletRequest("GET", "/test");

  @Test
  void regleMetierDonneUn400AvecLeMessageMetier() {
    ResponseEntity<ErrorResponse> reponse =
        handler.handleRegleMetier(new RegleMetierException("Regle violee"), requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(400);
    assertThat(reponse.getBody()).isNotNull();
    assertThat(reponse.getBody().message()).isEqualTo("Regle violee");
    assertThat(reponse.getBody().path()).isEqualTo("/test");
  }

  @Test
  void ressourceInconnueDonneUn404() {
    ResponseEntity<ErrorResponse> reponse =
        handler.handleNoResourceFound(
            new NoResourceFoundException(HttpMethod.GET, "/test", "introuvable"), requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(404);
    assertThat(reponse.getBody().message()).isEqualTo("Ressource non trouvee");
  }

  @Test
  void methodeNonSupporteDonneUn405() {
    ResponseEntity<ErrorResponse> reponse =
        handler.handleMethodNotSupported(
            new HttpRequestMethodNotSupportedException("POST"), requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(405);
    assertThat(reponse.getBody().message()).isEqualTo("Methode HTTP non supportee");
  }

  @Test
  void jsonIllisibleDonneUn400() {
    HttpMessageNotReadableException erreur =
        new HttpMessageNotReadableException("illisible", new MockHttpInputMessage(new byte[0]));

    ResponseEntity<ErrorResponse> reponse = handler.handleUnreadableBody(erreur, requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(400);
    assertThat(reponse.getBody().message()).isEqualTo("Requete JSON invalide");
  }

  @Test
  void validationListeLesChampsEnErreur() throws Exception {
    MethodArgumentNotValidException erreur = erreurDeValidation("nom", "Le nom est obligatoire");

    ResponseEntity<ErrorResponse> reponse = handler.handleValidation(erreur, requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(400);
    assertThat(reponse.getBody().validationErrors())
        .containsExactly(new ErrorResponse.ValidationError("nom", "Le nom est obligatoire"));
  }

  @Test
  void validationSansMessageUtiliseUnLibelleParDefaut() throws Exception {
    MethodArgumentNotValidException erreur = erreurDeValidation("nom", null);

    ResponseEntity<ErrorResponse> reponse = handler.handleValidation(erreur, requete);

    assertThat(reponse.getBody().validationErrors())
        .containsExactly(new ErrorResponse.ValidationError("nom", "Valeur invalide"));
  }

  @Test
  void erreurInattendueDonneUn500SansDetailTechnique() {
    ResponseEntity<ErrorResponse> reponse =
        handler.handleGeneralException(new IllegalStateException("secret interne"), requete);

    assertThat(reponse.getStatusCode().value()).isEqualTo(500);
    assertThat(reponse.getBody().message()).doesNotContain("secret interne");
  }

  private MethodArgumentNotValidException erreurDeValidation(String champ, String message)
      throws Exception {
    Method methode = Cible.class.getDeclaredMethod("valider", String.class);
    BeanPropertyBindingResult resultat = new BeanPropertyBindingResult(new Object(), "cible");
    resultat.addError(new FieldError("cible", champ, null, false, null, null, message));
    return new MethodArgumentNotValidException(new MethodParameter(methode, 0), resultat);
  }

  static class Cible {

    void valider(String valeur) {}
  }
}
