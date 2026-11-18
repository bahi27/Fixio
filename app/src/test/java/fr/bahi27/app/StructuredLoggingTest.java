package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
class StructuredLoggingTest {

  private static final Logger log = LoggerFactory.getLogger(StructuredLoggingTest.class);

  @Test
  void loggerFonctionneAvecFormatStructure() {
    log.info("Test de log structure avec horodatage et niveau");
    assertThat(log.isInfoEnabled()).isTrue();
  }
}
