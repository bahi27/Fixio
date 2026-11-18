package fr.bahi27.domain.common;

/** Exception levee lorsqu'une regle metier est violee. */
public class RegleMetierException extends RuntimeException {

  public RegleMetierException(String message) {
    super(message);
  }
}
