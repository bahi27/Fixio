# PLAN.md : stratégie et feuille de route

Ce fichier dit **quoi construire et dans quel ordre**.
Les règles de code sont dans `AGENTS.md`. Le modèle de données est dans
`partie-1.puml`. En cas de doute, ces deux fichiers font foi.

## Objectif

Plateforme de mise en relation entre clients et artisans.
Un client publie une demande, des artisans proposent des offres,
le client en accepte une, une mission est créée.

Périmètre du MVP : modules `user`, `catalog`, `request`, `mission`.
Tout le reste est hors périmètre tant que le MVP n'est pas validé.

## Règles de fonctionnement

1. Une phase à la fois. Ne commence jamais la phase suivante sans que
   je l'aie validée.
2. Une tâche à la fois, dans l'ordre de la liste.
3. Quand une tâche est terminée, coche-la ici (`[x]`) dans le même commit.
4. Si une tâche est floue ou demande un choix technique, pose la question
   avant de coder.
5. Ne modifie pas le modèle de données sans me le proposer d'abord.

## Cycle à appliquer pour chaque tâche

1. **Comprendre** : relis la tâche, le diagramme et le code existant concerné.
2. **Annoncer** : explique en 3 à 5 lignes ce que tu vas faire.
3. **Écrire les tests** : décris le comportement attendu en tests d'abord.
4. **Coder** : le minimum pour faire passer les tests.
5. **Vérifier** : lance `mvn verify`. Tout doit être vert.
6. **Résumer** : en langage simple, ce qui a changé, les fichiers touchés,
   ce que je dois vérifier.
7. **Commit** : un commit par tâche, message clair en français.

## Définition de "terminé"

Une tâche est terminée seulement si :

- [ ] `mvn verify` passe sans erreur ni avertissement nouveau
- [ ] le code est formaté automatiquement
- [ ] les services ont des tests unitaires, les endpoints un test d'intégration
- [ ] les entrées utilisateur sont validées
- [ ] aucune dépendance n'a été ajoutée sans mon accord
- [ ] le résumé est écrit et compréhensible sans lire le code

## Phase 0 : fondations

But : un projet vide qui démarre, se teste et se déploie.

- [x] Projet parent Maven + modules vides : domain, application, persistence,
      web, app. Les dépendances entre modules sont celles d'AGENTS.md.
      `mvn clean verify` passe. `domain` n'a aucune dépendance.
- [x] Projet Spring Boot (Java 21, Maven), structure par modules métier
- [x] `docker-compose.yml` avec PostgreSQL pour le développement
- [x] Flyway configuré, première migration vide
- [x] Configuration par variables d'environnement (aucun secret dans le code)
- [x] Formatage automatique et `mvn verify` qui l'applique
- [x] Un test d'intégration avec Testcontainers qui démarre l'application
- [x] Endpoint de santé (`/actuator/health`)
- [x] OpenAPI (Swagger UI) accessible en local
- [x] Gestion d'erreurs globale : format JSON unique, jamais d'erreur technique visible
- [x] Logs structurés
- [x] Intégration continue (GitHub Actions) qui lance `mvn verify`
- [ ] `README.md` : comment lancer le projet en 3 commandes
- [ ] Créer un module exemple minimal avec les 4 dossiers (domain, application,
      api, infrastructure) pour valider la structure avant les vrais modules
- [x] Test d'architecture automatique (ArchUnit `archunit-junit5` 1.5.1, Apache 2.0) qui
      fait échouer `mvn verify` si :
      - `domain` importe Spring, JPA ou Jackson
      - `web` accède à `persistence`
      - `application` accède à `persistence` ou `web`
      - un domaine (`fr.bahi27.domain.<domaine>`) dépend d'un autre domaine
      - un controller utilise directement un Repository
- [ ] Seuil JaCoCo (rapport agrégé en place, seuil à décider par le porteur du projet ;
      proposition : 80 % des lignes et 70 % des branches)
**Validation attendue :** je clone le projet, je lance 3 commandes, ça marche.

## Phase 1 : module `user`

