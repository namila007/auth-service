# Auth Service

[![GitHub Actions - Gradle](https://github.com/namila007/auth-service/actions/workflows/gradle.yml/badge.svg)](https://github.com/namila007/auth-service/actions/workflows/gradle.yml)
[![Java](https://img.shields.io/badge/java-25-blue.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Gradle-v9.2.0-green.svg)](https://gradle.org/)
[![Docker Compose](https://img.shields.io/badge/docker--compose-ready-blue.svg)](./infra/docker-compose.yaml)
![GitHub License](https://img.shields.io/github/license/namila007/auth-service)


Lightweight authentication service built with Java and Gradle, following a modular DDD structure. This repository contains the application code, domain modules, data layer, and an `infra/` folder with Docker-compose configuration for local development.

**Overview**
- **Purpose:** Provide authentication, user management and integration with Keycloak/Postgres for development and testing.
- **Stack:** Java 25, Spring Boot (modules), Gradle wrapper, Postgres, Redis, Keycloak, Docker Compose.

**Repository Layout**
- `auth-service/` : Root Gradle multi-project (top-level build).
- `auth-service-common/` : Shared libraries and utilities used across modules.
- `authservice-application/` : Spring Boot application entrypoint and runtime wiring.
- `authservice-data/` : Persistence layer, repositories, migrations, and DB integration.
- `authservice-domain/` : Domain model separated into `domain-core` and `domain-application`.
- `infra/` : Docker Compose and environment setup for Postgres, Redis, Keycloak, pgAdmin, and other infra resources.

**Getting Started (Local development)**
- Prerequisites: `Java 25`, Docker & Docker Compose, (optional) `pwsh`/PowerShell on Windows.
- Build the project (uses the included Gradle wrapper):

```pwsh
./gradlew.bat build
```

- Run tests:

```pwsh
./gradlew.bat test
```

- Start infrastructure for local development:

```pwsh
docker compose -f infra/docker-compose.yaml up -d
```

Check `infra/` for environment variables, mounts, and initialization scripts.

**Infra (quick summary)**
- `infra/docker-compose.yaml` — brings up services used by the app during local development:
  - Postgres: application database; `init-scripts/01-init-databases.sql` contains DB init SQL.
  - Redis: optional cache/session store (see `infra/data/redis`).
  - Keycloak: identity provider with `keycloak/realms/auth-service-realm.json` for realm import.
  - pgAdmin: DB admin UI for convenience (config under `infra/data/pgadmin`).

- Persistent data is stored in `infra/data/postgres/pgdata` and other `infra/data/*` folders.
- Use `infra/setup-env.ps1` or `infra/setup-env.bash` to quickly configure environment variables for local runs.

**Development notes**
- On Windows use `./gradlew.bat` (PowerShell) and on macOS/Linux `./gradlew`.
- The multi-module layout is intended to keep domain, application and infrastructure concerns separated.
- To add modules, follow the pattern in `authservice-domain/` and register them in `settings.gradle`.

**Useful files & locations**
- `init-scripts/01-init-databases.sql` — DB initialization
- `infra/docker-compose.yaml` — local infra orchestration
- `keycloak/realms/auth-service-realm.json` — Keycloak realm export for local import
