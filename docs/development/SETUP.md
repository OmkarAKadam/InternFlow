# InternFlow — Development Environment Setup

This guide describes how to set up and run the InternFlow development foundation.
It reflects the **actual repository state at Stage 3.1** and does not duplicate
the requirements or architecture documents in `docs/requirements/` and
`docs/architecture/`.

> **Status:** Stage 3.1 (Database & API Design). The Stage 3 user/cohort/
> internship schema exists as a Flyway migration and the API contract is drafted
> in `docs/architecture/API-SPECIFICATION.md`. **No Stage 3 endpoint, JPA entity,
> or business feature is implemented yet.** Authentication implementation is a
> Stage 2 dependency and is not present in this branch (see §5).

---

## 1. Repository Structure

```
InternFlow/
├── README.md                     # Project overview and quick start
├── .gitignore                    # Repository-wide ignore rules
├── .gitattributes                # Line-ending policy
├── docs/
│   ├── requirements/             # PRD, FRD, SRS (Stage 1.1–1.3, authoritative baseline)
│   ├── architecture/             # SAD, DDD, DECISIONS, API-SPECIFICATION
│   ├── reviews/                  # Stage review reports
│   └── development/
│       └── SETUP.md              # This file
├── frontend/                     # Next.js + React + TypeScript + Tailwind application
└── backend/                      # Java + Spring Boot modular-monolith application
```

---

## 2. Prerequisites

| Tool     | Required version     | Notes                                              |
|----------|----------------------|----------------------------------------------------|
| Git      | 2.x                  |                                                    |
| Node.js  | 20.x or newer (LTS)  | Verified locally with Node 24.x; npm ships with it |
| npm      | 10.x or newer        |                                                    |
| JDK      | 21 or newer          | Spring Boot 3.5 requires Java 17+; project targets Java 21 |
| Maven    | 3.9.x                | Confirmed build tool for the backend (see §5)      |

Optional, only for frontend work:

| Tool              | Needed for                                                   |
|-------------------|--------------------------------------------------------------|
| Docker            | Not used; no container workflow is configured                 |

**PostgreSQL 16 or newer is now required to start the backend**, because Flyway
runs the Stage 3.1 migration at startup (IF-DEC-20). PostgreSQL 18.3 was used for
the Stage 3.1 verification; Flyway 11.7.2 emits a "newer than tested" warning for
PostgreSQL 18 but the migration applies successfully.

Check your environment:

```powershell
git --version
node --version
npm --version
java -version
mvn -version
```

---

## 3. Installed Versions Selected During Stage 1.5

These versions were selected during Stage 1.5 by inspecting the available
environment. Only the technology stack itself is confirmed in the project
baseline (PRD §11.2); specific versions are **proposed implementation choices**
and may be revisited.

| Component       | Version selected | Location                                       |
|-----------------|------------------|------------------------------------------------|
| Next.js         | 16.4.2           | `frontend/package.json`                        |
| React           | As scaffolded by Next.js 16 | `frontend/package.json`             |
| TypeScript      | As scaffolded by Next.js 16 | `frontend/package.json`             |
| Tailwind CSS    | As scaffolded by Next.js 16 | `frontend/package.json`             |
| Java (target)   | 21 (LTS)         | `backend/pom.xml` (`java.version`)             |
| Spring Boot     | 3.5.16           | `backend/pom.xml` (parent)                     |
| Build tool      | Maven            | `backend/pom.xml`                              |

Rationale for Maven: `mvn` (3.9.14) is already installed and available, Gradle
is not installed on this machine, and the architecture documents do not prescribe
a build tool. Only one build configuration is maintained.

---

## 4. Frontend Setup (`frontend/`)

**Stack:** Next.js (App Router) + React + TypeScript + Tailwind CSS.

### Install dependencies

```powershell
cd frontend
npm install
```

### Run the development server

```powershell
npm run dev
```

Open <http://localhost:3000>. The landing page identifies the application as
InternFlow and states that the development foundation is in progress.

### Production build and start

```powershell
npm run build
npm run start
```

### Linting and type checking

```powershell
npm run lint
npm run typecheck
```

### Current routes

- `/` — neutral landing page with a link to sign in.
- `/login` — sign-in interface. It validates input and presents loading and
  error states, but is **not connected to a backend**: no approved
  authentication API contract exists yet, so submission shows an
  integration-pending notice and sends no request. See
  `docs/reviews/STAGE-2.2-REVIEW.md` §8 for what is required to connect it.

### Environment variables

- Copy `frontend/.env.example` to `frontend/.env.local` and fill in values as
  needed. `.env.local` is git-ignored and must never be committed.
- Only variables whose values are safe in the browser may use the
  `NEXT_PUBLIC_` prefix.
- Values in `.env.example` are **placeholders only**.

---