- [ ] Migration : tables `utilisateur` et `adresse`
- [ ] Inscription (email unique, mot de passe haché, rôle CLIENT ou ARTISAN)
- [ ] Connexion avec jeton (proposer l'approche avant de coder, application stateless)
- [ ] Un utilisateur ne peut jamais s'attribuer le rôle ADMIN lui-même
- [ ] Consulter et modifier son profil
- [ ] Gérer ses adresses (une seule adresse par défaut)
- [ ] Désactiver son compte
- [ ] Contrôle d'accès : chaque endpoint indique quel rôle y a droit
- [ ] Tests : cas normaux et cas d'erreur (email déjà pris, mot de passe faible, accès refusé)

## Phase 2 : module `catalog`

- [ ] Migration : catégories, spécialités, profil artisan, spécialités de l'artisan
- [ ] Lister catégories et spécialités (lecture publique, paginée)
- [ ] Créer et modifier catégories et spécialités (ADMIN uniquement)
- [ ] Création du profil artisan (SIRET unique, statut EN_ATTENTE)
- [ ] L'artisan gère ses spécialités, tarif indicatif et années d'expérience
- [ ] Vérification d'un artisan par un ADMIN (VERIFIE ou REFUSE)
- [ ] Recherche d'artisans par spécialité et ville (paginée, avec index)
- [ ] Données de départ : quelques catégories et spécialités via migration

## Phase 3 : module `request`

- [ ] Migration : demandes, pièces jointes, offres (contraintes d'unicité incluses)
- [ ] Client : créer une demande en BROUILLON, la modifier, la publier, l'annuler
- [ ] Interface `FileStorage` avec une implémentation disque local (premier cas
      réel où une interface est justifiée), puis envoi de pièces jointes
      (types et taille limités)
- [ ] Artisan : lister les demandes publiées correspondant à ses spécialités (paginé)
- [ ] Artisan vérifié : proposer, modifier, retirer une offre
- [ ] Une seule offre par artisan et par demande
- [ ] Client : consulter les offres reçues
- [ ] Client : accepter une offre (les autres passent à REFUSEE, la demande
      passe à ATTRIBUEE, le tout dans une seule transaction)
- [ ] Expiration des offres (tâche planifiée)
- [ ] Tests des règles : offre expirée, double acceptation, accès à la demande d'un autre client

## Phase 4 : module `mission`

- [ ] Migration : table mission
- [ ] Création automatique de la mission à l'acceptation d'une offre
- [ ] Planifier, démarrer, terminer, annuler avec les transitions autorisées
      (voir le diagramme), toute autre transition est refusée
- [ ] Qui peut faire quoi : l'artisan démarre et termine, le client et l'artisan
      peuvent annuler selon l'état
- [ ] La demande passe à TERMINEE quand la mission est terminée
- [ ] Consulter ses missions (client et artisan, paginé)
- [ ] Tests des transitions interdites

## Phase 5 : consolidation du MVP

- [ ] Revue de sécurité : accès, validation, secrets, messages d'erreur
- [ ] Revue des index et des requêtes lentes avec un jeu de données volumineux
- [ ] Test de bout en bout : inscription -> demande -> offre -> acceptation -> mission terminée
- [ ] Nettoyage : code mort, doublons, dépendances inutiles
- [ ] Documentation à jour : README, OpenAPI, ce plan
- [ ] Déploiement de test (Docker) sur un petit serveur

**Validation attendue :** le parcours complet fonctionne et je peux le démontrer.

## Après le MVP (ne pas commencer sans ordre de ma part)

Dans cet ordre probable : `review`, `messaging`, `notification`, `payment`
(avec interface `PaymentGateway`), `dispute`, conformité RGPD (export et
suppression des données).

## Journal des décisions

Ajoute ici chaque décision technique importante (date, choix, raison).

| Date | Décision | Raison |
|---|---|---|
| 2026-11-18 | Choix de PostgreSQL 17 dans docker-compose | Version stable et supportée avec volume persistant |
| 2026-11-18 | Renommage du projet et group/packages en fr.bahi27 | Demande utilisateur pour supprimer toute mention de l'ancien nom |
| 2026-11-18 | Spotless avec Google Java Format 1.28.0 | Formatage automatique applique sur mvn verify, compatible Java 25 |
| 2026-11-18 | Testcontainers PostgreSQL 17 avec @ServiceConnection | Demarrage conteneur PostgreSQL ephemere pour les tests d'integration sans configuration manuelle de datasource |
| 2026-11-18 | Actuator HealthEndpoint et exposition web /actuator/health | Exposition des sondes de sante pour le monitoring et la supervision |
| 2026-11-18 | Springdoc OpenAPI 3.2.0 dans module app | Documentation Swagger UI et OpenAPI 3 compatible Spring Boot 4.1 |
| 2026-11-18 | GlobalExceptionHandler avec ErrorResponse unifiee | Format JSON unique d'erreur (RFC 7807 inspire), masquage des erreurs techniques |
| 2026-11-18 | Format de log standardise ISO-8601 et niveaux par package | Logs structures clairs avec horodatage precis et niveau debug pour bahi27 |
| 2026-11-18 | GitHub Actions CI avec Eclipse Temurin 21 | Verification automatique du build et des tests sur push et PR |
| 2026-11-19 | Titre et version OpenAPI lus depuis application.yml (version issue du pom.xml) | Documentation exacte sans dupliquer le numero de version |
| 2026-11-19 | Reponses 405 et 400 (JSON illisible) ajoutees au handler global | Sans elles, une methode HTTP erronee ou un JSON casse renvoyait 500 avec un message trompeur |
| 2026-11-19 | Format de log verifie par test (ISO-8601) et actuator limite a health,info | Prouve le format sans dependance supplementaire et evite l'exposition de configuration sensible |
| 2026-11-19 | JaCoCo 0.8.16 avec rapport agrege dans le module app | Mesure de couverture globale ; aucun seuil impose tant que le porteur du projet ne l'a pas choisi |
| 2026-11-19 | ArchUnit 1.5.1 (scope test, module app) pour verrouiller les frontieres de couches | Une violation de couche casse le build au lieu d'etre decouverte a la relecture |
| 2026-11-19 | CI declenchee sur tout push et toute pull request (plus seulement main) | La CI couvre aussi les branches de travail comme phase-0 |
| 2026-11-19 | `AGENT.md` renomme en `AGENTS.md` ; PLAN.md pointe vers `partie-1.puml` (le fichier `docs/mvp-domaine.puml` n'existe pas) | Reference unique et chemin reellement existant |