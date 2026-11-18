package fr.bahi27.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude="
          + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
          + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
          + "org.springframework.boot.jpa.autoconfigure.JpaAutoConfiguration,"
          + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,"
          + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
    })
class Bahi27ApplicationTest {

  @Test
  void contextLoads() {
    // Verifie que le contexte Spring Boot demarre correctement.
    // Le test d'integration avec base de donnees reelle (Testcontainers)
    // sera ajoute dans une tache dediee de la Phase 0.
  }
}
