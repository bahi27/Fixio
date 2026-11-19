package fr.bahi27.app.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI bahi27OpenApi(
      @Value("${info.app.title}") String title, @Value("${info.app.version}") String version) {
    return new OpenAPI().info(new Info().title(title).version(version));
  }
}
