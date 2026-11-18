package fr.bahi27.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class Bahi27ApplicationIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void applicationDemarreEtFlywayMigreSurPostgreSql() {
    Integer resultat = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    assertThat(resultat).isEqualTo(1);
  }
}
