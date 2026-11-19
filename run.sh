#!/usr/bin/env bash
# Demarre la base, construit le projet et lance l'application.
set -euo pipefail

cd "$(dirname "$0")"

if [ ! -f .env ]; then
  cp .env.example .env
  echo "Fichier .env cree a partir de .env.example"
fi

set -a
# shellcheck disable=SC1091
. ./.env
set +a

echo "Demarrage de la base PostgreSQL..."
docker compose up -d --wait

echo "Construction du projet (sans les tests)..."
mvn -q -DskipTests install

echo "Lancement de l'application..."
echo "  API      : http://localhost:8080"
echo "  Swagger  : http://localhost:8080/swagger-ui.html"
echo "  Sante    : http://localhost:8080/actuator/health"
mvn -pl app spring-boot:run
