package fr.monsieurclef.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.monsieurclef")
public class MonsieurClefApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonsieurClefApplication.class, args);
    }
}
