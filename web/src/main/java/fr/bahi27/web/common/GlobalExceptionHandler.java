package fr.bahi27.web.common;

import fr.bahi27.domain.common.RegleMetierException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(RegleMetierException.class)
  public ResponseEntity<ErrorResponse> handleRegleMetier(
      RegleMetierException ex, HttpServletRequest request) {
    log.warn("Regle metier violee sur {}: {}", request.getRequestURI(), ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(
      org.springframework.web.servlet.resource.NoResourceFoundException ex,
      HttpServletRequest request) {
    ErrorResponse error =
        ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "Ressource non trouvee",
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ErrorResponse.ValidationError> validationErrors =
        ex.getBindingResult().getFieldErrors().stream().map(this::toValidationError).toList();

    ErrorResponse error =
        ErrorResponse.ofValidation(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Erreur de validation des donnees fournies",
            request.getRequestURI(),
            validationErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(
      Exception ex, HttpServletRequest request) {
    log.error("Erreur inattendue sur {}", request.getRequestURI(), ex);
    ErrorResponse error =
        ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Une erreur interne est survenue. Veuillez reessayer plus tard.",
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private ErrorResponse.ValidationError toValidationError(FieldError fieldError) {
    String message =
        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valeur invalide";
    return new ErrorResponse.ValidationError(fieldError.getField(), message);
  }
}