## 5. Backend Setup (`backend/`)

**Stack:** Java + Spring Boot (modular monolith), Spring Security (framework only),
Spring Data JPA, PostgreSQL driver. Build tool: **Maven**.

### Build

```powershell
cd backend
mvn clean package
```

### Run tests

```powershell
mvn test
```

### Run the application

```powershell
mvn spring-boot:run
# or, after packaging:
java -jar target/internflow-backend-0.1.0-SNAPSHOT.jar
```

The application starts on <http://localhost:8080> by default. **A reachable,
migrated PostgreSQL database is required** (see §6); startup fails if Flyway
cannot connect or migrate.

### Health endpoint

```
GET http://localhost:8080/api/health
```

Returns a constant, non-sensitive liveness response:

```json
{ "application": "internflow-backend", "status": "UP" }
```

The endpoint is intentionally unauthenticated (proposed decision IF-DEC-18) and
exposes no configuration, dependency, or data details.

### Backend structure

```
backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/internflow/
    │   │   ├── InternFlowApplication.java     # Spring Boot entry point
    │   │   └── shared/                        # Cross-cutting foundation only
    │   │       ├── config/SecurityConfig.java # Placeholder, deny-by-default
    │   │       └── health/HealthController.java
    │   └── resources/
    │       ├── application.yml                # Base configuration (env-var driven)
    │       └── db/migration/
    │           └── V1__create_stage3_core_tables.sql  # Stage 3.1 schema
    └── test/java/com/internflow/
        ├── InternFlowApplicationTests.java    # Context test (no database)
        └── persistence/
            └── Stage3SchemaMigrationTest.java # Opt-in live migration test
```

**Planned module layout.** The 13 MVP modules will live as packages under
`com.internflow` (e.g. `com.internflow.reports`, `com.internflow.review`,
`com.internflow.compliance`). Those packages are **not created yet** — they will
be added when each module actually contains code. The recommended layout is
recorded in `docs/architecture/SAD.md` §5.14. Per that document, the Faculty
Review Workflow is kept as its own `review` package (matching the 13-module list)
rather than merged into `reports` (Stage 1.5 choice, proposed).

### Security status — read this before assuming anything is protected

Spring Security is on the classpath because it is part of the confirmed stack
(IF-DEC-04). **In this branch (Stage 3.1), no authentication or authorization
mechanism is implemented.** `SecurityConfig` still:

- permits `GET /api/health` only,
- requires authentication for every other request (deny-by-default),
- disables CSRF and creates no sessions.

All authentication mechanics were an **open decision (OD-15)**; the mechanics
were **approved on 2026-10-18** (IF-DEC-19 in `docs/architecture/DECISIONS.md`:
server-managed sessions, HttpOnly cookie, CSRF, email identity, BCrypt, and the
approved policy values). The Stage 2 implementation of that design exists only
as uncommitted work on the separate `stage-2-ui-authentication` branch and is
**not present in this worktree**. The Stage 3.1 API contract
(`docs/architecture/API-SPECIFICATION.md` §3.3) assumes that work is integrated
before any protected Stage 3 endpoint is used. Until then, this configuration is
a development placeholder, not a production security posture.

**Do not implement Stage 3 authorization around the placeholder chain.** Merge
or rebase the Stage 2 authentication work first, then enforce role and record
scope per the API Specification.

---

## 6. PostgreSQL, Flyway, and Persistence Configuration

The Stage 3.1 physical schema is created by Flyway migration
`backend/src/main/resources/db/migration/V1__create_stage3_core_tables.sql`
(IF-DEC-20/IF-DEC-22; see `docs/architecture/DDD.md` §15). Hibernate never
creates or alters the schema. Later tables (reports, documents, compliance,
completion, notifications, audit) remain deferred while OD-02/OD-03/etc. are open.

### 6.1 Create a local database

Create a database and role once. Example using the PostgreSQL `psql` client
(adjust the password; never commit it):

```powershell
$env:PGPASSWORD = "<postgres-admin-password>"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE ROLE internflow LOGIN PASSWORD '<local-password>';"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE internflow OWNER internflow;"
Remove-Item Env:PGPASSWORD
```

### 6.2 Environment variables

| Variable                    | Purpose                                             | Default                                          |
|-----------------------------|-----------------------------------------------------|--------------------------------------------------|
| `INTERNFLOW_DB_URL`         | JDBC URL                                            | `jdbc:postgresql://localhost:5432/internflow`    |
| `INTERNFLOW_DB_USERNAME`    | Database user                                       | `internflow`                                     |
| `INTERNFLOW_DB_PASSWORD`    | Database password (**never committed**)             | *(empty)*                                        |
| `INTERNFLOW_FLYWAY_ENABLED` | Run migrations at startup (`false` for diagnosis only) | `true`                                        |
| `INTERNFLOW_JPA_DDL_AUTO`   | Hibernate DDL mode (keep `none`/`validate`)         | `none`                                           |
| `SERVER_PORT`               | HTTP port                                           | `8080`                                           |

