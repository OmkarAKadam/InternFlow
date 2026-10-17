# InternFlow Technology and Architecture Decisions Record

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | InternFlow Technology and Architecture Decisions Record |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-DEC-001 |
| Version | 1.0 |
| Status | Draft — Pending Review (not approved) |
| Last Updated | 2026-10-17 |
| Purpose | Record every technology and architecture decision relevant to the InternFlow MVP — what is confirmed by the requirements baseline, what is proposed by this document and awaits approval, and what remains open — with context, rationale, consequences, alternatives, and traceability to the source requirements. This document decides nothing by itself; approval belongs to human review. |
| Related Source Documents | PRD v1.0 — `docs/requirements/PRD.md` (baseline commit `9f91303`); FRD v1.0 — `docs/requirements/FRD.md` (commit `c8b8754`); SRS v1.0 — `docs/requirements/SRS.md` (commit `7ffeecc`); README — `README.md`. Companion deliverables: `docs/architecture/SAD.md` (System Architecture Document), `docs/architecture/DDD.md` (Database Design Document). |
| Intended Audience | Project reviewer/stakeholder approving the architecture baseline; the development team implementing the MVP; authors of the API Specification, Security Requirements, Test Plan, and Deployment and Operations Guide. |

**Review note:** This document remains a draft pending human review and must not be treated as wholesale-approved. At the Stage 1.6 planning review (2026-10-17) the six formerly **Proposed** entries (IF-DEC-12, IF-DEC-13, IF-DEC-15, IF-DEC-16, IF-DEC-17, IF-DEC-18) were **approved with the boundaries recorded in §5**, and the AS-01/AS-02/AS-03 dependency note (§8) was accepted (assumptions remain assumptions — §8). Every entry marked **Open** and the remaining §11 review actions are unresolved until a reviewer signs them off. It does not modify the PRD, FRD, or SRS, invents no requirement IDs, retention periods, SLAs, reporting cadences, or endpoint contracts, and where the source documents leave a matter open, it records the gap rather than inventing a rule.

**Status conventions** (consistent with FRD §2.4 and SRS §2.6):

- **Confirmed** — established by the PRD/FRD/SRS baseline; the source is cited. Confirmed entries here restate the baseline; they do not extend it.
- **Proposed** — offered by this record (and reflected in `SAD.md`/`DDD.md`) because some design must exist; requires explicit approval before being treated as decided.
- **Approved** — originally **Proposed** by this record and explicitly approved at human review with any boundaries recorded in the entry; approved entries do not become part of the requirements baseline and do not resolve any open decision.
- **Open** — genuinely unresolved; either carried forward from FRD §9 open decisions (**OD-xx**) or identified here as an item no source document settles. No value is invented in the meantime.
- **Assumption (AS-xx)** — FRD §9.3 assumptions are reproduced unchanged where they bear on a decision; they are not approved.

## Table of Contents

