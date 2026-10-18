# InternFlow — Development Environment Setup

This guide describes how to set up and run the InternFlow development foundation.
It reflects the **actual repository state** after Stage 1.5 and does not duplicate
the requirements or architecture documents in `docs/requirements/` and
`docs/architecture/`.

> **Status:** InternFlow is at the end of Stage 1 (Planning and Project Setup).
> Only a minimal, verifiable development foundation exists. No MVP features are
> implemented yet.

---

## 1. Repository Structure

```
InternFlow/
├── README.md                     # Project overview and quick start
├── .gitignore                    # Repository-wide ignore rules
├── .gitattributes                # Line-ending policy
├── docs/
│   ├── requirements/             # PRD, FRD, SRS (Stage 1.1–1.3, authoritative baseline)
│   ├── architecture/             # SAD, DDD, DECISIONS (Stage 1.4)
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

Optional, only needed for later stages or database work:

| Tool              | Needed for                                                   |
|-------------------|--------------------------------------------------------------|
| PostgreSQL 16+    | Running the backend against a live database (not yet required) |
| Docker            | Local PostgreSQL/container workflows (not used in Stage 1.5)   |

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

The application starts on <http://localhost:8080> by default.

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
    │   └── resources/application.yml          # Base configuration (env-var driven)
    └── test/java/com/internflow/InternFlowApplicationTests.java
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
(IF-DEC-04), but **no authentication or authorization mechanism is implemented**.
`SecurityConfig` currently:

- permits `GET /api/health` only, and
- requires authentication for every other request (deny-by-default),
- disables CSRF and creates no sessions, because no session/token mechanism
  exists yet.

All authentication mechanics were an **open decision (OD-15)**; the mechanics were **approved on 2026-10-18** (IF-DEC-19 in `docs/architecture/DECISIONS.md`: server-managed sessions, HttpOnly cookie, CSRF, email identity, BCrypt, and the approved policy values) but **are not implemented yet**. This configuration remains a development placeholder, not a production security posture, until a later Stage 2 substage implements the approved design.

---

## 6. PostgreSQL and Persistence Configuration

The database design is still a **draft** (`docs/architecture/DDD.md`). No entities,
migrations, or schema exist, and several schema decisions remain open (including
OD-02 and OD-03, which must resolve before the schema is finalized).

What is prepared:

- `backend/src/main/resources/application.yml` reads all database settings from
  environment variables with safe local placeholders.
- Hibernate DDL auto-generation defaults to `none`, and the PostgreSQL dialect is
  set explicitly so the application context can start **without** a live database.
- No credentials are stored in tracked files; no database host has been selected.

Expected environment variables (all optional locally; defaults in parentheses):

| Variable                    | Purpose                                             | Default                                          |
|-----------------------------|-----------------------------------------------------|--------------------------------------------------|
| `INTERNFLOW_DB_URL`         | JDBC URL                                            | `jdbc:postgresql://localhost:5432/internflow`    |
| `INTERNFLOW_DB_USERNAME`    | Database user                                       | `internflow`                                     |
| `INTERNFLOW_DB_PASSWORD`    | Database password (**never committed**)             | *(empty)*                                        |
| `INTERNFLOW_JPA_DDL_AUTO`   | Hibernate DDL mode (keep `none`/`validate`)         | `none`                                           |
| `SERVER_PORT`               | HTTP port                                           | `8080`                                           |

Example (PowerShell, current session only):

```powershell
$env:INTERNFLOW_DB_URL      = "jdbc:postgresql://localhost:5432/internflow"
$env:INTERNFLOW_DB_USERNAME = "internflow"
$env:INTERNFLOW_DB_PASSWORD = "<your-local-password>"
```

**Not tested:** Stage 1.5 did not connect to a live PostgreSQL instance. No
PostgreSQL client or server was available in the development environment at that
time, and no database host has been chosen. A live connection must be verified in
a later stage.

**Stage 1.6 review note (2026-10-17):** during the Stage 1.6 planning review, a
local PostgreSQL server **was** detected listening on `localhost:5432` (the
backend's connection attempt received a SCRAM authentication request and was
refused because no password was configured). Successful database authentication
and application connectivity have **not** been verified: they still require valid
credentials and a created database. No credentials are recorded here.

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
| Backend starts but data endpoints fail | Expected: no authentication and no schema exist yet. Only `/api/health` is reachable. |
| Database errors when adding entities later | PostgreSQL is not configured yet; set the `INTERNFLOW_DB_*` variables and revisit the schema design. |

---

## 9. What Is Deliberately Not Implemented Yet

Per Stage 1.5 scope, none of the following exist and none should be added without
the corresponding stage: user registration/login, JWT or session authentication,
role-management logic, internship or cohort CRUD, weekly reports, faculty review
workflows, document uploads, compliance calculation, notifications, completion
verification, database migrations, and JPA entities for the proposed schema.
