# bahi27

Plateforme de mise en relation entre clients et artisans pour des prestations et réparations.

## Prérequis
- Java 21 (Eclipse Temurin recommandé)
- Docker & Docker Compose
- Maven 3.9+

## Lancer le projet en 3 commandes

```bash
# 1. Démarrer la base de données PostgreSQL
docker compose up -d

# 2. Compiler et exécuter les tests
mvn clean verify

# 3. Lancer l'application
mvn spring-boot:run -pl app
```

## Accès aux services locaux
- **API & Swagger UI :** http://localhost:8080/swagger-ui/index.html
- **Documentation OpenAPI (JSON) :** http://localhost:8080/v3/api-docs
- **Santé de l'application (Actuator) :** http://localhost:8080/actuator/health
- **Base PostgreSQL :** `localhost:5432` (base : `bahi27`, user : `bahi27`, password : `bahi27`)

## Architecture
Architecture hexagonale en monolithe modulaire Maven :
- `domain` : regles metier pures et interfaces (ports), sans dependance technique
- `application` : cas d'usage et services orchestrateurs
- `persistence` : adaptateurs JPA, Flyway et implementations de repositories
- `web` : controleurs REST, DTOs et gestion d'erreurs globale
- `app` : point d'entree Spring Boot, configuration et tests d'assemblage
