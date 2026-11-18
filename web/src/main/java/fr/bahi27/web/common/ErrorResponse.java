package fr.bahi27.web.common;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<ValidationError> validationErrors) {

  public record ValidationError(String field, String message) {}

  public static ErrorResponse of(int status, String error, String message, String path) {
    return new ErrorResponse(Instant.now(), status, error, message, path, List.of());
  }

  public static ErrorResponse ofValidation(
      int status, String error, String message, String path, List<ValidationError> errors) {
    return new ErrorResponse(Instant.now(), status, error, message, path, errors);
  }
}
