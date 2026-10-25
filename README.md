# InternFlow

InternFlow is an internship operations and compliance platform that manages the
internship lifecycle from registration to verified completion. It replaces
scattered spreadsheets, messages, emails, and documents with a centralized,
traceable workflow for students, faculty coordinators, and college
administrators.

## Current Status

**Stage 2.4 — Stage Verification (report pending acceptance).**

Stage 2.3, the authentication backend foundation, is approved with documented
limitations. The Stage 2.4 verification pass re-ran the backend suite (38/38),
`mvn -B verify`, and the frontend lint/type checks; results are recorded in
`docs/reviews/STAGE-2.4-REVIEW.md`.

Authentication is **not production-ready**. No persisted accounts exist
(OD-02/OD-03), production sign-in returns the documented
`503 sign-in-not-configured` response, rate limiting and the final `SameSite`
decision remain unresolved, and the frontend login form is not connected. No
MVP business features are implemented, and no database schema or migrations
exist.

## Repository Layout

| Path | Contents |
|------|----------|
| `docs/requirements/` | PRD, FRD, and SRS — the product baseline |
| `docs/architecture/` | Software architecture, domain/database design, decision register |
| `docs/development/` | Development environment setup guide |
| `frontend/` | Next.js + React + TypeScript + Tailwind CSS |
| `backend/` | Java + Spring Boot modular monolith, built with Maven |

## Confirmed Technology Stack

- **Frontend:** Next.js, React, TypeScript, Tailwind CSS
- **Backend:** Java, Spring Boot, Spring Security
- **Database:** PostgreSQL, Spring Data JPA, Hibernate
- **Testing:** JUnit, Spring Boot Test, Playwright
- **Architecture:** modular monolith (a single backend deployable, not microservices)

## Quick Start

Prerequisites, environment variables, configuration details, and troubleshooting
are documented in **[docs/development/SETUP.md](docs/development/SETUP.md)**.

```powershell
# Frontend
cd frontend
npm install
npm run dev            # http://localhost:3000

# Backend (separate terminal)
cd backend
mvn spring-boot:run    # http://localhost:8080/api/health
```

## Documentation

The documents in `docs/` are the authoritative baseline for the project. They are
currently marked **Draft — Pending Review** and contain unresolved decisions
(OD-01 through OD-16) that must be resolved before the affected areas are
finalized.
