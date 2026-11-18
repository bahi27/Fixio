package fr.bahi27.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.bahi27")
public class Bahi27Application {

  public static void main(String[] args) {
    SpringApplication.run(Bahi27Application.class, args);
  }
}
