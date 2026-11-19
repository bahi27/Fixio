package fr.bahi27.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RegleMetierExceptionTest {

  @Test
  void conserveLeMessageDeLaRegleViolee() {
    RegleMetierException exception = new RegleMetierException("Le prix doit etre positif");

    assertEquals("Le prix doit etre positif", exception.getMessage());
  }
}
