# InternFlow

InternFlow is an internship operations and compliance platform that manages the
internship lifecycle from registration to verified completion. It replaces
scattered spreadsheets, messages, emails, and documents with a centralized,
traceable workflow for students, faculty coordinators, and college
administrators.

## Current Status

**Stage 3 — User, Cohort & Internship Management (in progress).**

**Stage 3.1 — Database & API Design (approved).**

Requirements and architecture documentation exist in **draft** form under
`docs/`. The first physical database schema (six core tables) is implemented as a
Flyway migration and the Stage 3 API contract is drafted in
`docs/architecture/API-SPECIFICATION.md`. **No Stage 3 endpoint, JPA entity, or
business feature is implemented yet**, and authentication implementation is a
Stage 2 dependency not present on the Stage 3 branch.

## Repository Layout

| Path | Contents |
|------|----------|
| `docs/requirements/` | PRD, FRD, and SRS — the product baseline |
| `docs/architecture/` | Software architecture, domain/database design, decision register, API specification |
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

# Backend (separate terminal; requires a PostgreSQL database — see SETUP.md §6)
cd backend
mvn spring-boot:run    # http://localhost:8080/api/health
```

## Documentation

The documents in `docs/` are the authoritative baseline for the project. They are
currently marked **Draft — Pending Review** and contain unresolved decisions
(OD-01 through OD-16, except OD-15 which was closed by IF-DEC-19) that must be
resolved before the affected areas are finalized.
