# bahi27

Plateforme de mise en relation entre clients et artisans : un client publie une
demande, les artisans proposent des offres, le client en accepte une et une
mission est créée.

Le projet est pour l'instant à l'état de fondations : le socle technique
démarre, se teste et se déploie, mais aucun module métier n'est encore
implémenté.

## Prérequis

- **JDK 21 Eclipse Temurin** (`java -version` doit afficher Temurin 21 ; ne pas
  utiliser un JDK Oracle)
- **Maven 3.9 ou plus**
- **Docker** avec le plugin **Compose** (utilisé pour PostgreSQL et pour les
  tests d'intégration Testcontainers)

## Démarrage rapide

```bash
git clone <url-du-depot> bahi27
cd bahi27
./run.sh
```

Le script copie `.env.example` en `.env` si nécessaire, démarre PostgreSQL,
attend qu'il soit prêt, construit le projet **sans les tests**, puis lance le
module `app`. Sous Windows, utiliser `run.bat` (terminal `cmd`).

Une fois démarré :

| Adresse | Contenu |
| --- | --- |
| http://localhost:8080/actuator/health | état de l'application (`{"status":"UP"}`) |
| http://localhost:8080/swagger-ui.html | interface Swagger UI |
| http://localhost:8080/v3/api-docs | description OpenAPI (titre `bahi27 API`, version du projet) |

Pour arrêter l'application : `Ctrl+C`. Pour arrêter la base :
`docker compose down` (les données restent dans le volume `pgdata`).

## Commandes utiles

```bash
mvn clean verify                        # build complet + tous les tests + formatage
mvn -pl app spring-boot:run             # lancer l'application (base déjà démarrée)
mvn -DskipTests install                 # construire sans les tests
docker compose up -d --wait             # démarrer PostgreSQL et attendre qu'il soit prêt
docker compose down                     # arrêter PostgreSQL
```

`mvn clean verify` exige Docker : les tests d'intégration démarrent un
PostgreSQL jetable avec Testcontainers.

## Configuration

Toute la configuration passe par des variables d'environnement, avec des
valeurs par défaut dans `app/src/main/resources/application.yml` :

| Variable | Défaut | Rôle |
| --- | --- | --- |
| `DATABASE_HOST` | `localhost` | hôte PostgreSQL |
| `DATABASE_PORT` | `5432` | port PostgreSQL |
| `DATABASE_NAME` | `bahi27` | nom de la base |
| `DATABASE_USERNAME` | `bahi27` | utilisateur |
| `DATABASE_PASSWORD` | `bahi27` | mot de passe |

`docker-compose.yml` lit le fichier `.env` (créé à partir de `.env.example`).
Aucun secret n'est stocké dans le dépôt : `.env` est ignoré par Git.

## Structure du projet

Projet Maven multi-modules, architecture hexagonale, packages
`fr.bahi27.<couche>.<domaine>` :

| Module | Contenu |
| --- | --- |
| `domain` | règles métier, **aucune dépendance** (ni Spring, ni JPA) |
| `application` | orchestration des cas d'usage |
| `persistence` | base de données, migrations Flyway, adaptateurs |
| `web` | API HTTP, gestion d'erreurs commune |
| `app` | point d'entrée Spring Boot, configuration, tests d'intégration |

Les frontières sont vérifiées automatiquement par un test ArchUnit
(`app/src/test/java/fr/bahi27/app/ArchitectureTest.java`) : le build échoue si
`domain` importe Spring/JPA, si `web` touche `persistence`, si `application`
touche les adaptateurs, ou si un domaine dépend d'un autre domaine.

La couverture est mesurée par JaCoCo ; `mvn verify` échoue si un module de
production descend sous **70 % des lignes** ou **60 % des branches**. Le
rapport global est écrit dans `app/target/site/jacoco-aggregate/index.html`.

## Git

Chaque tâche validée donne lieu à un commit unique en français, préfixé d'un
type : `feat:`, `fix:`, `build:`, `chore:`, `test:` ou `docs:`. Aucun commit
ne doit laisser le build rouge.

## Erreurs et logs

Toutes les erreurs HTTP renvoient le même format JSON (`status`, `error`,
`message`, `path`, et `validationErrors` le cas échéant). Aucun détail
technique n'est exposé au client : la trace complète part dans les logs.
Les logs sont horodatés en ISO-8601 et n'exposent ni mot de passe ni secret ;
seuls `health` et `info` sont exposés sous `/actuator`.

## Documentation

- `AGENTS.md` : règles de code et de collaboration (fait foi)
- `PLAN.md` : feuille de route par phases et journal des décisions
- `partie-1.puml` : modèle de données de référence