Example (PowerShell, current session only):

```powershell
$env:INTERNFLOW_DB_URL      = "jdbc:postgresql://localhost:5432/internflow"
$env:INTERNFLOW_DB_USERNAME = "internflow"
$env:INTERNFLOW_DB_PASSWORD = "<your-local-password>"
```

Starting the backend applies any pending migrations automatically. A missing
database, bad credentials, or a failed migration stops startup — this is a
configuration/technical failure, not a reason to disable Flyway in normal use.

### 6.3 Running the tests

```powershell
cd backend
mvn test
```

`mvn test` does **not** need PostgreSQL: Surefire sets
`spring.flyway.enabled=false`, and the migration test is skipped unless
`INTERNFLOW_TEST_DB_URL` is set.

To verify the real migration against a **disposable** database, point these
variables at it (the test creates and drops a uniquely named schema; do not use a
database whose `public` schema you care about):

| Variable | Purpose |
|---|---|
| `INTERNFLOW_TEST_DB_URL` | JDBC URL of a disposable database; presence enables the test |
| `INTERNFLOW_TEST_DB_USERNAME` | Database user |
| `INTERNFLOW_TEST_DB_PASSWORD` | Password (may be empty only for a local trust-auth test server) |

```powershell
$env:INTERNFLOW_TEST_DB_URL      = "jdbc:postgresql://127.0.0.1:5432/internflow_test"
$env:INTERNFLOW_TEST_DB_USERNAME = "postgres"
$env:INTERNFLOW_TEST_DB_PASSWORD = "<local-test-password>"
mvn -B -Dtest=Stage3SchemaMigrationTest test
```

The test verifies table creation, unique/check constraints, the AS-01/AS-02/AS-03
partial unique indexes, date ordering, and `ON DELETE RESTRICT`. PostgreSQL 18
reports a RESTRICT violation as SQLSTATE `23001`; earlier versions use `23503`,
and the test accepts both.

### 6.4 Creating the next migration

Add `V2__<description>.sql` beside `V1`; never edit a migration already applied to
a shared database. Each schema change must also update `docs/architecture/DDD.md`
§15 and, if it affects the contract, `docs/architecture/API-SPECIFICATION.md`.

**Verification status:** Stage 3.1 executed `V1` through application startup and
through `Stage3SchemaMigrationTest` against PostgreSQL 18.3 using a disposable
local cluster. The password-protected system service on `localhost:5432` was not
used, and no production database host has been selected. No credentials are
recorded here.

---

## 7. Repository Conventions

### Line endings (`.gitattributes`)

`* text=auto` normalizes text files to LF in the repository while keeping native
line endings in working copies (CRLF on Windows). Binary formats are declared so
Git never modifies them. This policy applies to future changes; previously
committed documentation was **not** rewritten.

### Ignored files (`.gitignore`)

Build output (`node_modules/`, `.next/`, `target/`), local environment files
(`.env`, `.env.local`, and similar), logs, test artifacts, IDE files, and OS files
are ignored. Example/template files such as `.env.example` **are** tracked, and
they contain placeholders only.

---

## 8. Common Setup Issues

| Symptom | Likely cause / fix |
|---|---|
| `mvn` not recognized | Maven is not installed or not on `PATH`. Install Maven 3.9+ or use an IDE's bundled Maven. |
| `java -version` shows an older JDK | The backend targets Java 21. Install JDK 21+ and point `JAVA_HOME` at it. |
| Frontend `npm install` fails | Check network/proxy access to the npm registry, then retry. |
| Port already in use | Change the port: `npm run dev -- -p 3001`, or set `SERVER_PORT` for the backend. |
| Backend starts but data endpoints fail | Expected: no Stage 3 endpoint is implemented. Only `/api/health` is reachable. |
| Backend fails at startup with a Flyway/JDBC error | PostgreSQL is not running, the database does not exist, or `INTERNFLOW_DB_*` credentials are wrong. Fix the database; do not disable Flyway permanently. |
| Flyway warns that PostgreSQL 18 is newer than tested | Informational for Flyway 11.7.2; the Stage 3.1 migration was verified on PostgreSQL 18.3. Pin a newer Flyway only after testing. |

---

## 9. What Is Deliberately Not Implemented Yet

Per Stage 3.1 scope, none of the following exist and none should be added without
the corresponding stage: working authentication/authorization in this branch,
role-management endpoints, company/cohort/internship endpoints, JPA entities and
repositories, weekly reports, faculty review workflows, document uploads,
compliance calculation, notifications, completion verification, and the deferred
database tables listed in `docs/architecture/DDD.md` §15.4.