- [1. Document Control](#1-document-control)
- [2. Scope and How to Read This Record](#2-scope-and-how-to-read-this-record)
- [3. Decision Register](#3-decision-register)
- [4. Confirmed Decisions (IF-DEC-01 … IF-DEC-11)](#4-confirmed-decisions-if-dec-01--if-dec-11)
- [5. Approved Decisions (IF-DEC-12, IF-DEC-13, IF-DEC-15 … IF-DEC-18)](#5-approved-decisions-if-dec-12-if-dec-13-if-dec-15--if-dec-18)
- [6. Open Items (IF-DEC-10, IF-DEC-14, and Provider Selections)](#6-open-items-if-dec-10-if-dec-14-and-provider-selections)
- [7. Interaction with FRD Open Decisions (OD-01 … OD-16)](#7-interaction-with-frd-open-decisions-od-01--od-16)
- [8. Assumptions Affecting Architecture (AS-01, AS-02, AS-03)](#8-assumptions-affecting-architecture-as-01-as-02-as-03)
- [9. Alternatives Considered and Rejected](#9-alternatives-considered-and-rejected)
- [10. Traceability](#10-traceability)
- [11. Review Checklist and Approval Status](#11-review-checklist-and-approval-status)

## 2. Scope and How to Read This Record

This record covers technology and architecture choices only: architecture style, application stack, security framework, datastore and persistence, storage, notifications, hosting, API style, concurrency and derived-data strategy, and operational basics. Product rules — role boundaries, the five report states and their transitions, faculty-only weekly-report approval, completion authority, the 13 MVP modules — are **not** decided here; they are requirements owned by the PRD/FRD/SRS and are only cited as constraints on the decisions below. In particular:

- Administrators receive **no** report-approval or completion-verification authority by default (FRD §4; any change is **OD-01**/**OD-08**).
- Weekly-report approval belongs to faculty coordinators only (FRD §4, §5).
- The report state model has exactly five states — `DRAFT`, `SUBMITTED`, `CORRECTION_REQUESTED`, `RESUBMITTED`, `APPROVED` — and only the transitions defined in FRD §5.2 (SRS-FR-003).

Each entry below uses a fixed structure:

| Field | Meaning |
|---|---|
| **Status** | Confirmed / Proposed / Open, as defined above |
| **Context** | The problem or constraint, with source citation |
| **Decision** | What is decided (or proposed / what must be decided) |
| **Consequences** | What this choice implies — benefits, costs, follow-on work |
| **Alternatives** | Options considered and why they were not chosen |
| **Related requirements** | Requirement IDs, all of which resolve to `FRD.md` or `SRS.md` |

## 3. Decision Register

| ID | Decision | Status | Primary source |
|---|---|---|---|
| IF-DEC-01 | Modular monolith architecture | **Confirmed** | PRD §11.2; NFR-MNT-001 |
| IF-DEC-02 | Frontend stack: Next.js, React, TypeScript, Tailwind CSS | **Confirmed** | PRD §11.2, §12 |
| IF-DEC-03 | Backend stack: Java and Spring Boot | **Confirmed** | PRD §11.2 |
| IF-DEC-04 | Authentication and authorization framework: Spring Security | **Confirmed** | PRD §11.2; FR-AUTH-003, FR-AUTH-004 |
| IF-DEC-05 | Primary datastore: PostgreSQL (hosting **Open**) | **Confirmed** (hosting open) | PRD §11.2; SRS-IF-06 |
| IF-DEC-06 | ORM and persistence: Spring Data JPA and Hibernate | **Confirmed** | PRD §11.2; SRS-IF-06 |
| IF-DEC-07 | Private object storage for document content (vendor **Open**) | **Confirmed** (vendor open) | PRD §11.2; FR-DOC-007; SRS-IF-11 |
| IF-DEC-08 | Notification channels: email and in-app (transport/provider **Open**, OD-09) | **Confirmed** (transport open) | PRD §11.2; SRS-IF-13, SRS-IF-17 |
| IF-DEC-09 | Frontend deployment: Vercel | **Confirmed** | PRD §11.2 |
| IF-DEC-10 | Backend hosting environment | **Open** | PRD §11.2 ("to be selected and configured") |
| IF-DEC-11 | Testing technologies: JUnit, Spring Boot Test, Playwright | **Confirmed** | PRD §11.2; NFR-MNT-004 |
| IF-DEC-12 | REST API style over JSON for the frontend–backend boundary | **Approved** (Stage 1.6) | SRS-IF-01 (boundary confirmed; style not) |
| IF-DEC-13 | Persisted derived compliance evaluations (`requirement_evaluations`) | **Approved** (Stage 1.6, bounded) | NFR-PERF-004, NFR-DAT-006, FR-COMP-007 |
| IF-DEC-14 | API versioning strategy | **Open** | No source document establishes it |
| IF-DEC-15 | Synchronous, in-transaction compliance recalculation | **Approved** (Stage 1.6, bounded) | NFR-REL-001, NFR-REL-005, NFR-DAT-006 |
| IF-DEC-16 | Optimistic locking (version column) for workflow concurrency | **Approved** (Stage 1.6) | NFR-DAT-008; FR-REPORT-006 |
| IF-DEC-17 | No message broker or background-job platform in the MVP | **Approved** (Stage 1.6, bounded) | PRD §11.1, §14.1; SRS §3.4 |
| IF-DEC-18 | Health endpoint and basic structured logging | **Approved** (Stage 1.6) | No quantitative source requirement; NFR-REL-008 (no SLA invented) |

Counts: 10 Confirmed (IF-DEC-01 … IF-DEC-09, IF-DEC-11), 6 Approved (IF-DEC-12, 13, 15, 16, 17, 18 — formerly Proposed; approved at the Stage 1.6 planning review, 2026-10-17, with the boundaries recorded in §5), 2 Open (IF-DEC-10, IF-DEC-14). IF-DEC-05/07/08 are Confirmed with an embedded Open sub-item (hosting, vendor, transport). Total: 18 decisions. No entry resolves an FRD open decision; those remain in §7.

## 4. Confirmed Decisions (IF-DEC-01 … IF-DEC-11)

The entries below cover the established technology baseline of PRD §11.2 (IF-DEC-01 … IF-DEC-09 and IF-DEC-11 are **Confirmed**; IF-DEC-10 is presented here in register order but is **Open** — see §6). Confirmed entries restate the baseline; the context, consequences, and alternatives are supplied by this record for review and may be challenged without any requirement changing.

### IF-DEC-01 — Modular monolith architecture

- **Status:** Confirmed.
- **Context:** The PRD fixes the architecture style as a modular monolith and lists microservices as an explicit non-goal (PRD §11.2, §5.2). The system targets a single college department or internship cohort (PRD §11.1; SRS §3.4) with a 12-week MVP timeline (PRD §11.1).
- **Decision:** One deployable backend application organized into the 13 MVP modules with explicit boundaries; no module reaches into another module's internals except through its service surface. The frontend is a separate deployable (IF-DEC-02, IF-DEC-09).
- **Consequences:** Simplest deployment and debugging; transactional integrity across modules is in-process (supports IF-DEC-15). Module boundaries depend on team discipline and review, since the compiler does not enforce them; recommended package boundaries are recorded in `SAD.md` §5.14 as a recommendation, not a decision.
- **Alternatives:** Microservices — rejected by the PRD non-goal and unjustified at single-cohort scale (PRD §5.2; `SAD.md` P7/P9). Serverless function decomposition — not established by any source document and unnecessary for a two-deployable design.
- **Related requirements:** NFR-MNT-001; NFR-MNT-002; PRD §11.2, §14.1.

### IF-DEC-02 — Frontend stack: Next.js, React, TypeScript, Tailwind CSS

- **Status:** Confirmed.
- **Context:** PRD §11.2 establishes Next.js, React, and TypeScript for the frontend and Tailwind CSS for styling; PRD §12 notes these as established technologies. The frontend presents and collects; it never enforces authorization (SRS-FR-001; NFR-SEC-001).
- **Decision:** The frontend is built with Next.js, React, TypeScript, and Tailwind CSS, deployed as its own project (IF-DEC-09), communicating with the backend only through the API boundary (IF-DEC-12).
- **Consequences:** Type sharing across the API boundary is possible but must not weaken server-side validation (SRS-IF-03); interface hiding is never an enforcement mechanism (NFR-SEC-001). Accessibility conformance target remains open (PRD §12) and is not claimed here.
- **Alternatives:** Other React meta-frameworks or a separately assembled React toolchain — not established by the baseline; adopting them would contradict PRD §11.2 without a requirement to do so.
- **Related requirements:** PRD §11.2, §12; SRS-IF-01; NFR-SEC-001.

### IF-DEC-03 — Backend stack: Java and Spring Boot

- **Status:** Confirmed.
- **Context:** PRD §11.2 establishes Java and Spring Boot as the backend. The backend owns business rules, authorization, and persistence (NFR-MNT-002; `SAD.md` §6).
- **Decision:** A single Spring Boot application implements all 13 modules, layered as controller → service → repository with authorization enforced before any business processing (`SAD.md` §6, §8).
- **Consequences:** One runtime, one build, one deployment unit; Spring Boot's transaction support underpins the atomic audit-with-action requirement (NFR-REL-002). The choice couples persistence (IF-DEC-06) and security (IF-DEC-04) to the Spring ecosystem.
- **Alternatives:** Other JVM frameworks (Jakarta EE, Micronaut, Quarkus) — not established by the baseline. Node.js or Python backends — would contradict PRD §11.2 and the fixed Spring Security decision.
- **Related requirements:** PRD §11.2; NFR-MNT-002; SRS §5.2.

### IF-DEC-04 — Authentication and authorization framework: Spring Security

- **Status:** Confirmed. The framework is confirmed; the authentication *mechanics* (credentials, password policy, token/session lifetime, lockout) are **Open** under OD-15 and are not decided here (FRD §9; NFR-SEC-009).
- **Context:** PRD §11.2 fixes Spring Security. Every protected operation must be authorized server-side with role and record scope, deny-by-default (SRS-FR-001, SRS-FR-007; NFR-SEC-001, NFR-SEC-002).
- **Decision:** Spring Security is the authentication and authorization framework, providing the server-side enforcement layer for the FRD §4 permission matrix. Specific mechanics — credential model, session vs. token, lifetimes, lockout — are deferred to the Security Requirements per OD-15 and must not be assumed by other deliverables.
- **Consequences:** Deny-by-default annotations/filters are the single enforcement point; frontend routing guards are convenience only (NFR-SEC-001). Any early implementation must be revisited when OD-15 resolves; the MVP must not rely on a specific mechanism before then (NFR-SEC-009).
- **Alternatives:** Hand-rolled authorization filters — rejected: higher defect risk against NFR-SEC-002 and unnecessary given the fixed Spring stack. Third-party identity providers — not established by any source document; no external integration is in MVP scope (SRS-FR-002).
- **Related requirements:** PRD §11.2; FR-AUTH-003; FR-AUTH-004; NFR-SEC-001; NFR-SEC-002; NFR-SEC-009; OD-15.

### IF-DEC-05 — Primary datastore: PostgreSQL

- **Status:** Confirmed (instance **hosting** is Open — see §6).
- **Context:** PRD §11.2 establishes PostgreSQL for structured data. Referential integrity, atomic state transitions, and persistence-level uniqueness are required (SRS-IF-07, SRS-IF-08; NFR-DAT-001, NFR-DAT-002).
- **Decision:** PostgreSQL is the single system of record for all structured data, including audit history and the persisted compliance evaluations (IF-DEC-13). Where a vendor or managed service is needed, selection is deferred (§6); no hosting commitment is invented.
- **Consequences:** Constraints (unique, partial-unique, check) enforce key invariants at the persistence layer as required (SRS-IF-08); the schema design lives in `DDD.md`. Backup, retention, and availability targets are **not** established: retention is OD-11 and no uptime/RTO/RPO target exists (NFR-REL-008).
- **Alternatives:** Other relational stores — would contradict PRD §11.2. Document or key-value stores as primary — rejected: referential integrity across 14 entity types is a Confirmed requirement (NFR-DAT-001).
- **Related requirements:** PRD §11.2; SRS-IF-06; SRS-IF-07; NFR-DAT-001; NFR-DAT-002.

### IF-DEC-06 — ORM and persistence: Spring Data JPA and Hibernate

- **Status:** Confirmed.
- **Context:** PRD §11.2 fixes Spring Data JPA and Hibernate as the persistence layer (SRS-IF-06).
- **Decision:** All persistence access goes through Spring Data JPA repositories with Hibernate as the ORM; schema-affecting choices (constraints, version columns, indexes) are specified in `DDD.md` and applied through versioned migrations managed at the appropriate implementation stage.
- **Consequences:** Declarative transactions support the atomic audit-with-action rule (NFR-REL-002) and in-transaction recalculation (IF-DEC-15); lazy-loading and N+1 behavior must be watched against NFR-PERF-003/NFR-PERF-004. Uniqueness and check constraints must still be expressed at the database level — the ORM is not the enforcement mechanism for NFR-DAT-002 (see `DDD.md` C-016, interim constraints under OD-12/OD-05/OD-08).
- **Alternatives:** jOOQ or plain JDBC — not established by the baseline. A framework-level caching layer — not introduced: no source requirement demonstrates the need at single-college scale (`SAD.md` P7).
- **Related requirements:** PRD §11.2; SRS-IF-06; NFR-REL-002; NFR-PERF-004.

### IF-DEC-07 — Private object storage for document content

- **Status:** Confirmed as a decision category ("private object storage", PRD §11.2); the **vendor/provider is Open** (§6).
- **Context:** Document content must be stored privately and served only through permission-checked paths, with no public or guessable URL at any time (FR-DOC-007; NFR-SEC-004; SRS-IF-11).
- **Decision:** Uploaded document bytes live in private object storage; the backend is the only component with storage credentials, and retrieval always goes through a permission-checked backend path. Storage failures surface as technical failures with no half-created document record (SRS-IF-12). The vendor is not selected.
- **Consequences:** Every download costs an authorization check plus a storage round trip; presigned or public URLs are prohibited by NFR-SEC-004 and must not appear as a "performance fix". Vendor selection must preserve these properties regardless of product chosen.
- **Alternatives:** Serving files from the local filesystem of the backend instance — rejected: couples durability and horizontal scaling to one instance and complicates the private-access guarantee; also unestablished by PRD §11.2. Public or CDN-bucket URLs — rejected: violates NFR-SEC-004 and SRS-IF-11.
- **Related requirements:** FR-DOC-007; NFR-SEC-004; SRS-IF-11; SRS-IF-12; SRS-FR-008; PRD §11.2.

### IF-DEC-08 — Notification channels: email and in-app

- **Status:** Confirmed as channels (PRD §11.2); **email transport, retries, and delivery guarantees are Open** under OD-09 (SRS-IF-17).
- **Context:** Notifications are delivered through an in-application notification store plus a configured email delivery service (SRS-IF-13). Delivery is non-blocking: failure never rolls back or alters the triggering operation, and the failure is recorded (SRS-IF-14; NFR-REL-004). Recipients are resolved from record relationships, never user-supplied addresses (SRS-FR-006). No sensitive document content is embedded (SRS-IF-15).
- **Decision:** In-app notification rows are persisted in PostgreSQL (IF-DEC-05); email dispatch is a best-effort, post-commit, in-process step. Trigger set, timing, retries, and duplicate-suppression policy remain open under OD-09 and are not decided here.
- **Consequences:** In-app notifications get database-grade consistency and auditability for free; email is at-most-once in practice until OD-09 resolves retry behavior. Duplicate prevention (NFR-REL-006) must be designed into the event → notification path from the start.
- **Alternatives:** SMS or push channels — excluded from the MVP (SRS-FR-002 lists "extra notification channels" as post-MVP). A message broker or third-party notification platform — see IF-DEC-17 and OD-09.
- **Related requirements:** SRS-IF-13; SRS-IF-14; SRS-IF-15; SRS-IF-17; SRS-FR-006; NFR-REL-004; NFR-REL-006; OD-09.

### IF-DEC-09 — Frontend deployment: Vercel

- **Status:** Confirmed.
- **Context:** PRD §11.2 establishes Vercel for frontend deployment; PRD §11.3 lists "Vercel for the frontend and suitable backend hosting" as the hosting assumption.
- **Decision:** The frontend is deployed on Vercel. Backend hosting is separate and open (IF-DEC-10).
- **Consequences:** Frontend environment configuration (API base URL, TLS via platform) is managed in Vercel; secrets must still be externalized and never committed (NFR-SEC-008). Cross-origin configuration between Vercel and the backend host must be defined when the backend host is chosen (part of the API Specification work, not decided here).
- **Alternatives:** Static hosting on the backend host — possible but would depart from PRD §11.2 without a requirement; other JAMstack hosts — unestablished.
- **Related requirements:** PRD §11.2, §11.3; NFR-SEC-008; SRS-IF-16.

### IF-DEC-10 — Backend hosting environment

- **Status:** **Open.** PRD §11.2 states "Suitable backend hosting, to be selected and configured"; no source document selects a provider, region, or topology.
- **Context:** The backend (IF-DEC-03) and PostgreSQL (IF-DEC-05) both need a runtime home. NFR-REL-008 forbids inventing uptime or recovery commitments.
- **Decision:** **Not decided.** To be selected and documented in the Deployment and Operations Guide. Decision criteria that any option must satisfy: runs a Spring Boot JVM process, supports externalized secrets (NFR-SEC-008), TLS termination in deployed environments (SRS-IF-16), connectivity to PostgreSQL and to private object storage (IF-DEC-07), and no public exposure of database or storage credentials.
- **Consequences:** Until selected, deployment diagrams in `SAD.md` §15 show a logical "backend host" placeholder; performance targets that depend on topology (NFR-PERF-002) cannot be finalized.
- **Alternatives:** Candidates (container platform, VM with reverse proxy, platform-as-a-service) are intentionally not enumerated as a shortlist here, because the baseline establishes no evaluation criteria and no source favors one; enumerating them risks implying a recommendation. The reviewer may direct a shortlist as a review action (§11).
- **Related requirements:** PRD §11.2, §11.3; NFR-REL-008; NFR-SEC-008; SRS-IF-16.

### IF-DEC-11 — Testing technologies: JUnit, Spring Boot Test, Playwright

- **Status:** Confirmed.
- **Context:** PRD §11.2 fixes JUnit, Spring Boot Test, and Playwright; PRD §13.1 requires automated testing of critical workflows; NFR-MNT-004 requires automated tests using this selected stack.
- **Decision:** JUnit and Spring Boot Test cover unit and integration testing of services, constraints, and authorization; Playwright covers end-to-end browser workflows for the critical paths (report lifecycle, review, compliance, completion). Quantitative coverage targets are **Open** (NFR-MNT-006) and are not asserted here.
- **Consequences:** Test architecture and the mapping of tests to verification methods belong to the Test Plan (`SAD.md` §16); this record fixes only the technologies. The three-layer authorization model (`SAD.md` §8.2) is directly testable at each layer with this toolset.
- **Alternatives:** Other JVM test frameworks or Selenium instead of Playwright — would contradict PRD §11.2. Property-based testing libraries — permissible as additions but not established; additions require review.
- **Related requirements:** PRD §11.2, §13.1; NFR-MNT-004; NFR-MNT-006.

## 5. Approved Decisions (IF-DEC-12, IF-DEC-13, IF-DEC-15 … IF-DEC-18)

The entries below are **not** established by any source document. They were originally offered as **Proposed** because the architecture needs a position; at the Stage 1.6 planning review (2026-10-17) each was **approved with the boundaries recorded in its entry**. Approval makes each an architectural direction for the current baseline — it does not promote it to the requirements baseline, does not settle the open sub-items listed with it, and resolves none of the FRD open decisions (§7). Each is cross-referenced from `SAD.md` and/or `DDD.md` with the same ID.

### IF-DEC-12 — REST API style over JSON

- **Status:** **Approved** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record). Boundary: endpoint paths, request/response schemas, and error-response structure remain open for the API Specification, and API versioning is tracked separately under IF-DEC-14 (Open) — no endpoint contract is invented here.
- **Context:** SRS-IF-01 confirms only that the frontend and backend communicate through a defined API boundary; it explicitly states that "the protocol style, endpoint catalogue, and payload schemas are not established by the source documents and are deferred to the API Specification."
- **Decision:** Use a RESTful style over HTTPS with JSON bodies for the resource-oriented domain (internships, cohorts, reports, documents, notifications), leaving endpoint paths, payload schemas, and error shapes to the API Specification. Error responses shall convey the failure categories of FRD §7.1 (SRS-IF-04).
- **Consequences:** Aligns with the stateless Spring MVC stack and with SRS-IF-05 (state cannot be manipulated client-side because transitions are server-validated). Does **not** pre-decide versioning (IF-DEC-14, Open) or any concrete endpoint contract — none is invented here.
- **Alternatives:** GraphQL — rejected for MVP: adds a query-planning layer and a second schema surface with no requirement demanding flexible client-driven queries, and record-scope enforcement (SRS-FR-007) is simpler on resource endpoints. RPC-style endpoints — workable but weaker fit for the resource-oriented domain; also unestablished. gRPC — rejected: the only client is a browser (SRS §3.4, SRS-IF-18).
- **Related requirements:** SRS-IF-01; SRS-IF-03; SRS-IF-04; SRS-IF-05; SRS-FR-007.

### IF-DEC-13 — Persisted derived compliance evaluations

- **Status:** **Approved with boundaries** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record). Boundary: persisted per-requirement evaluations are approved as the common derived compliance source for dashboards, reports, exports, and completion checks, but OD-04 remains **Open** — the compliance status vocabulary, calculation rules, and detailed evaluation behavior are not finalized. Failed evaluations must never silently appear satisfied (NFR-REL-005), and the no-edit-by-any-role rule (PR-07) stands.
- **Context:** A single compliance result per internship must be consistent across dashboards, reports, CSV exports, and completion eligibility (SRS-FR-004), served from "the single evaluated compliance source (or a consistent materialization of it)" (NFR-PERF-004), with failed evaluations marked stale or unevaluated and never as satisfied (NFR-REL-005).
- **Decision:** Persist per-requirement evaluation rows (one per applicable requirement per internship — the `requirement_evaluations` table, `DDD.md` §8.1) as the single derived compliance source; dashboards, exports, and completion all read from it rather than recomputing independently.
- **Consequences:** Guarantees cross-surface consistency (NFR-DAT-006) and bounds read cost (NFR-PERF-004); introduces a staleness risk that is managed by IF-DEC-15 (recalculation in the triggering transaction) plus a stale flag on failure (NFR-REL-005). Evaluation **statuses and calculation rules are Open** under OD-04 — this decision fixes *where* results live, not *how* they are computed. The row set and vocabulary are therefore partially open in `DDD.md`.
- **Alternatives:** Recompute on every read — rejected: per-request joins across reports/documents/requirements on every dashboard load risks divergent results across surfaces and fails NFR-PERF-004's intent at any non-trivial data volume. Storing only a single aggregate flag — rejected: loses per-requirement detail that dashboards and completion checks need (FR-COMP-001, FR-DASH-006). Event-sourced compliance log — rejected: disproportionate at single-college scale (`SAD.md` P9).
- **Related requirements:** FR-COMP-007; FR-DASH-006; SRS-FR-004; NFR-PERF-004; NFR-DAT-006; NFR-REL-005; OD-04.

### IF-DEC-15 — Synchronous, in-transaction compliance recalculation

- **Status:** **Approved with boundaries** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record). Boundary: the intended approach is synchronous recalculation within the triggering transaction, but the precise failure-handling policy must be specified before implementation — the choice between transaction rollback and marking an evaluation stale/unevaluated is not made here and follows the failure conditions of FRD §7.3 / SRS §11. Consistency and data-integrity requirements (NFR-DAT-006, NFR-REL-001/005) are preserved.
- **Context:** Compliance-derived information must be consistent everywhere it is displayed (NFR-DAT-006), failed evaluations must never appear as satisfied (NFR-REL-005), and failed operations must leave data unchanged with no partial writes (NFR-REL-001).
- **Decision:** When an event that affects compliance occurs (report state change, document review, requirement change), the affected compliance evaluations are recalculated **synchronously within the same database transaction** as the triggering change. If recalculation fails, the transaction rolls back or the affected result is marked stale/unevaluated — never silently satisfied.
- **Consequences:** Strong consistency with no message ordering or replay concerns; adds latency to the triggering operation, which is acceptable at single-department scale (PRD §11.1) but must be re-examined if targets are later defined (NFR-PERF-002). Whether "roll back" or "mark stale" applies in each failure mode follows the error semantics of FRD §7.3 / SRS §11, not a choice made here.
- **Alternatives:** Asynchronous recalculation via queue — rejected for MVP: requires infrastructure no requirement demands (IF-DEC-17) and creates a consistency window that conflicts with NFR-DAT-006. Nightly batch recompute — rejected: dashboards and completion checks would show stale data between runs, violating the single-source consistency expectation at read time. Recompute-on-read — see IF-DEC-13.
- **Related requirements:** NFR-REL-001; NFR-REL-005; NFR-DAT-006; SRS-FR-004; FRD §7.3.

### IF-DEC-16 — Optimistic locking for workflow concurrency

- **Status:** **Approved** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record). Boundary: applies to concurrently modified workflow aggregates where appropriate, starting with weekly reports as described below; database-level unique and partial-unique constraints remain independent safeguards. No workflow functionality or database migrations are implemented under this approval.
- **Context:** Concurrent conflicting workflow actions (two faculty approvals racing, duplicate submissions) must yield exactly one effective transition with only one action recorded (NFR-DAT-008; FRD §7.2 case 22; SRS-ERR-007), and one report per internship-period must be enforced atomically (NFR-DAT-002; FR-REPORT-006).
- **Decision:** Add a version column to `weekly_reports` (and to other concurrently-written aggregates identified during design) and use JPA optimistic locking; a losing writer receives an invalid-transition/no-op outcome and writes nothing. Database-level unique and partial-unique constraints remain the final backstop for NFR-DAT-002 — locking is complementary, not substitutive.
- **Consequences:** No blocking or deadlocks under the expected low concurrency; losers must surface a meaningful error per FRD §7.1 (invalid-transition category, SRS-IF-04). Retry-on-conflict behavior for idempotent endpoints (NFR-REL-003) must be designed carefully to avoid double-recalculation (IF-DEC-15).
- **Alternatives:** Pessimistic row locks — rejected for now: access patterns are short transactions at single-cohort scale and would add contention without benefit (`DDD.md` §12); not prohibited, but nothing requires it. Database serializable isolation everywhere — rejected: broad performance impact and harder-to-diagnose failures for the same guarantee. Single-writer application instances — rejected: correctness must not depend on deployment topology.
- **Related requirements:** NFR-DAT-008; NFR-DAT-002; FR-REPORT-006; SRS-ERR-007; NFR-REL-003; FRD §7.2 (case 22).

### IF-DEC-17 — No message broker or background-job platform in the MVP

- **Status:** **Approved with boundaries** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record). Boundary: the MVP introduces no message broker, external job queue, or separate worker fleet without a demonstrated requirement, and no queue infrastructure is to be added; OD-09 remains **Open**, so this approval does not finalize email delivery guarantees, retry policies, deadline-reminder behavior, or notification scheduling requirements.
- **Context:** The system targets one department or cohort (PRD §11.1; SRS §3.4); the PRD non-goals exclude microservices (PRD §5.2), and no requirement demonstrates a need for brokered messaging. Notification delivery must be non-blocking (SRS-IF-14) but no delivery guarantee is established (OD-09, SRS-IF-17).
- **Decision:** The MVP uses no message broker, no external job queue, and no separate worker fleet. In-app notifications are rows in PostgreSQL; email dispatch is an in-process, post-commit, best-effort step with failures recorded (NFR-REL-004). Any periodic work uses Spring's built-in scheduling in the same deployable until a demonstrated need says otherwise.
- **Consequences:** Keeps the deployment footprint at two application deployables plus datastore, storage, and email service (`SAD.md` P9); in-process dispatch means email loss is possible if the process dies between commit and send — acceptable only while OD-09 leaves delivery guarantees open, and must be revisited if OD-09 introduces retries or scheduled reminders. No cross-instance coordination exists if the backend is ever scaled horizontally — a constraint to re-check when IF-DEC-10 selects hosting.
- **Alternatives:** Redis/RabbitMQ/Kafka — rejected: infrastructure and operational cost with no requirement to satisfy; contradicts P7's "no distributed infrastructure without a demonstrated requirement". External job schedulers or serverless cron — dependent on IF-DEC-10, still unselected; Spring scheduling suffices for MVP scale. Adopting a broker preemptively "for OD-09" — rejected: deciding OD-09's scope from this document would pre-empt the open decision.
- **Related requirements:** PRD §11.1, §5.2, §14.1; SRS §3.4; SRS-IF-14; NFR-REL-004; OD-09.

### IF-DEC-18 — Health endpoint and basic structured logging

- **Status:** **Approved** (Stage 1.6 planning review, 2026-10-17; originally Proposed by this record; the health endpoint was additionally live-verified during the Stage 1.6 review — `GET /api/health` returned the documented response and unauthenticated access to other paths was refused). Boundary: no uptime target, log-retention period, or mandatory monitoring vendor is invented. No source document mandates a specific observability tool; NFR-REL-008 forbids inventing uptime or recovery targets, and no log-retention duration is established (OD-11 applies to records; log policy belongs to the future Deployment and Operations Guide).
- **Context:** Operators and reviewers need to know whether the backend is running and what happened during a failure, without committing to an SLA or a specific log volume/retention that no requirement defines.
- **Decision:** Expose an unauthenticated liveness-style health endpoint (no sensitive detail) and write structured application logs for errors, authorization denials, and workflow transitions, excluding secrets and sensitive document content. No metrics platform, tracing system, or log aggregation vendor is selected; no uptime target is claimed.
- **Consequences:** Provides minimal operability for deployment and test environments; the health endpoint must not leak configuration or data (consistent with NFR-SEC-003's generic-failure posture and NFR-SEC-008). Log content policy and retention remain with the Security Requirements / Deployment and Operations Guide — nothing here commits a retention period.
- **Alternatives:** Full observability stack (metrics, distributed tracing, aggregation) — rejected for MVP: disproportionate for two deployables and would imply operational commitments NFR-REL-008 says do not exist. No health endpoint at all — rejected: deployment verification (IF-DEC-10, `SAD.md` §15) would have no lightweight signal.
- **Related requirements:** NFR-REL-008; NFR-SEC-003; NFR-SEC-008; PRD §12.

## 6. Open Items (IF-DEC-10, IF-DEC-14, and Provider Selections)

| Open item | Owning decision / OD | What must be decided | Deferred to |
|---|---|---|---|
| Backend hosting environment | IF-DEC-10 (Open) | Provider, topology, scaling, TLS termination, secret management | Deployment and Operations Guide |
| API versioning strategy | IF-DEC-14 (Open) | URL prefix vs. header versioning vs. no versioning for a single-frontend MVP | API Specification |
| Object storage vendor | IF-DEC-07 (Confirmed, vendor open) | Concrete provider preserving private-only access (SRS-IF-11) | Deployment and Operations Guide |
| Email transport/provider | IF-DEC-08 (Confirmed, transport open) | Service, transport, retry behavior, delivery guarantees | OD-09 → SRS / Deployment Guide |
| Database hosting | IF-DEC-05 (Confirmed, hosting open) | Managed service vs. self-hosted; backup approach; **no invented retention (OD-11) or availability targets (NFR-REL-008)** | Deployment and Operations Guide |
| Authentication mechanics | IF-DEC-04 (framework confirmed) | Credentials, password policy, session/token lifetime, lockout | OD-15 → Security Requirements |
| Compliance status vocabulary and calculation rules | IF-DEC-13 (persistence approved) | Statuses, weights, formulas | OD-04 → SRS + Database Design |
| Performance/uptime targets | none — no decision exists | Response-time, throughput, concurrency, availability commitments | NFR-PERF-002, NFR-REL-008 (targets open) |

**IF-DEC-14 — API versioning strategy (Open).** Context: SRS-IF-01 defers protocol details to the API Specification, and no source document establishes versioning. Decision: **not decided.** Options to evaluate when the API Specification is written: URL prefix versioning, media-type/header versioning, or no versioning given a single first-party frontend. Consequences: client error-handling and cache behavior vary by option; deferring is low-risk for a single-frontend MVP but must be settled before any second consumer exists. Related requirements: SRS-IF-01, SRS-IF-04.

No entry in this section resolves an FRD open decision; the FRD open decisions remain exactly as recorded in FRD §9 and are summarized in §7 below without alteration.

## 7. Interaction with FRD Open Decisions (OD-01 … OD-16)

Reproduced from FRD §9 (unchanged; this record resolves none of them). The final column notes which decisions above are *constrained by* or *waiting on* the open decision.

| OD | Topic | Effect on this record |
|---|---|---|
| OD-01 | Role and permission boundaries | Bounds IF-DEC-04: Spring Security enforces only the matrix as written; admin authority (report approval, completion verification) stays absent by default |
| OD-02 | Approved-report modification, reopening, rejection | May add transitions to FRD §5.2's model; IF-DEC-12/IF-DEC-16 implementations must accommodate whatever is decided |
| OD-03 | Requirement ownership and change propagation | Affects IF-DEC-13/IF-DEC-15: requirement edits may trigger re-evaluation |
| OD-04 | Compliance statuses and calculation rules | Open within IF-DEC-13: persistence is approved, vocabulary/formula not |
| OD-05 | Document review requirements and effects | May add compliance inputs to IF-DEC-13's evaluation rows; `DDD.md` flags interim constraints |
| OD-06 | Audit event set and viewing permissions | Bounds logging/audit scope referenced by IF-DEC-18; no viewing map invented |
| OD-07 | Exact MVP reports and CSV columns | Deferred to Reporting design; not addressed here |
| OD-08 | Completion verification authority and reversal | No architecture decision grants authority; admin default remains none |
| OD-09 | Notification triggers, timing, retries | Open within IF-DEC-08/IF-DEC-17: triggers and guarantees undecided |
| OD-10 | Deadline and late-submission policies | Not addressed here; no cadence or deadline value invented |
| OD-11 | Record and history retention | No retention period invented anywhere in this record (also see IF-DEC-18 log policy) |
| OD-12 | Internship field catalogue and lifecycle states | May alter `DDD.md` schema; interim constraints flagged there |
| OD-13 | Student–internship reassignment rules | Database Design scope; IF-DEC-16 concurrency may be affected |
| OD-14 | Student cohort membership moves | Database Design scope; IF-DEC-13 history handling may be affected |
| OD-15 | Authentication mechanics | Open within IF-DEC-04: framework confirmed, mechanics undecided |
| OD-16 | Document file-type and size policy values | Upload validation values deferred to Security Requirements; not invented here |

## 8. Assumptions Affecting Architecture (AS-01, AS-02, AS-03)

Reproduced unchanged from FRD §9.3. These remain **assumptions — not confirmed product requirements**. At the Stage 1.6 planning review (2026-10-17) AS-01, AS-02, and AS-03 were **accepted as working assumptions for the current architectural baseline**; they are listed here because the schema and service design in `DDD.md` depend on them (partial unique indexes and cardinality rules). If a reviewer later rejects one, the corresponding `DDD.md` constraints must be revisited.

| Assumption | Statement (FRD §9.3) | Requirement | Where it bites |
|---|---|---|---|
| AS-01 | A student has at most one active internship in the MVP. | FR-INT-002 | `DDD.md` partial unique index on `internships`; dashboard scoping |
| AS-02 | A student belongs to exactly one cohort at a time. | FR-COHORT-002 | `DDD.md` partial unique index on `cohort_memberships` |
| AS-03 | A cohort has one primary Faculty Coordinator, with support for additional assignments as a permitted extension. | FR-COHORT-003 | `DDD.md` `cohort_coordinator_assignments`; review permission scope (FRD §4) |

Related assumptions not repeated here (AS-04 single role per user, AS-05 admin-created accounts, AS-06 no correction-cycle cap) likewise remain FRD §9.3 items and are unchanged.

## 9. Alternatives Considered and Rejected

Consolidated view across §4–§5 (each entry's reasoning appears with its decision):

| Alternative | Where rejected | Reason |
|---|---|---|
| Microservices / serverless decomposition | IF-DEC-01 | PRD §5.2 non-goal; single-department scale (PRD §11.1; SRS §3.4) |
| Non-Spring backend frameworks | IF-DEC-03 | Contradicts PRD §11.2 baseline |
| Hand-rolled authorization filters | IF-DEC-04 | Risk against deny-by-default (NFR-SEC-002) |
| Non-PostgreSQL or non-relational primary store | IF-DEC-05 | Contradicts PRD §11.2; referential integrity (NFR-DAT-001) |
| Public/CDN file URLs, filesystem serving | IF-DEC-07 | NFR-SEC-004, SRS-IF-11 forbid public URLs |
| SMS/push notification channels | IF-DEC-08 | Post-MVP per SRS-FR-002 |
| GraphQL | IF-DEC-12 | No requirement needs client-driven queries; scope enforcement simpler on resources |
| Recompute compliance on every read | IF-DEC-13 | Fails NFR-PERF-004 intent; divergence risk (NFR-DAT-006) |
| Async/queued or batch compliance recalculation | IF-DEC-15 | Consistency window conflicts with NFR-DAT-006; needs broker (IF-DEC-17) |
| Pessimistic locking, serializable-everywhere | IF-DEC-16 | Contention/perf cost unjustified at current scale |
| Message broker / external job queue | IF-DEC-17 | No demonstrated requirement; adds distributed infrastructure (SAD P7) |
| Full observability stack | IF-DEC-18 | Disproportionate; risks implying SLAs NFR-REL-008 says don't exist |

## 10. Traceability

### 10.1 Decision → source requirement

| Decision | Requirement(s) cited | Resolves source? |
|---|---|---|
| IF-DEC-01 | NFR-MNT-001; NFR-MNT-002; PRD §11.2, §14.1 | Restates confirmed baseline |
| IF-DEC-02 | PRD §11.2, §12; SRS-IF-01 | Restates confirmed baseline |
| IF-DEC-03 | PRD §11.2; NFR-MNT-002; SRS §5.2 | Restates confirmed baseline |
| IF-DEC-04 | PRD §11.2; FR-AUTH-003; FR-AUTH-004; NFR-SEC-001; NFR-SEC-002; NFR-SEC-009 | Framework only; OD-15 remains |
| IF-DEC-05 | PRD §11.2; SRS-IF-06; SRS-IF-07; NFR-DAT-001; NFR-DAT-002 | Datastore only; hosting open |
| IF-DEC-06 | PRD §11.2; SRS-IF-06; NFR-REL-002; NFR-PERF-004 | Restates confirmed baseline |
| IF-DEC-07 | FR-DOC-007; NFR-SEC-004; SRS-IF-11; SRS-IF-12; SRS-FR-008 | Category confirmed; vendor open |
| IF-DEC-08 | SRS-IF-13; SRS-IF-14; SRS-IF-15; SRS-IF-17; SRS-FR-006; NFR-REL-004; NFR-REL-006 | Channels confirmed; OD-09 remains |
| IF-DEC-09 | PRD §11.2, §11.3; NFR-SEC-008; SRS-IF-16 | Restates confirmed baseline |
| IF-DEC-10 | PRD §11.2, §11.3; NFR-REL-008; NFR-SEC-008; SRS-IF-16 | **No** — explicitly open |
| IF-DEC-11 | PRD §11.2, §13.1; NFR-MNT-004; NFR-MNT-006 | Technologies confirmed; targets open |
| IF-DEC-12 | SRS-IF-01; SRS-IF-03; SRS-IF-04; SRS-IF-05; SRS-FR-007 | **No** — approved at Stage 1.6; still defers to API Specification |
| IF-DEC-13 | FR-COMP-007; FR-DASH-006; SRS-FR-004; NFR-PERF-004; NFR-DAT-006; NFR-REL-005 | **No** — approved at Stage 1.6 (bounded); OD-04 remains |
| IF-DEC-14 | SRS-IF-01; SRS-IF-04 | **No** — explicitly open |
| IF-DEC-15 | NFR-REL-001; NFR-REL-005; NFR-DAT-006; SRS-FR-004 | **No** — approved at Stage 1.6 (bounded) |
| IF-DEC-16 | NFR-DAT-008; NFR-DAT-002; FR-REPORT-006; SRS-ERR-007; NFR-REL-003 | **No** — approved at Stage 1.6 |
| IF-DEC-17 | PRD §11.1, §5.2, §14.1; SRS §3.4; SRS-IF-14; NFR-REL-004 | **No** — approved at Stage 1.6 (bounded); OD-09 remains |
| IF-DEC-18 | NFR-REL-008; NFR-SEC-003; NFR-SEC-008; PRD §12 | **No** — approved at Stage 1.6; no SLA/retention invented |

### 10.2 Requirement → decisions that consume it

| Requirement | Consumed by |
|---|---|
| PRD §11.2 (tech decisions) | IF-DEC-01 … IF-DEC-11 |
| SRS-IF-01 … SRS-IF-18 (interface) | IF-DEC-12, IF-DEC-14; constraints on IF-DEC-02/04/07/08 |
| SRS-FR-001, SRS-FR-007 (authorization/scope) | IF-DEC-04, IF-DEC-12 |
| SRS-FR-003 (state model) | Constraint on IF-DEC-12, IF-DEC-16 (no client-side bypass) |
| SRS-FR-004 (single compliance) | IF-DEC-13, IF-DEC-15 |
| NFR-SEC-001 … NFR-SEC-010 | IF-DEC-02, IF-DEC-04, IF-DEC-07, IF-DEC-18 |
| NFR-PERF-002 … NFR-PERF-004 | IF-DEC-13, IF-DEC-15 (targets open) |
| NFR-DAT-001 … NFR-DAT-009 | IF-DEC-05, IF-DEC-13, IF-DEC-16 |
| NFR-REL-001 … NFR-REL-008 | IF-DEC-05, IF-DEC-15, IF-DEC-17, IF-DEC-18 |
| NFR-MNT-001 … NFR-MNT-006 | IF-DEC-01, IF-DEC-11 |
| OD-01 … OD-16 | §7 — all remain open |

### 10.3 Cross-document consistency

| Check | Expected | Where verified |
|---|---|---|
| Decision IDs match `SAD.md` §10, §12, §13, §17.3 | IF-DEC-07, 12, 13, 14, 15, 16, 17, 18 | §11 (below) |
| Decision IDs match `DDD.md` §8.1, §12 | IF-DEC-13, IF-DEC-16 | §11 (below) |
| All cited requirement IDs resolve in `FRD.md`/`SRS.md` | 0 unresolved | §11 (below) |
| No invented retention/SLA/cadence/endpoint values | 0 occurrences | §11 (below) |

## 11. Review Checklist and Approval Status

**Overall status: Draft — Pending Review. This record approves nothing by itself.** Approvals made at human review (currently the Stage 1.6 disposition in §11.3) are recorded from the reviewer, not granted by this document; document-level approval and the two Open entries remain outstanding.

### 11.1 Automated consistency checks (executed 2026-10-15; re-executed 2026-10-17 at the Stage 1.6 review)

| # | Check | Result |
|---|---|---|
| 1 | Every `IF-DEC-nn` referenced in `SAD.md` and `DDD.md` exists in this register with the same meaning | **Pass** — IF-DEC-07, 12, 13, 14 (API versioning), 15, 16, 17, 18 consistent across all three documents |
| 2 | Every requirement ID cited in this record (`FR-*`, `SRS-*`, `NFR-*`, `PR-*`, `OD-*`, `AS-*`) resolves in `docs/requirements/` | **Pass** (programmatically checked; see the note below) |
| 3 | All 16 FRD open decisions OD-01 … OD-16 are represented in §7 and none is marked resolved | **Pass** |
| 4 | AS-01/AS-02/AS-03 statements match FRD §9.3 verbatim in meaning and citation | **Pass** |
| 5 | No invented retention period, SLA/uptime target, reporting cadence, compliance value, or endpoint contract appears | **Pass** — open items named in §6 instead |
| 6 | Role boundaries preserved: no admin report-approval or completion authority; faculty-only weekly-report approval; five states with FRD §5.2 transitions only | **Pass** (§2 restates; no decision alters them) |
| 7 | Register counts match §3 header counts (10 Confirmed, 6 Approved, 2 Open) | **Pass** |

Programmatic check #2 was run over all `FR-`, `SRS-`, `NFR-`, `PR-`, `OD-`, `AS-`, and `IF-DEC-` identifiers in this file against the identifier sets in `docs/requirements/FRD.md`, `docs/requirements/SRS.md`, and `docs/requirements/PRD.md`; unresolved identifiers: **none**. (If this file is edited later, re-run the same check.)

### 11.2 Human review actions required

| # | Action for reviewer | Blocking? |
|---|---|---|
| 1 | ~~Approve or amend the six Proposed entries (IF-DEC-12, IF-DEC-13, IF-DEC-15, IF-DEC-16, IF-DEC-17, IF-DEC-18)~~ **Completed at the Stage 1.6 planning review (2026-10-17):** all six approved with the boundaries recorded in §5 | No — resolved |
| 2 | Direct a shortlist or criteria for backend hosting (IF-DEC-10) | Yes, before deployment planning |
| 3 | Confirm the embedded open sub-items (storage vendor, email transport, database hosting) are correctly deferred | Yes |
| 4 | Confirm §7's interaction notes do not overstate or resolve any OD-01 … OD-16 | **Yes** |
| 5 | Confirm the AS-01/AS-02/AS-03 dependency note in §8 | Yes — accepted at the Stage 1.6 review (2026-10-17); further confirmation optional |
| 6 | Decide whether a versioning recommendation should be prepared for the API Specification (IF-DEC-14) | No — may stay open |

### 11.3 Approval

| Role | Name | Decision | Date |
|---|---|---|---|
| Project reviewer / stakeholder | _pending_ | _pending_ | _pending_ |
| Development lead | _pending_ | _pending_ | _pending_ |

**Stage 1.6 planning review disposition (2026-10-17):** the six formerly Proposed decisions **IF-DEC-12, IF-DEC-13, IF-DEC-15, IF-DEC-16, IF-DEC-17, and IF-DEC-18 were approved** with the boundaries recorded in §5 (review action 1 resolved), and the AS-01/AS-02/AS-03 dependency note in §8 was accepted (review action 5). Review actions 2, 3, 4, and 6 remain pending, as do the two Open entries (IF-DEC-10, IF-DEC-14) and all FRD §9 open decisions. This partial disposition does **not** constitute document-level approval; the rows above stay _pending_.

*End of document — status: Draft, pending human review.*
