package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${logging.pattern.console}")
  private String patternConsole;

  @Test
  void formatDeLogContientHorodatageIsoNiveauLoggerEtMessage() {
    String ligne = formater("fr.bahi27.app.StructuredLoggingTest");

    assertThat(ligne)
        .matches(
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}:\\d{2} \\[main\\] INFO"
                + "\\s+fr\\.bahi27\\.app\\.StructuredLoggingTest - Message de controle\\s*$");
  }

  @Test
  void formatDeLogNeContientNiMotDePasseNiUtilisateur() {
    String ligne = formater("fr.bahi27.app.StructuredLoggingTest");

    assertThat(ligne).doesNotContain("password").doesNotContain("username");
  }

  private String formater(String nomLogger) {
    PatternLayout layout = new PatternLayout();
    layout.setContext(new LoggerContext());
    layout.setPattern(patternConsole);
    layout.start();
    return layout.doLayout(evenementInfo(nomLogger));
  }

  private LoggingEvent evenementInfo(String nomLogger) {
    LoggingEvent evenement = new LoggingEvent();
    evenement.setLoggerName(nomLogger);
    evenement.setLevel(Level.INFO);
    evenement.setThreadName("main");
    evenement.setMessage("Message de controle");
    return evenement;
  }
}
