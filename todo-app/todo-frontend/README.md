# Todo App

Eine Full-Stack Todo-Anwendung mit Spring Boot Backend (Java 17) und React Frontend.
Die App ermöglicht das Erstellen, Bearbeiten, Als-erledigt-Markieren und Löschen von Aufgaben.

## Architektur

```
Todo-App/
├── todo-app/                    # Backend (Spring Boot)
│   ├── src/main/java/org/example/todo/
│   │   ├── TodoAppApplication.java       # Einstiegspunkt
│   │   ├── controller/TodoController.java  # REST-Controller (OpenAPI-Generiert)
│   │   ├── service/TodoService.java      # Geschäftslogik
│   │   ├── repository/TodoRepository.java # JPA-Repository
│   │   ├── model/Todo.java               # JPA-Entity
│   │   ├── mapper/TodoMapper.java        # DTO ↔ Entity Mapping
│   │   ├── exception/                    # Eigene Exceptions & Handler
│   │   └── config/Webconfig.java         # CORS-Konfiguration
│   ├── src/main/resources/
│   │   ├── application.properties        # Haupt-Config
│   │   └── static/openapi.yaml           # OpenAPI 3.0 Spezifikation
│   └── src/test/                         # Unit- & Integration-Tests
│
├── todo-app/todo-frontend/      # Frontend (React 19)
│   ├── src/
│   │   ├── App.js               # Root-Komponente
│   │   ├── TodoList.js          # Hauptkomponente mit CRUD-Logik
│   │   ├── api.js               # Zentrale API-Konfiguration (Axios)
│   │   ├── TodoList.test.jsx    # Jest + Testing Library Tests
│   │   └── App.test.js          # Smoke Test
│   └── e2e/                     # Playwright E2E-Tests
│
└── jenkinsfile                  # CI/CD Pipeline
```

## Tech Stack

**Backend:**
- Spring Boot 3.2.5 (Java 17)
- Spring Data JPA + Hibernate
- H2 In-Memory Database
- OpenAPI Generator (Code-First aus YAML)
- Lombok
- Bean Validation (jakarta.validation)
- JUnit 5 + Mockito

**Frontend:**
- React 19
- Create React App
- Axios (HTTP-Client)
- Jest + Testing Library (Unit)
- Playwright (E2E)

**CI/CD:**
- Jenkins Pipeline

## Voraussetzungen

- **Java** 17+ (`java -version`)
- **Maven** 3.8+ (`mvn -version`)
- **Node.js** 18+ (`node -v`)
- **npm** 9+ (`npm -v`)

## Setup & Start

### Backend starten

```bash
cd todo-app
mvn spring-boot:run
```

Das Backend läuft auf `http://localhost:8085`.

### Frontend starten

In einem zweiten Terminal:

```bash
cd todo-app/todo-frontend
npm install
npm start
```

Das Frontend läuft auf `http://localhost:3000` und öffnet sich automatisch im Browser.

## API Dokumentation

Sobald das Backend läuft, ist die Swagger UI erreichbar unter:
- **Swagger UI:** `http://localhost:8085/swagger-ui/index.html`
- **OpenAPI YAML:** `http://localhost:8085/openapi.yaml`
- **H2 Console:** `http://localhost:8085/h2-console` (JDBC URL: `jdbc:h2:mem:todo-db`, User: `sa`)

### Endpoints

| Method | Path                  | Beschreibung                          | Statuscodes    |
|--------|-----------------------|---------------------------------------|----------------|
| GET    | `/todos`              | Alle Todos abrufen                    | 200            |
| POST   | `/todos`              | Neues Todo anlegen                    | 201, 400       |
| GET    | `/todos/{id}`         | Einzelnes Todo abrufen                | 200, 404       |
| DELETE | `/todos/{id}`         | Todo löschen                          | 204, 404       |
| PUT    | `/todos/{id}/done`    | Status togglen (erledigt ↔ offen)     | 200, 404       |
| PUT    | `/todos/{id}/title`   | Titel ändern (Body: `text/plain`)     | 200, 404, 400  |

### Beispiel-Request

```bash
# Neues Todo anlegen
curl -X POST http://localhost:8085/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Einkaufen gehen","done":false}'

# Titel ändern
curl -X PUT http://localhost:8085/todos/1/title \
  -H "Content-Type: text/plain" \
  -d "Wochenendeinkauf"
```

## Tests

### Backend-Tests

```bash
cd todo-app
mvn test
```

Umfasst:
- **Unit-Tests** (`TodoServiceTest`) — Service-Logik mit Mockito
- **Integration-Tests** (`TodoServiceIntegrationTest`, `TodoApiIntegrationTest`) — Vollständiger Spring-Boot-Context mit H2

### Frontend-Tests

```bash
cd todo-app/todo-frontend

# Unit-Tests (Jest)
npm test

# E2E-Tests (Playwright) — Backend & Frontend müssen laufen!
# 1. Backend:    cd .. && mvn spring-boot:run
# 2. Frontend:   cd todo-frontend && npm start
# 3. In drittem Terminal:
npx playwright install chromium   # einmalig nötig
npm run test:e2e

# E2E sichtbar debuggen (Browser öffnet sich):
$env:HEADED="true"; npm run test:e2e   # PowerShell
# oder
HEADED=true npm run test:e2e           # Bash/Linux
```

## Konfiguration

### Profile

- **default** (`application.properties`): Entwicklung, H2-In-Memory-DB, DDL `create-drop`
- **test** (`application-test.properties`): Isolierte Test-DB, reduziertes Logging, H2-Console deaktiviert

### Wichtige Properties

| Property                              | Default                        | Beschreibung              |
|---------------------------------------|--------------------------------|---------------------------|
| `server.port`                         | 8085                           | Backend-Port              |
| `spring.datasource.url`               | `jdbc:h2:mem:todo-db`          | Datenbank-URL             |
| `spring.jpa.hibernate.ddl-auto`       | `create-drop`                  | Schema-Generierung        |
| `spring.h2.console.enabled`           | `true`                         | H2-Web-Console            |

### Frontend-Umgebungsvariablen

| Variable                | Default                   | Beschreibung                    |
|-------------------------|---------------------------|---------------------------------|
| `REACT_APP_API_URL`     | `http://localhost:8085`   | Backend-URL für API-Aufrufe     |

## Bekannte Limitationen

- **Keine Authentifizierung:** Aktuell keine Benutzer/Passwort-Abfrage. Nicht für Produktion geeignet.
- **H2 In-Memory-DB:** Daten gehen beim Neustart verloren. Für Produktion auf PostgreSQL/MySQL wechseln.
- **`ddl-auto=create-drop`:** Schema wird bei jedem Start neu erstellt. In Produktion `validate` + Flyway/Liquibase verwenden.

## Roadmap

Geplante Erweiterungen:
- Spring Security mit JWT-Authentifizierung
- Benutzer-Verwaltung (Todos pro User)
- Production-DB (PostgreSQL) + Flyway-Migrationen
- Dockerfile & docker-compose.yml
- Migration von Create React App auf Vite
- Pagination & Filtering für `GET /todos`

## Lizenz

Dieses Projekt ist Eigentum des Autors und nicht für die öffentliche Verbreitung freigegeben