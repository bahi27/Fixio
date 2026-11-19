@echo off
rem Demarre la base, construit le projet et lance l'application.
setlocal

cd /d "%~dp0"

if not exist .env (
    copy .env.example .env >nul
    echo Fichier .env cree a partir de .env.example
)

findstr /v /b "#" .env > "%TEMP%\bahi27-env.txt"
for /f "usebackq tokens=1,* delims==" %%A in ("%TEMP%\bahi27-env.txt") do set "%%A=%%B"
del "%TEMP%\bahi27-env.txt"

echo Demarrage de la base PostgreSQL...
docker compose up -d --wait
if errorlevel 1 goto erreur

echo Construction du projet (sans les tests)...
call mvn -q -DskipTests install
if errorlevel 1 goto erreur

echo Lancement de l'application...
echo   API      : http://localhost:8080
echo   Swagger  : http://localhost:8080/swagger-ui.html
echo   Sante    : http://localhost:8080/actuator/health
call mvn -pl app spring-boot:run
goto fin

:erreur
echo Echec du demarrage.
exit /b 1

:fin
endlocal
