# InternFlow Software Requirements Specification (SRS)

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | InternFlow Software Requirements Specification (SRS) |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-SRS-001 |
| Version | 1.0 |
| Status | Draft — Pending Review (not approved) |
| Last Updated | 2026-10-14 |
| Relationship to PRD | Derived from and traceable to InternFlow PRD v1.0 (`docs/requirements/PRD.md`, baseline commit `9f91303`). The PRD remains the authoritative product-scope baseline and this document does not modify it. |
| Relationship to FRD | Derived from and traceable to InternFlow FRD v1.0 (`docs/requirements/FRD.md`, commit `c8b8754`). The FRD remains the authoritative source for detailed functional behavior, role permissions, the report state model, validation and edge cases, and functional acceptance criteria. This SRS references the FRD rather than restating it. |
| Purpose | Specify InternFlow's system-level software requirements: system context and boundaries, external interfaces, cross-cutting functional requirements, non-functional (quality) requirements, data and workflow constraints, authorization expectations, error handling, verification approach, traceability, and the carried-forward assumptions, constraints, dependencies, and open decisions. |
| Intended Audience | Project reviewer/stakeholder; the development team implementing the MVP; authors of the System Architecture Document, Database Design Document, API Specification, Security Requirements, UI/UX Specification, Test Plan, and Deployment and Operations Guide. |

**Review note:** This document is a draft pending review and must not be treated as approved. It does not resolve any open decision, assumption, or proposed rule. Identifiers of the form **OD-xx**, **AS-xx**, and **PR-xx** retain the meanings and status recorded in the FRD (FRD §9). Identifier references of the form **FR-&lt;MODULE&gt;-NNN** resolve to FRD requirements (FRD §6).

## Table of Contents

- [1. Document Control](#1-document-control)
- [2. Introduction](#2-introduction)
  - [2.1 Purpose](#21-purpose)
  - [2.2 Scope](#22-scope)
  - [2.3 Intended Audience](#23-intended-audience)
  - [2.4 Definitions, Acronyms, and Abbreviations](#24-definitions-acronyms-and-abbreviations)
  - [2.5 References](#25-references)
  - [2.6 Requirement and Status Conventions](#26-requirement-and-status-conventions)
- [3. Overall System Description](#3-overall-system-description)
  - [3.1 Product Perspective](#31-product-perspective)
  - [3.2 Product Functions (MVP Modules)](#32-product-functions-mvp-modules)
  - [3.3 User Classes and Characteristics](#33-user-classes-and-characteristics)
  - [3.4 Operating Environment](#34-operating-environment)
  - [3.5 Design and Implementation Constraints](#35-design-and-implementation-constraints)
  - [3.6 Assumptions and Dependencies](#36-assumptions-and-dependencies)
- [4. System Context and Boundaries](#4-system-context-and-boundaries)
  - [4.1 System Context](#41-system-context)
  - [4.2 In-Scope Responsibilities](#42-in-scope-responsibilities)
  - [4.3 Out-of-Scope and Deferred Functionality](#43-out-of-scope-and-deferred-functionality)
  - [4.4 External Systems and Responsibilities](#44-external-systems-and-responsibilities)
- [5. External Interface Requirements](#5-external-interface-requirements)
  - [5.1 User Interfaces](#51-user-interfaces)
  - [5.2 Software Interfaces](#52-software-interfaces)
  - [5.3 Communication Interfaces](#53-communication-interfaces)
  - [5.4 Hardware Interfaces](#54-hardware-interfaces)
- [6. Functional Requirements Overview](#6-functional-requirements-overview)
  - [6.1 Relationship to the FRD](#61-relationship-to-the-frd)
  - [6.2 Module-Level Functional Summary](#62-module-level-functional-summary)
  - [6.3 System-Level Functional Requirements](#63-system-level-functional-requirements)
- [7. Non-Functional Requirements](#7-non-functional-requirements)
  - [7.1 Security](#71-security)
  - [7.2 Performance](#72-performance)
  - [7.3 Reliability and Availability](#73-reliability-and-availability)
  - [7.4 Usability and Accessibility](#74-usability-and-accessibility)
  - [7.5 Maintainability](#75-maintainability)
  - [7.6 Compatibility](#76-compatibility)
  - [7.7 Data Integrity](#77-data-integrity)
  - [7.8 Auditability and Traceability](#78-auditability-and-traceability)
  - [7.9 Privacy and Data Protection](#79-privacy-and-data-protection)
- [8. Data and Information Requirements](#8-data-and-information-requirements)
  - [8.1 Conceptual Data Model](#81-conceptual-data-model)
  - [8.2 Data Constraints and Uniqueness Rules](#82-data-constraints-and-uniqueness-rules)
  - [8.3 Data Retention (Deferred)](#83-data-retention-deferred)
- [9. Business Rules and Workflow Constraints](#9-business-rules-and-workflow-constraints)
  - [9.1 Report Lifecycle](#91-report-lifecycle)
  - [9.2 Compliance Rules](#92-compliance-rules)
  - [9.3 Completion Rules](#93-completion-rules)
  - [9.4 Cross-Module Business Rules](#94-cross-module-business-rules)
- [10. Authorization and Access-Control Requirements](#10-authorization-and-access-control-requirements)
  - [10.1 Enforcement Model](#101-enforcement-model)
  - [10.2 Role and Scope Model](#102-role-and-scope-model)
  - [10.3 Pending Permission Decisions](#103-pending-permission-decisions)
- [11. Error Handling and Exceptional Conditions](#11-error-handling-and-exceptional-conditions)
  - [11.1 Error Taxonomy](#111-error-taxonomy)
  - [11.2 Exceptional Condition Requirements](#112-exceptional-condition-requirements)
  - [11.3 Consistency and Non-Disclosure](#113-consistency-and-non-disclosure)
- [12. Verification and Acceptance Approach](#12-verification-and-acceptance-approach)
  - [12.1 Verification Methods](#121-verification-methods)
  - [12.2 Verification Mapping](#122-verification-mapping)
  - [12.3 Evidence and Acceptance](#123-evidence-and-acceptance)
- [13. Requirements Traceability](#13-requirements-traceability)
  - [13.1 Traceability Model](#131-traceability-model)
  - [13.2 PRD-to-SRS Traceability Matrix](#132-prd-to-srs-traceability-matrix)
  - [13.3 Non-Functional Traceability](#133-non-functional-traceability)
  - [13.4 Coverage and Validation Checks](#134-coverage-and-validation-checks)
- [14. Assumptions, Dependencies, Constraints, and Open Decisions](#14-assumptions-dependencies-constraints-and-open-decisions)
  - [14.1 Assumptions](#141-assumptions)
  - [14.2 Dependencies](#142-dependencies)
  - [14.3 Constraints](#143-constraints)
  - [14.4 Open Decisions](#144-open-decisions)
  - [14.5 Items Requiring Human Approval](#145-items-requiring-human-approval)
- [15. Appendices](#15-appendices)
  - [15.1 Appendix A: FRD Requirement Inventory](#151-appendix-a-frd-requirement-inventory)
  - [15.2 Appendix B: Document Review Checklist](#152-appendix-b-document-review-checklist)
  - [15.3 Appendix C: Change Control and Version History](#153-appendix-c-change-control-and-version-history)

## 2. Introduction

### 2.1 Purpose

This Software Requirements Specification (SRS) formalizes InternFlow's system-level requirements. It specifies what the software must provide at the system boundary and which quality attributes it must satisfy, so that architecture, database design, API design, implementation, and testing can proceed against an agreed, verifiable baseline.

The document:

- Defines the system context, boundaries, and responsibilities (SRS §4).
- Describes the external interfaces of the system (SRS §5).
- Provides a functional requirements overview that is traceable to the FRD without duplicating it (SRS §6).
- States explicit non-functional (quality) requirements and separates confirmed requirements from proposed targets and open decisions (SRS §7).
- Records data, workflow, authorization, and error-handling constraints at the requirements level (SRS §8, SRS §9, SRS §10, SRS §11).
- Defines the verification and acceptance approach (SRS §12) and the traceability chain from the PRD through the FRD to this SRS (SRS §13).
- Carries forward the assumptions, constraints, dependencies, and open decisions that remain unresolved (SRS §14).

The section structure follows a conventional SRS organization similar to that associated with IEEE 830 and ISO/IEC/IEEE 29148. **This document does not claim formal conformance to any standard**; it has not been audited against one, and no compliance statement is intended.

### 2.2 Scope

This SRS covers the InternFlow MVP for the established initial deployment target of one college department or internship cohort (PRD §11.1). It covers the 13 essential MVP modules defined in PRD §7.1 and detailed in FRD §6.1–§6.13:

1. Authentication and Role-Based Access
2. User Management
3. Internship Management
4. Cohort Management
5. Document Management
6. Weekly Reports and Work Logs
7. Faculty Review Workflow
8. Compliance Engine
9. Role-Specific Dashboards
10. Notifications
11. Completion Verification
12. Audit History
13. Reports and CSV Exports

In addition, this SRS covers system-level and non-functional concerns that the FRD explicitly defers to it: measurable quality targets, external interface expectations, cross-cutting data and authorization constraints, and the verification approach (FRD §2.3; PRD §12; PRD §18.3).

**Explicitly outside this SRS:**

- Detailed functional behavior and functional acceptance criteria, which remain authoritative in the FRD (FRD §6). This SRS references FRD identifiers and sections rather than restating them.
- Post-MVP functionality listed in PRD §7.2 and §16 (Company Mentor evaluations, attendance tracking, advanced analytics and PDF reports, additional notification channels, multi-department or multi-college support, and external college-system integrations).
- Concrete database schema, API endpoint and payload definitions, UI specifications, security implementation mechanics, and deployment configuration. These belong to the downstream Stage 1 deliverables and later stages named in SRS §13.2.
- Application source code of any kind. This substage is documentation only.

### 2.3 Intended Audience

| Audience | Expected use of this SRS |
|---|---|
| Project reviewer/stakeholder | Confirm scope, boundaries, quality expectations, and the carried-forward open decisions. |
| System architects and database designers | Derive the architecture and data model consistent with SRS §4, SRS §8, SRS §9, and SRS §10. |
| Backend and frontend developers | Implement against the FRD (functional detail) with this SRS's system-level, non-functional, and interface constraints. |
| API and security authors | Use SRS §5, SRS §7.1, and SRS §10 to specify interfaces and security requirements. |
| Test authors | Use SRS §12 and SRS §13 to derive verification methods and the future Test Plan. |

### 2.4 Definitions, Acronyms, and Abbreviations

Terms defined in PRD §17 (Cohort, Work Log, Weekly Report, Compliance, Faculty Coordinator, Completion Verification, Audit History, Correction Request, RBAC, MVP) and in FRD §2.5 (Report, Report state, Review action, Reporting period, Requirement, Mandatory requirement, Compliance status, Assigned cohort, Assigned student, Authorized actor, Operation, CSV, Account activation) carry their existing meaning in this SRS.

Additional terms and abbreviations used in this SRS:

| Term or abbreviation | Meaning |
|---|---|
| SRS | Software Requirements Specification: this document. |
| System | The InternFlow application as a whole: frontend, backend, persistence, and its use of the external services in SRS §4.4. |
| System boundary | The dividing line between what InternFlow is responsible for and what external services are responsible for (SRS §4). |
| External system | A service or component outside the system boundary that InternFlow depends on (for example, PostgreSQL, object storage, an email delivery service). |
| Non-functional requirement (NFR) | A quality requirement describing how well the system must perform a function (security, performance, reliability, usability, maintainability, compatibility, data integrity, auditability, privacy). |
| Verification method (VM) | A defined way of demonstrating that a requirement is satisfied (SRS §12.1). |
| Confirmed | Directly established by the PRD or FRD (with the source cited). |
| Derived | Follows necessarily from an established decision or principle but is not restated verbatim in the source; flagged for reviewer confirmation. |
| Proposed | A requirement this SRS proposes because the source material requires some testable behavior but does not settle it; requires explicit review before it is treated as confirmed. |
| Open decision (OD-xx) | A genuinely unresolved matter recorded in the FRD (FRD §9) and carried forward here; not resolved by this SRS. |
| Assumption (AS-xx) | A planning assumption from the FRD (FRD §9.3) that keeps requirements testable but is not an approved decision. |
| Constraint | A condition that limits or bounds the design or implementation. |
| API | Application Programming Interface: the defined boundary through which the frontend and backend communicate. |
| CI | Continuous Integration (referenced only as a potential automation practice; not established as an MVP requirement). |
| DB | Database. |
| JPA | Jakarta Persistence API, used via Spring Data JPA. |
| ORM | Object-Relational Mapping (Hibernate). |
| RBAC | Role-Based Access Control. |
| TLS | Transport Layer Security. |
| UI / UX | User Interface / User Experience. |
| UAT | User Acceptance Testing. |

### 2.5 References

| Reference | Description and location |
|---|---|
| PRD | InternFlow Product Requirements Document v1.0 — `docs/requirements/PRD.md` (baseline commit `9f91303`). Authoritative for product vision, goals, scope, personas, module list, established decisions, non-functional expectations, and open decisions. |
| FRD | InternFlow Functional Requirements Document v1.0 — `docs/requirements/FRD.md` (commit `c8b8754`). Authoritative for detailed functional behavior, role permissions, report state transitions, validation and edge cases, and functional acceptance criteria. |
| README | Project introduction — `README.md`. |
| Stage 1 planning roadmap | The 12-week development roadmap and the Stage 1 substage sequence, as recorded in the project planning materials and reflected in the PRD timeline (PRD §11.1, §14.1). |

Reference convention: "PRD §x.y" and "FRD §x.y" denote sections of those documents. Within PRD §7.1, "PRD §7.1.N" denotes the Nth module listed under PRD §7.1 (for example, §7.1.11 = Completion Verification), per the FRD convention (FRD §2.3). "SRS §x.y" denotes a section of this document.

### 2.6 Requirement and Status Conventions

Every requirement and rule in this SRS carries one of the following status labels, consistent with the FRD (FRD §2.4).

| Label | Meaning |
|---|---|
| **Confirmed** | Directly established by the PRD or FRD, with the source cited. |
| **Derived** | Follows necessarily from an established decision or principle; flagged for reviewer confirmation and does not conflict with the sources. |
| **Proposed** | Proposed by this SRS because *some* testable behavior is required but the source does not settle it; requires review before it is treated as confirmed. |
| **Open decision (OD-xx)** | Genuinely unresolved; recorded in FRD §9 and carried forward here without a fabricated resolution. |
| **Assumption (AS-xx)** | A planning assumption from FRD §9.3; not an approved decision. |
| **Constraint** | A condition that limits the design or implementation (PRD §14.1; SRS §14.3). |

Identifier scheme for this SRS. Each family is used only for its stated purpose; none redefines an FRD identifier.

- **NFR-&lt;CATEGORY&gt;-NNN** — non-functional requirement, where CATEGORY is one of SEC, PERF, REL, UX, MNT, CMP, DAT, AUD, PRV (SRS §7).
- **SRS-FR-NNN** — system-level functional requirement spanning modules (SRS §6.3). Detailed functional behavior remains in the FRD.
- **SRS-IF-NN** — external interface requirement (SRS §5.2, §5.3, §5.4).
- **SRS-UI-NN** — user-interface requirement (SRS §5.1).
- **SRS-DR-NNN** — data and information requirement (SRS §8).
- **SRS-WF-NNN** — business rule or workflow constraint (SRS §9).
- **SRS-AUTH-NNN** — authorization and access-control requirement (SRS §10).
- **SRS-ERR-NNN** — error-handling and exceptional-condition requirement (SRS §11).
- **SRS-VER-NNN** — verification and acceptance requirement (SRS §12.3).
- **VM-NN** — verification method (SRS §12.1).
- **SRS §x.y** — internal cross-reference to this document.
- **FR-&lt;MODULE&gt;-NNN**, **OD-xx**, **AS-xx**, **PR-xx** — identifiers owned by the FRD; cited, never redefined.

A requirement's status label is part of the requirement. A **Proposed** requirement or an **Open decision** is not an approved commitment and must not be implemented as though confirmed.

## 3. Overall System Description

### 3.1 Product Perspective

InternFlow is a new, self-contained, workflow-driven internship operations and compliance platform. It is not a replacement for, or an extension of, an existing InternFlow system, and it is not part of a larger program of record. It replaces scattered spreadsheets, messages, emails, and manually maintained documents with a centralized, traceable workflow (PRD §2, §3, §4).

The system is a modular monolith: a single deployable backend with clearly separated modules, a separate frontend, a relational database, and private object storage (PRD §11.2, §14.1). Microservices are explicitly out of scope (PRD §5.2, §14.1).

The system's central purpose is to support the internship lifecycle: cohort configuration, internship registration, document submission, faculty review, weekly reporting with correction and resubmission, compliance tracking, and authorized completion verification, with auditability throughout (PRD §8, §9).

### 3.2 Product Functions (MVP Modules)

The system provides the 13 essential MVP modules. Detailed functional behavior is authoritative in the FRD; the table below maps each module to its FRD section and requirement range.

| # | Module | FRD section | FRD requirements | System-level purpose |
|---|---|---|---|---|
| 1 | Authentication and Role-Based Access | FRD §6.1 | FR-AUTH-001 … FR-AUTH-007 | Authenticate users and enforce role- and scope-based authorization on every protected operation. |
| 2 | User Management | FRD §6.2 | FR-USER-001 … FR-USER-008 | Create and administer user accounts, roles, and account status; separate administration from internship-specific scope. |
| 3 | Internship Management | FRD §6.3 | FR-INT-001 … FR-INT-008 | Register and maintain internship records and their relationships to students, cohorts, and requirements. |
| 4 | Cohort Management | FRD §6.4 | FR-COHORT-001 … FR-COHORT-007 | Configure cohorts, membership, faculty assignment, deadlines, and requirement sets. |
| 5 | Document Management | FRD §6.5 | FR-DOC-001 … FR-DOC-008 | Support required-document definition, submission, private storage, review, and access control. |
| 6 | Weekly Reports and Work Logs | FRD §6.6 | FR-REPORT-001 … FR-REPORT-010 | Support report creation, submission, correction, resubmission, approval, and state-based compliance effects. |
| 7 | Faculty Review Workflow | FRD §6.7 | FR-REVIEW-001 … FR-REVIEW-010 | Provide scoped review queues, correction requests, approvals, and review history. |
| 8 | Compliance Engine | FRD §6.8 | FR-COMP-001 … FR-COMP-010 | Resolve applicable requirements and compute compliance status with missing-requirement detail. |
| 9 | Role-Specific Dashboards | FRD §6.9 | FR-DASH-001 … FR-DASH-007 | Present role-appropriate status, pending actions, and scoped views. |
| 10 | Notifications | FRD §6.10 | FR-NOTIF-001 … FR-NOTIF-007 | Generate and deliver in-app and email notifications for workflow events, non-blocking. |
| 11 | Completion Verification | FRD §6.11 | FR-COMPLETE-001 … FR-COMPLETE-008 | Verify completion only when mandatory requirements are satisfied and by an authorized actor. |
| 12 | Audit History | FRD §6.12 | FR-AUDIT-001 … FR-AUDIT-006 | Record, protect, and expose important actions and decisions for traceability. |
| 13 | Reports and CSV Exports | FRD §6.13 | FR-EXPORT-001 … FR-EXPORT-008 | Provide basic operational reports and permission-respecting CSV exports. |

The first major implementation milestone is the end-to-end flow: **Student submits a report → Faculty requests corrections → Student resubmits → Faculty approves → Compliance status updates** (PRD §9; FRD §5).

### 3.3 User Classes and Characteristics

Three user classes participate in the MVP; Company Mentor is post-MVP and has no MVP permissions (PRD §6.4; FRD §3). Each user holds a single role in the MVP (AS-04). Access is further scoped by cohort assignment (FR-USER-007).

| User class | Characteristics | Primary capabilities (summary) | FRD reference |
|---|---|---|---|
| Student | Primary participant; acts only on own records. | Register internship; submit documents; create, edit, submit, and resubmit own reports; view own compliance, deadlines, notifications, and completion status. | FRD §3.1, §6.3, §6.5, §6.6 |
| Faculty Coordinator | Academic reviewer with cohort-scoped authority; sole weekly-report approver. | Review assigned students' documents and reports; request corrections; approve reports; monitor compliance; verify completion when eligible. | FRD §3.2, §4.2.7, §6.11 |
| College Administrator | Deployment-wide administrative role. | Manage users, roles, and account status; manage cohorts, membership, faculty assignment, deadlines, and requirements; access administrative reporting and authorized audit information. | FRD §3.3, §4.2.2, §4.2.4 |
| Company Mentor | Optional post-MVP; not an MVP participant. | None in the MVP. | PRD §6.4; FRD §3 |

Administrative status alone never grants review, approval, or completion-verification authority (PR-05; FRD §4.1, §4.4). Faculty Coordinators approve weekly reports (PRD §11.1).

### 3.4 Operating Environment

The system operates as a web application accessed through a browser, with the frontend and backend deployed to their selected hosting environments and a managed PostgreSQL database. The approved technology direction is (PRD §11.2):

| Layer | Established technology |
|---|---|
| Frontend | Next.js, React, TypeScript, Tailwind CSS |
| Backend | Java, Spring Boot, Spring Security |
| Persistence | PostgreSQL, Spring Data JPA, Hibernate |
| Document storage | Private object storage (provider to be determined) |
| Notifications | Email delivery service and in-application notifications |
| Testing | JUnit, Spring Boot Test, Playwright |
| Deployment | Vercel for the frontend; a suitable backend host; a suitable managed PostgreSQL deployment (specific providers to be selected) |

The initial operating scope is one college department or internship cohort (PRD §11.1). The provider selections, hosting configuration, and infrastructure details are not yet established and remain open (SRS §14.4; PRD §18).

### 3.5 Design and Implementation Constraints

The following constraints bound the design and implementation (PRD §14.1; SRS §14.3). They are stated here as constraints, not as new decisions.

- **Architecture:** Modular monolith; no microservices (PRD §5.2, §14.1).
- **Technology stack:** The stack in SRS §3.4 is established and is not to be silently changed (PRD §11.2).
- **Timeline:** Planned development duration of 12 weeks (PRD §14.1).
- **Deployment scope:** One college department or internship cohort; no multi-college infrastructure prematurely (PRD §11.1).
- **Feature priority:** Work logs take priority over attendance; attendance and Company Mentor functionality are optional for the MVP (PRD §11.1, §14.1).
- **Release focus:** The first major workflow must be completed and verified before significant effort on secondary modules (PRD §9.2, §15.1).
- **Scope preservation:** All 13 essential MVP modules remain in scope; prioritization affects order, not inclusion (PRD §7.3).
- **No invented policies:** Where the source documents leave a policy open, the system must not commit to a value; such matters remain open decisions (SRS §14.4).

### 3.6 Assumptions and Dependencies

**Assumptions.** The FRD planning assumptions AS-01 through AS-06 apply and are carried forward for review (SRS §14.1). They concern a single active internship per student (AS-01), single-cohort membership (AS-02), a single primary coordinator per cohort (AS-03), a single role per user (AS-04), administrator-created accounts with no self-registration (AS-05), and no cap on correction cycles (AS-06). Additional product-level planning assumptions are recorded in PRD §14.4.

**Dependencies.** InternFlow depends on external systems and services that must be available and configured during implementation: a PostgreSQL database; private object storage with access controls; a configured email delivery service; in-application notification functionality; hosting for the frontend and backend; and the selected test frameworks and environments (PRD §14.2). The availability and configuration of these dependencies must be verified during implementation. Dependencies are summarized in SRS §14.2.

## 4. System Context and Boundaries

### 4.1 System Context

InternFlow sits between its human actors and a small set of external services. Human actors interact through the web frontend; the backend enforces business rules, authorization, workflow transitions, compliance requirements, and persistence (PRD §10, §11.2).

```
                 +------------------------------+
   Student  ---->|                              |----> PostgreSQL (structured data)
 Faculty   ---->|          InternFlow          |----> Private object storage (documents)
 Admin     ---->|   (frontend + backend)       |----> Email delivery service (notifications)
                 |                              |
                 |  in-app notifications -------+----> recipients inside the system
                 +------------------------------+
```

The diagram is illustrative of responsibilities, not of deployment topology or vendor selection.

### 4.2 In-Scope Responsibilities

The system is responsible for:

- Authenticating users and enforcing role- and scope-based authorization on every protected operation, server-side (FR-AUTH-003, FR-AUTH-004).
- Managing user accounts, roles, and account status (FR-USER-001 … FR-USER-008).
- Registering and maintaining internship records and their relationships (FR-INT-001 … FR-INT-008).
- Configuring cohorts, membership, faculty assignment, deadlines, and requirement sets (FR-COHORT-001 … FR-COHORT-007).
- Accepting, privately storing, reviewing, and access-controlling required documents (FR-DOC-001 … FR-DOC-008).
- Supporting the weekly report lifecycle: creation, submission, correction, resubmission, and approval, with the report state model (FR-REPORT-001 … FR-REPORT-010; FRD §5).
- Providing the faculty review workflow and its history (FR-REVIEW-001 … FR-REVIEW-010).
- Computing compliance status and missing-requirement detail (FR-COMP-001 … FR-COMP-010).
- Presenting role-specific dashboards (FR-DASH-001 … FR-DASH-007).
- Generating and delivering in-app and email notifications for workflow events (FR-NOTIF-001 … FR-NOTIF-007).
- Verifying internship completion only when mandatory requirements are satisfied and by an authorized actor (FR-COMPLETE-001 … FR-COMPLETE-008).
- Recording, protecting, and exposing audit history (FR-AUDIT-001 … FR-AUDIT-006).
- Providing basic operational reports and permission-respecting CSV exports (FR-EXPORT-001 … FR-EXPORT-008).

### 4.3 Out-of-Scope and Deferred Functionality

The following are **not** part of the MVP and are excluded from this SRS, consistent with PRD §7.2 and §16:

- Company Mentor evaluations and any Company Mentor permissions or workflows.
- Attendance tracking (work logs take priority).
- Advanced analytics and PDF reports.
- Notification channels beyond email and in-app.
- Multi-department or multi-college support.
- External college-system integrations (for example, LMS or SIS integrations).

The system must not expose interfaces or workflows for these items in the MVP (SRS-FR-002). Database schema, API endpoints and payloads, UI specifications, security mechanics, and deployment configuration are deferred to later deliverables and are not specified here (SRS §13.2).

### 4.4 External Systems and Responsibilities

| External system | Responsibility | InternFlow responsibility | Status / reference |
|---|---|---|---|
| PostgreSQL database | Persist structured application data and enforce relational constraints; provide transaction semantics. | Define the logical data model and constraints; issue correct transactions; handle persistence failures. | Technology confirmed (PRD §11.2); schema deferred (OD-12, OD-13, OD-14, OD-03) |
| Private object storage | Store uploaded document content privately. | Authorization checks before upload/retrieval; no public URLs; handle storage failures as technical failures. | Mechanism confirmed (FR-DOC-007, PRD §11.2); provider to be determined |
| Email delivery service | Deliver notification emails. | Compose and send notifications; treat delivery failures as non-blocking and record them. | Dependency confirmed (PRD §14.2); provider and retry policy open (OD-09) |
| In-application notification store | Hold per-user notifications and read status. | Generate one notification per event per recipient; keep it private to the recipient. | Confirmed (FR-NOTIF-004, FR-NOTIF-007) |
| Frontend hosting (Vercel) | Serve the frontend application. | Build and deploy the frontend; communicate with the backend API. | Confirmed direction (PRD §11.2); configuration open |
| Backend hosting platform | Run the backend application. | Build and deploy the backend; secure configuration and secrets. | To be selected (PRD §11.2); open |
| Managed PostgreSQL deployment | Host the database. | Provision and connect securely. | To be selected (PRD §11.2); open |

No specific email providers, object-storage vendors, API endpoints, or payload schemas are established by the source documents and none are invented here.

## 5. External Interface Requirements

This section describes the expected high-level interfaces between InternFlow and the outside world. It states expectations and constraints; it does not define endpoints, payload schemas, or vendor choices, which belong to the API Specification and deployment documentation.

### 5.1 User Interfaces

- **SRS-UI-01 (Confirmed):** The system shall provide a web-based frontend through which all three roles interact; no separate desktop client is required (PRD §11.2).
- **SRS-UI-02 (Confirmed):** The system shall present role-appropriate landing surfaces and navigation: an authenticated user is directed to the surface of their assigned role, and role-restricted surfaces and operations are inaccessible to other roles (FR-AUTH-001, FR-AUTH-004).
- **SRS-UI-03 (Confirmed):** The frontend shall expose the workflows of the 13 MVP modules for the roles permitted in FRD §4, and shall not expose post-MVP functionality (SRS-FR-002).
- **SRS-UI-04 (Confirmed):** The interface shall present workflow states and outcomes clearly, including report states, correction feedback, review outcomes, compliance status with missing requirements, and pending actions (FR-DASH-001, FR-DASH-002, FR-REVIEW-006, FR-COMP-008).
- **SRS-UI-05 (Confirmed):** The interface shall be responsive (usable across typical screen sizes); the supported-browser and device baseline is not established and remains open (PRD §12; SRS §7.6).
- **SRS-UI-06 (Confirmed):** Interface hiding is a usability measure only; no business rule or authorization decision shall rely on the interface (FR-AUTH-004, FR-DASH-007).
- **SRS-UI-07 (Open):** The target accessibility conformance level is not established (PRD §12, §18.3). Until decided, no formal accessibility conformance is claimed (SRS §7.4).

Presentation details (layout, components, styling) belong to the UI/UX Specification and are not specified here.

### 5.2 Software Interfaces

**Frontend–backend interface.**

- **SRS-IF-01 (Confirmed):** The frontend shall communicate with the backend through a defined API boundary, consistent with the established separate frontend and backend (PRD §11.2). The protocol style, endpoint catalogue, and payload schemas are not established by the source documents and are deferred to the API Specification.
- **SRS-IF-02 (Confirmed):** Every API operation classified as protected in FRD §4 shall enforce authentication and authorization on the server side, considering role and record scope (FR-AUTH-003, FR-AUTH-004).
- **SRS-IF-03 (Confirmed):** The API shall validate inputs at the server boundary and reject invalid requests before any persistence (FR-USER-006, FR-INT-003, FR-REPORT-002, FR-COHORT-001, FR-EXPORT-005).
- **SRS-IF-04 (Confirmed):** The API shall convey outcomes in the failure categories of FRD §7.1 (business validation, authorization, invalid transition, technical, configuration-missing) such that callers can distinguish them; concrete status codes and error shapes belong to the API Specification.
- **SRS-IF-05 (Confirmed):** The API shall represent workflow states so that clients cannot bypass the report state model through direct state manipulation (FRD §5.3, §5.4).

**Backend–database interface.**

- **SRS-IF-06 (Confirmed):** The backend shall persist structured data in PostgreSQL using Spring Data JPA and Hibernate (PRD §11.2).
- **SRS-IF-07 (Confirmed):** The backend shall maintain referential integrity across users, cohorts, internships, requirements, reports, documents, reviews, compliance results, completions, notifications, and audit events (FR-INT-008; SRS §8).
- **SRS-IF-08 (Confirmed):** The backend shall enforce uniqueness of one report per internship per reporting period atomically at persistence so that concurrent creation requests cannot both succeed (FR-REPORT-006).
- **SRS-IF-09 (Confirmed):** State-changing operations that have required audit or history side effects shall apply both within a consistent transaction so that neither is applied without the other (FR-REVIEW-002; FR-AUDIT-001).
- **SRS-IF-10 (Confirmed):** Database failures shall surface as technical failures with no partial state (SRS §7.3, SRS §11).

**Backend–object storage interface.**

- **SRS-IF-11 (Confirmed):** Uploaded documents shall be stored in private object storage and retrieved only through permission-checked backend paths; no publicly accessible file URL shall exist (FR-DOC-007).
- **SRS-IF-12 (Confirmed):** Storage failures (unavailable store, upload/retrieval error) shall surface as technical failures with no half-created document record (FR-DOC-002, FR-DOC-007; SRS §11).

**Notification interfaces.**

- **SRS-IF-13 (Confirmed):** The system shall deliver notifications through an in-application notification store and through a configured email delivery service (FR-NOTIF-004, FR-NOTIF-005; PRD §11.2).
- **SRS-IF-14 (Confirmed):** Notification delivery shall be non-blocking: a delivery failure shall not roll back, block, or alter the triggering business operation, and the failure shall be recorded (FR-NOTIF-006).
- **SRS-IF-15 (Confirmed):** Notifications shall not embed sensitive document contents (FR-NOTIF-003).

### 5.3 Communication Interfaces

- **SRS-IF-16 (Derived):** Client–server and server–service communication shall use the standard web transport over TLS in deployed environments, consistent with the security expectations for a web application handling student data (PRD §12). Specific TLS versions, cipher policy, and certificate management belong to the Security Requirements and deployment documentation.
- **SRS-IF-17 (Open):** Email transport, retry behavior, and delivery guarantees are not established and remain open (OD-09). No specific email protocol configuration is assumed here.

### 5.4 Hardware Interfaces

- **SRS-IF-18 (Not applicable):** InternFlow has no dedicated hardware interface requirements. It runs on general-purpose hosting infrastructure and is accessed through browsers. No device-specific hardware integration is required for the MVP (PRD §11.2).

## 6. Functional Requirements Overview

### 6.1 Relationship to the FRD

The FRD is the authoritative source for detailed functional behavior, role permissions, the report state model, validation and edge cases, and functional acceptance criteria (FRD §6, §5, §4, §7). This SRS does not reproduce those 104 requirement definitions. Instead it:

- Confirms that all 13 MVP modules are represented and traceable (SRS §13).
- States a small set of **system-level** functional requirements (SRS §6.3) that describe cross-cutting system behavior implied by the FRD, so that architecture and testing have a system-level anchor.
- References FRD identifiers wherever functional detail is needed.

### 6.2 Module-Level Functional Summary

| Module | Functional capability (system level) | FRD requirements | Key cross-cutting concerns |
|---|---|---|---|
| 1. Authentication and RBAC | Authenticate users; enforce role and scope on every protected operation; generic failure messages; deactivation handling. | FR-AUTH-001 … FR-AUTH-007 | Server-side enforcement; non-enumeration; OD-15, OD-01 |
| 2. User Management | Create and administer accounts, roles, and status; separate administration from internship scope; no deletion. | FR-USER-001 … FR-USER-008 | OD-01, OD-15, PR-06, AS-05 |
| 3. Internship Management | Register and maintain internships; enforce one active internship per student; track anchor lifecycle states. | FR-INT-001 … FR-INT-008 | OD-12, OD-13, AS-01 |
| 4. Cohort Management | Configure cohorts, membership, faculty assignment, deadlines, and requirement sets. | FR-COHORT-001 … FR-COHORT-007 | OD-03, OD-10, OD-14, AS-02, AS-03 |
| 5. Document Management | Define required documents; accept, store privately, and review documents; expose status. | FR-DOC-001 … FR-DOC-008 | OD-05, OD-16 |
| 6. Weekly Reports and Work Logs | Manage the report lifecycle: draft, submit, correct, resubmit, approve. | FR-REPORT-001 … FR-REPORT-010 | Report state model; OD-10 |
| 7. Faculty Review Workflow | Scoped review queue, correction requests, approvals, review history. | FR-REVIEW-001 … FR-REVIEW-010 | PR-01, PR-02, PR-03 |
| 8. Compliance Engine | Resolve applicable requirements; compute compliance and missing requirements. | FR-COMP-001 … FR-COMP-010 | OD-03, OD-04, OD-05 |
| 9. Role-Specific Dashboards | Scoped, consistent role dashboards with pending actions. | FR-DASH-001 … FR-DASH-007 | PR-01, OD-10 |
| 10. Notifications | Generate and deliver in-app/email notifications for workflow events. | FR-NOTIF-001 … FR-NOTIF-007 | OD-09, OD-10, OD-11 |
| 11. Completion Verification | Verify completion only when eligible and by an authorized actor; expose distinct status. | FR-COMPLETE-001 … FR-COMPLETE-008 | OD-08, OD-03 |
| 12. Audit History | Record, protect, and expose important actions. | FR-AUDIT-001 … FR-AUDIT-006 | OD-06, OD-11 |
| 13. Reports and CSV Exports | Provide operational reports and permission-respecting CSV exports. | FR-EXPORT-001 … FR-EXPORT-008 | OD-07, OD-06 |

### 6.3 System-Level Functional Requirements

These system-level functional requirements are cross-cutting: each restates, at the system boundary, a behavior that the FRD defines for specific modules. They are **Confirmed** and are mapped to their FRD sources. They do not add functional scope.

| ID | System-level requirement | Status | FRD source |
|---|---|---|---|
| SRS-FR-001 | The system shall enforce authorization server-side for every protected operation, considering both the actor's role and the record scope, and shall deny operations not permitted by the FRD permission matrix. | Confirmed | FR-AUTH-003, FR-AUTH-004, FR-AUTH-007; FRD §4 |
| SRS-FR-002 | The system shall expose only the 13 MVP modules and their workflows, and shall not expose post-MVP functionality (Company Mentor, attendance, analytics/PDF, extra notification channels, multi-college, external integrations) in the MVP. | Confirmed | PRD §7.2, §16; FRD §2.2 |
| SRS-FR-003 | The system shall change report state only through the transitions defined in the report state model, and shall reject direct state manipulation. | Confirmed | FRD §5.2, §5.4 |
| SRS-FR-004 | The system shall compute a single compliance result per internship and shall use it consistently across dashboards, operational reports, CSV exports, and completion eligibility. | Confirmed | FR-COMP-007, FR-DASH-006, FR-COMPLETE-001 |
| SRS-FR-005 | The system shall record the required audit events atomically with the action that produces them, so that no required action lacks an audit record and no audit record exists for a failed action. | Confirmed | FR-AUDIT-001, FR-REVIEW-002 |
| SRS-FR-006 | The system shall resolve notification recipients from record relationships and permissions, never from user-supplied addresses, so that users are notified only for events concerning their own records or assigned cohort. | Confirmed | FR-NOTIF-002 |
| SRS-FR-007 | The system shall enforce record scope on every read, list, filter, pagination, sorting, and export operation, so that no client-supplied parameter can expose out-of-scope records. | Confirmed | FR-DASH-005, FR-EXPORT-003, FR-COHORT-006 |
| SRS-FR-008 | The system shall store uploaded documents in private object storage with permission-checked retrieval and no publicly accessible URLs. | Confirmed | FR-DOC-007 |
| SRS-FR-009 | The system shall enforce the project's no-deletion policy in the MVP: internships, reports, documents, and cohorts are not deleted; deactivation or supersession is used where applicable. | Confirmed | PR-08; FR-USER-005, FR-COHORT-007 |
| SRS-FR-010 | The system shall reject an internship completion verification when any mandatory requirement is unsatisfied or unevaluated, and shall record completion only when requirements are satisfied and an authorized actor verifies. | Confirmed | FR-COMPLETE-001, FR-COMPLETE-004, FR-COMPLETE-007 |

## 7. Non-Functional Requirements

This section states InternFlow's quality requirements. Each requirement carries a status. Where the source documents do not establish a measurable target, this section records the need to define and validate the target rather than inventing one; candidate targets, where offered, appear only in a clearly marked **Proposed — not yet approved** subsection and are not confirmed requirements (PRD §12, §18.3; SRS §2.6).

Identifier scheme: NFR-&lt;CATEGORY&gt;-NNN, where CATEGORY is SEC, PERF, REL, UX, MNT, CMP, DAT, AUD, or PRV.

### 7.1 Security

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-SEC-001 | All protected operations shall be authorized server-side; interface visibility shall never be the enforcement mechanism. | Confirmed | FR-AUTH-003, FR-AUTH-004; PRD §12 |
| NFR-SEC-002 | Authorization shall default to denial: an operation is permitted only when the FRD permission matrix explicitly allows it under stated conditions. | Confirmed | FRD §4.1, §4.2 |
| NFR-SEC-003 | Authentication failures shall use a single generic message that does not reveal whether an account exists or whether it is activated or deactivated. | Confirmed | FR-AUTH-001, FR-AUTH-005, FR-AUTH-006 |
| NFR-SEC-004 | Document content shall be stored privately and served only through permission-checked paths; no public or guessable URL shall expose document content. | Confirmed | FR-DOC-007; PRD §11.2 |
| NFR-SEC-005 | All create, update, submit, review, and export operations shall validate inputs at the server boundary and reject invalid input before persistence. | Confirmed | FR-USER-006, FR-INT-003, FR-REPORT-002, FR-COHORT-001, FR-EXPORT-005 |
| NFR-SEC-006 | CSV exports shall neutralize values that spreadsheet applications could interpret as formulas, so that exported data cannot execute in a spreadsheet. | Confirmed | FR-EXPORT-004 |
| NFR-SEC-007 | Security-relevant events in the required audit set shall be recorded and protected from application-level modification (see SRS §7.8). | Confirmed | FR-AUDIT-001, FR-AUDIT-005 |
| NFR-SEC-008 | Sensitive configuration and secrets (for example, database credentials, email-service credentials, storage credentials) shall be supplied through externalized configuration and shall not be committed to source control. | Derived | PRD §12 (protection of sensitive data); implementation policy deferred to Security Requirements |
| NFR-SEC-009 | Authentication mechanics — credential model, password policy, credential storage, token/session lifetime, renewal, and lockout/rate limiting — shall comply with the Security Requirements once decided; the MVP shall not rely on any specific mechanism before that decision. | Open decision (OD-15) | FRD OD-15; FR-AUTH-002, FR-AUTH-006 |
| NFR-SEC-010 | The system shall not claim compliance with any security standard or regulation unless such compliance is explicitly established and verified. | Constraint | PRD §12; SRS §2.1 |

### 7.2 Performance

No numerical performance targets have been established by the source documents (PRD §12, §18.3). The following requirements therefore define behavioral performance expectations and record the need to define measurable targets. A clearly marked proposed target set is offered for review in SRS §7.2.1 and is **not** a confirmed requirement.

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-PERF-001 | Common interactive operations (opening dashboards, viewing records, submitting reports, reviewing) shall complete within a time that is acceptable for interactive use under the MVP's intended single-department load. | Open (target undefined) | PRD §12, §18.3 |
| NFR-PERF-002 | Measurable response-time, throughput, and concurrency targets for the intended deployment shall be defined and validated before they are treated as acceptance commitments. | Open decision (targets) | PRD §18.3 |
| NFR-PERF-003 | Data retrieval for lists, dashboards, reports, and exports shall apply the requesting user's scope filters in the data layer rather than retrieving all records and filtering afterward. | Derived | FR-DASH-005, FR-EXPORT-003 |
| NFR-PERF-004 | Compliance-derived displays shall be served from the single evaluated compliance source (or a consistent materialization of it) so that performance optimizations do not produce divergent results. | Confirmed | FR-COMP-007, FR-DASH-006 |
| NFR-PERF-005 | CSV export generation shall not block other users' interactive operations for the duration of the export. | Proposed | FR-EXPORT-007; PRD §12 |

#### 7.2.1 Proposed Performance Targets (Not Approved)

The following candidate targets are offered only to make the open performance requirement concrete for review. **They are Proposed, not approved, and are not confirmed requirements.** They have not been validated and must not be treated as acceptance commitments until approved (PRD §18.3).

| Candidate target | Value (proposed) | Scope |
|---|---|---|
| Interactive read (page/API) p95 response time | ≤ 2 seconds | Single-department demonstration load |
| Write operation (submit/approve/create) p95 response time | ≤ 3 seconds | Single-department demonstration load |
| CSV export for one cohort report | completes within 30 seconds | Bounded by a proposed row cap to be decided with OD-07 |
| Nominal concurrent active users | ≥ 25 | Single-department demonstration |
| Dashboard/aggregate load | ≤ 3 seconds | Single-department demonstration |

These proposed values are indicative only and require product/technical approval; the row cap and any export-size limits are not established and are part of OD-07.

### 7.3 Reliability and Availability

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-REL-001 | A rejected or failed operation shall leave data unchanged; no partial writes or corrupt state shall result (atomicity at the operation boundary). | Confirmed | FRD §7.3 |
| NFR-REL-002 | A state-changing workflow action and its required audit/history entries shall be applied atomically: if audit or history persistence fails, the action fails and no state change is recorded. | Confirmed | FR-REVIEW-002, FR-AUDIT-001 |
| NFR-REL-003 | Repeated submission or retry requests shall be handled idempotently so that at most one state change (and at most one timestamp) results from a repeated action. | Confirmed | FR-REPORT-006 (T8); FRD §5.2 |
| NFR-REL-004 | Notification delivery shall be non-blocking; a notification failure shall not affect the triggering business operation, and the failure shall be recorded for operational visibility. | Confirmed | FR-NOTIF-006 |
| NFR-REL-005 | Compliance evaluation failure shall mark the affected result stale or unevaluated and shall never mark a requirement satisfied; stale results shall be re-processed. | Confirmed | FR-COMP-007 |
| NFR-REL-006 | Duplicate notifications for a single event occurrence (including retries) shall not be created. | Confirmed | FR-NOTIF-007 |
| NFR-REL-007 | The system shall surface a clear failure outcome on persistence, storage, or export errors rather than presenting a partial result as complete. | Confirmed | FR-EXPORT-007; FRD §7.1 |
| NFR-REL-008 | No uptime target, service-level agreement, or recovery-time/recovery-point objective is established; such commitments shall not be invented. Availability expectations remain demonstration-focused. | Open decision (targets) | PRD §12, §18.3; SRS §14.4 |

### 7.4 Usability and Accessibility

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-UX-001 | Navigation and landing surfaces shall be role-appropriate; users shall not be offered operations their role or scope does not permit. | Confirmed | FR-AUTH-001, FR-DASH-007 |
| NFR-UX-002 | Workflow states, review feedback, and outcomes shall be presented understandably: students shall see correction feedback and pending actions; faculty shall see review queues and outcomes. | Confirmed | FR-REVIEW-006 (PR-03); FR-DASH-001, FR-DASH-002 |
| NFR-UX-003 | Validation and error messages shall be explanatory of the rule violated and shall be categorized consistently with the FRD failure categories; error text shall not assert a policy that is still open. | Confirmed | FRD §7.1, §7.3 |
| NFR-UX-004 | The interface shall be responsive across typical screen sizes. | Confirmed | PRD §12 |
| NFR-UX-005 | The system shall not claim formal accessibility conformance; the target conformance level is not established and remains open. | Open decision (target) | PRD §12, §18.3 |
| NFR-UX-006 | Terminology presented to users shall be consistent with the PRD and FRD glossaries (PRD §17; FRD §2.5). | Derived | PRD §17; FRD §2.5 |

### 7.5 Maintainability

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-MNT-001 | The system shall be implemented as a modular monolith with clear module boundaries and separated responsibilities; microservices shall not be introduced. | Confirmed | PRD §11.2, §14.1 |
| NFR-MNT-002 | Frontend, backend, persistence, and external-service responsibilities shall remain clearly separated, with the backend enforcing business rules, authorization, and persistence. | Confirmed | PRD §11.2, §10 |
| NFR-MNT-003 | Coding conventions and project structure shall be consistent within the codebase. | Derived | PRD §12 (maintainability) |
| NFR-MNT-004 | Automated tests shall be implemented using the selected stack (JUnit, Spring Boot Test, Playwright) covering critical workflows. | Confirmed | PRD §11.2, §13.1 |
| NFR-MNT-005 | Requirements and technical documentation shall be maintained with traceability between requirements and their verification (this SRS and the future Test Plan). | Confirmed | PRD §12; FRD §2.3 |
| NFR-MNT-006 | Quantitative code-quality and test-coverage targets are not established; such targets shall be defined where practical before being treated as commitments. | Open decision (targets) | PRD §18.3 |

### 7.6 Compatibility

The source documents do not establish supported browsers, devices, or operating systems (PRD §12, §18.3). Rather than guessing, this section records the open target and one clearly marked proposed baseline.

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-CMP-001 | Supported browser and device targets are not established and shall be defined before compatibility is treated as a requirement. | Open decision (target) | PRD §12, §18.3 |
| NFR-CMP-002 | The MVP shall not depend on browser-specific behavior that would prevent operation in current mainstream browsers. | Derived | PRD §12 |
| NFR-CMP-003 | The system shall not assume a specific client operating system; access is browser-based. | Confirmed | PRD §11.2 |

**Proposed baseline (not approved):** support for the latest two stable major versions of mainstream evergreen desktop browsers (for example, Chromium-based, Firefox, and Safari families) and responsive access on current mobile browsers. This is a proposed target only and requires approval; it is not a confirmed requirement.

### 7.7 Data Integrity

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-DAT-001 | Referential integrity shall be maintained across users, cohorts, internships, requirements, reports, documents, reviews, compliance results, completions, notifications, and audit events. | Confirmed | FR-INT-008; SRS §8 |
| NFR-DAT-002 | At most one report shall exist per internship per reporting period, enforced atomically at persistence so that concurrent creation requests cannot both succeed. | Confirmed | FR-REPORT-006 |
| NFR-DAT-003 | Workflow state shall change only through the defined transitions; direct or out-of-band state manipulation shall be rejected. | Confirmed | FRD §5.2, §5.4 |
| NFR-DAT-004 | No MVP operation shall delete internships, reports, documents, or cohorts; deactivation or supersession shall be used where applicable. | Confirmed | PR-08; FR-USER-005 |
| NFR-DAT-005 | Review history and verification history shall be append-only through application workflows and shall not be editable or deletable by any role. | Confirmed | FR-REVIEW-006, FR-COMPLETE-008 |
| NFR-DAT-006 | Compliance-derived information shall be consistent across all surfaces that display it (single source of truth). | Confirmed | FR-COMP-007, FR-DASH-006 |
| NFR-DAT-007 | Deactivating a user shall preserve that user's records and their attribution; no cascade deletion or anonymization shall occur. | Confirmed | FR-USER-005, FR-AUDIT-003 |
| NFR-DAT-008 | Concurrent conflicting workflow actions (for example, two approvals racing) shall result in exactly one effective transition, with only one action recorded. | Confirmed | FR-REVIEW-002, FRD §7.2 (case 22) |
| NFR-DAT-009 | Configuration gaps (for example, a cohort without a requirement set) shall be represented explicitly and shall never produce a false compliant or complete result. | Confirmed | FR-COMP-001, FR-COMP-010 |

### 7.8 Auditability and Traceability

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-AUD-001 | The system shall record the required audit events for workflow, user, requirement, and completion actions, atomically with the action. | Confirmed | FR-AUDIT-001 |
| NFR-AUD-002 | Each audit event shall record event type, actor (or "system"), timestamp, affected entity, and applicable before/after values. | Confirmed | FR-AUDIT-002 |
| NFR-AUD-003 | Attribution shall persist even if the acting user is later deactivated or their role changes; attribution shall never change retroactively. | Confirmed | FR-AUDIT-003 |
| NFR-AUD-004 | No application-level operation shall modify or delete a recorded audit event, for any role including administrators. | Confirmed | FR-AUDIT-005 |
| NFR-AUD-005 | Audit history shall be visible only to authorized users within their scope; the final viewing mapping is subject to decision. | Confirmed (viewing map Open) | FR-AUDIT-004; OD-06 |
| NFR-AUD-006 | The retention duration for audit history and other records shall not be invented; retention is distinct from immutability and visibility. | Open decision | FR-AUDIT-006; OD-11 |
| NFR-AUD-007 | Requirements shall be traceable to the PRD and FRD, and verification methods shall be traceable to requirements (SRS §13). | Confirmed | PRD §12; FRD §2.3 |

### 7.9 Privacy and Data Protection

| ID | Requirement | Status | Source |
|---|---|---|---|
| NFR-PRV-001 | Access to student records and documents shall be restricted per the FRD permission matrix; users shall not access records outside their permitted scope. | Confirmed | FRD §4; FR-DOC-007, FR-INT-006, FR-REPORT-007 |
| NFR-PRV-002 | Reports and CSV exports shall respect the requesting user's data-access permissions; exported data shall contain only permitted rows. | Confirmed | FR-EXPORT-003; PRD §7.1.13 |
| NFR-PRV-003 | Notifications shall not embed sensitive document content; they shall carry only the context needed to identify the event (permission re-checked on navigation). | Confirmed | FR-NOTIF-003 |
| NFR-PRV-004 | Data classification, encryption requirements, personal-data handling, and applicable institutional or legal policies are not established and shall be defined before they are treated as commitments. The system shall not claim compliance with any privacy law or regulation without an established basis. | Open decision | PRD §12, §18.3; FRD OD-01, OD-11 |

## 8. Data and Information Requirements

This section describes the conceptual data and its constraints at the requirements level. It does not define a database schema; the physical design belongs to the Database Design Document and is deferred (OD-12, OD-13, OD-14, OD-03, OD-11).

### 8.1 Conceptual Data Model

The system manages the following conceptual entities. Names are descriptive, not a schema.

| Entity | Meaning | Key relationships |
|---|---|---|
| User | A person with an account, a single role, and an account status. | Has one role (AS-04); may be a Student, Faculty Coordinator, or College Administrator. |
| Role | The assigned role of a user. | One of the three MVP roles; no other role exists in the MVP. |
| Cohort | A group of students managed together with an applicable requirement set and deadlines. | Has many internships; has many requirement definitions; has assigned Faculty Coordinators. |
| Cohort requirement | A defined obligation configured for a cohort and applied to its internships; classified mandatory or optional. | Belongs to one cohort; consumed by compliance evaluation; feeds completion eligibility. |
| Internship | The internship record of a student within a cohort. | Belongs to one student (AS-01) and one cohort; has many reports and documents; has at most one completion record. |
| Document | Metadata for a submitted required document. | Belongs to one internship and one student; associated with a document category. |
| Document content | The stored file for a document, held in private object storage. | Referenced by a document; retrieved only through permission-checked paths. |
| Report | A weekly report record with its work-log content, tracked through the report states. | Belongs to one internship; at most one per internship per reporting period (FR-REPORT-006). |
| Review history entry | An append-only record of a correction request or approval on a report. | Belongs to one report. |
| Requirement evaluation / compliance result | The system's evaluation of an internship against its requirements. | Derived from reports, documents, and requirement definitions; consumed by dashboards, reports, exports, and completion eligibility. |
| Notification | A per-recipient in-app/email notification for a workflow event. | Belongs to one recipient; references the triggering record. |
| Completion record | The record of a verified internship completion. | Belongs to one internship; records the verifier and evaluated requirement results at verification. |
| Audit event | An immutable record of an important action or decision. | References an actor (or system) and an affected entity. |

**SRS-DR-001 (Confirmed):** The system shall maintain the relationships above such that compliance evaluation can always resolve an internship's applicable requirement set through the chain student → internship → cohort → requirement set (FR-INT-008, FR-COMP-001).

**SRS-DR-002 (Confirmed):** Each internship shall resolve to exactly one student and exactly one cohort (FR-INT-002).

**SRS-DR-003 (Confirmed):** Each user shall have exactly one role in the MVP (AS-04).

**SRS-DR-004 (Assumption):** A student shall have at most one active internship (AS-01) and belong to exactly one cohort at a time (AS-02); both are assumptions requiring confirmation.

**SRS-DR-005 (Confirmed):** A report shall belong to exactly one internship and shall record its reporting period; at most one report may exist per internship-period pair (FR-REPORT-006).

**SRS-DR-006 (Confirmed):** A document shall belong to exactly one internship and one student, and shall be associated with a required category (FR-DOC-001, FR-DOC-002).

**SRS-DR-007 (Confirmed):** A completion record shall exist only as the result of authorized completion verification and shall reference the internship, the verifier, and the evaluated requirement results at verification (FR-COMPLETE-003).

**SRS-DR-008 (Confirmed):** Audit events shall reference an actor (or be explicitly system-originated) and the affected entity, and shall not be editable or deletable through application workflows (FR-AUDIT-002, FR-AUDIT-003, FR-AUDIT-005).

**SRS-DR-009 (Open):** The full internship field catalogue and lifecycle state set, the reassignment and cohort-move rules, and the requirement-set versioning model are not established and remain open (OD-12, OD-13, OD-14, OD-03).

### 8.2 Data Constraints and Uniqueness Rules

| ID | Constraint | Status | Source |
|---|---|---|---|
| SRS-DR-010 | Report uniqueness per internship and reporting period shall be enforced at persistence, atomically, so concurrent creation cannot produce two reports. | Confirmed | FR-REPORT-006 |
| SRS-DR-011 | Requirement records shall carry a mandatory/optional classification, and only mandatory requirements affect compliance completeness and completion eligibility. | Confirmed | FR-COHORT-004, FR-COMP-002 |
| SRS-DR-012 | Referential integrity shall prevent breaking the internship→cohort→requirement chain; referenced cohorts shall not be deleted. | Confirmed | FR-INT-008; PR-08 |
| SRS-DR-013 | Review history and verification history shall be append-only through application workflows. | Confirmed | FR-REVIEW-006, FR-COMPLETE-008 |
| SRS-DR-014 | Deactivation shall not delete or anonymize records or their attribution. | Confirmed | FR-USER-005, FR-AUDIT-003 |
| SRS-DR-015 | Timestamps recorded for submission, resubmission, review, verification, and audit shall reflect actual event times. | Confirmed | FR-REPORT-005, FR-REVIEW-002, FR-AUDIT-002 |

### 8.3 Data Retention (Deferred)

**SRS-DR-016 (Open):** No retention duration is established for completed records, audit events, notifications, document versions, or history. Within the MVP, no automated deletion, purge, or archival operation exists for these records; this is an interim operational statement, not a retention policy, and shall not be cited as an agreed duration (FR-AUDIT-006, OD-11). A formal retention, archival, and purge policy is deferred to the Database Design and Deployment/Operations documents.

## 9. Business Rules and Workflow Constraints

This section summarizes the business rules that constrain the system, at the requirements level. The FRD is authoritative for their detail (FRD §5, §6.6–§6.13).

### 9.1 Report Lifecycle

The report state model has exactly five states and no others in the MVP: `DRAFT`, `SUBMITTED`, `CORRECTION_REQUESTED`, `RESUBMITTED`, `APPROVED` (FRD §5.1). `APPROVED` is terminal in the MVP (FRD §5.5).

| Rule | Statement | Status | FRD source |
|---|---|---|---|
| SRS-WF-001 | A report shall be created once per internship and reporting period and reused through all later states; no later action creates an additional report for the period. | Confirmed | FR-REPORT-001, FR-REPORT-006 |
| SRS-WF-002 | Only the owning Student may submit, revise, or resubmit a report; only an assigned Faculty Coordinator may request corrections or approve. | Confirmed | FRD §4.2.6, §4.2.7 |
| SRS-WF-003 | A report's state shall change only through the defined transitions; editing content is a permission, not a transition, and does not itself change state. | Confirmed | FRD §5.3, §5.4 |
| SRS-WF-004 | In `CORRECTION_REQUESTED`, the owner may revise content in place; the state advances to `RESUBMITTED` only on the Resubmit action. | Confirmed | FR-REPORT-008, FR-REPORT-009 |
| SRS-WF-005 | Only `APPROVED` satisfies a period's report requirement; `SUBMITTED`, `CORRECTION_REQUESTED`, and `RESUBMITTED` do not; an absent report is missing. | Confirmed | FR-REPORT-010, FR-COMP-003, FR-COMP-004 |
| SRS-WF-006 | No Rejected, Withdrawn, or Reopened state exists in the MVP; introducing one requires a decision. | Confirmed (terminal default) | FRD §5.1, §5.5; OD-02 |
| SRS-WF-007 | Repeated submission requests shall be idempotent; correction resubmission reuses the existing report record. | Confirmed | FR-REPORT-006 (T8); FRD §5.2 |
| SRS-WF-008 | Review deadline and late-submission handling are not enforced under the current interim rules until decided; actual timestamps are recorded. | Interim / Open | FR-REPORT-005, FR-REVIEW-009; OD-10 |

The first major milestone is the end-to-end flow across SRS-WF-001 through SRS-WF-005: submit → request corrections → resubmit → approve → compliance updates (PRD §9; FRD §5).

### 9.2 Compliance Rules

| Rule | Statement | Status | FRD source |
|---|---|---|---|
| SRS-WF-009 | Compliance shall be evaluated only against the internship's cohort requirement set; an unconfigured cohort yields a "not configured" state, never compliant. | Confirmed | FR-COMP-001 |
| SRS-WF-010 | An internship is fully compliant only when every mandatory requirement is satisfied; optional requirements are reported but do not block compliance or completion. | Confirmed | FR-COMP-002 |
| SRS-WF-011 | Compliance shall be recomputed in response to compliance-relevant events; displays shall never contradict the latest recorded state beyond the accepted processing window. | Confirmed | FR-COMP-007 |
| SRS-WF-012 | Missing-requirement displays shall enumerate every unsatisfied mandatory requirement with a plain-language reason; unevaluated requirements shall be shown as such. | Confirmed | FR-COMP-008 |
| SRS-WF-013 | The compliance status vocabulary and derivation are proposed and subject to decision. | Proposed / Open | FR-COMP-010; OD-04 |
| SRS-WF-014 | Whether a requirement-set change propagates to existing internships is not decided; the system shall not hardcode retroactive or grandfather behavior. | Open | FR-COMP-009; OD-03 |
| SRS-WF-015 | Document submission satisfies a document requirement under the current interim rule; whether document review is required is not decided. | Interim / Open | FR-COMP-005; OD-05 |
| SRS-WF-016 | No percentage score, weighting, grace period, or waiver exists in the MVP. | Confirmed | FR-COMP-002 |

### 9.3 Completion Rules

| Rule | Statement | Status | FRD source |
|---|---|---|---|
| SRS-WF-017 | An internship is eligible for completion verification only when every mandatory requirement is satisfied and none is missing, unapproved, in outstanding correction, or unevaluated. | Confirmed | FR-COMPLETE-001 |
| SRS-WF-018 | Only a Faculty Coordinator assigned to the internship's cohort may verify completion; administrator authority is not granted by default. | Confirmed (admin open) | FR-COMPLETE-002; OD-08 |
| SRS-WF-019 | Completion shall be recorded only as the result of authorized verification and shall be attributed to the verifying user and time. | Confirmed | FR-COMPLETE-003 |
| SRS-WF-020 | A verification attempt with unmet mandatory requirements shall be rejected with the outstanding-requirement list, and no completion record shall be created. | Confirmed | FR-COMPLETE-004 |
| SRS-WF-021 | Completion status shall be distinct from compliance/dashboard progress; progress shall never be represented as verified completion. | Confirmed | FR-COMPLETE-006 |
| SRS-WF-022 | Verified completion is immutable in the MVP; no operation reverses it; whether reversal is possible is not decided. | Interim / Open | FR-COMPLETE-005; OD-08 |

### 9.4 Cross-Module Business Rules

- **SRS-WF-023 (Confirmed):** Compliance contributions, dashboard figures, reports, and exports shall derive from the same compliance evaluation (SRS-FR-004; FR-COMP-007, FR-DASH-006).
- **SRS-WF-024 (Confirmed):** Scope (role plus cohort/internship assignment) shall be enforced on every operation, not only presentation (SRS-FR-001, SRS-FR-007).
- **SRS-WF-025 (Confirmed):** Administrative role status alone shall never confer review, approval, or completion-verification authority (PR-05; FRD §4.4).
- **SRS-WF-026 (Open):** Deadline and late-submission policy, review-deadline policy, and reporting cadence are not established; interim behavior applies until decided (OD-10).

## 10. Authorization and Access-Control Requirements

### 10.1 Enforcement Model

- **SRS-AUTH-001 (Confirmed):** Every operation classified as protected in FRD §4 shall be authorized server-side before any business logic executes (FR-AUTH-003).
- **SRS-AUTH-002 (Confirmed):** Authorization shall consider both the actor's role and the record scope (for example, own record, assigned cohort, deployment scope); a role alone never grants record-level authority (FR-USER-007, FR-AUTH-003).
- **SRS-AUTH-003 (Confirmed):** Authorization shall default to denial; access is granted only where FRD §4 explicitly allows it under stated conditions (FRD §4.1).
- **SRS-AUTH-004 (Confirmed):** Authorization failures shall not disclose the content of the denied record and shall not confirm or deny the existence of records outside the requester's scope (FR-AUTH-003, FRD §7.3).
- **SRS-AUTH-005 (Confirmed):** Hiding navigation or controls shall never be the enforcement mechanism; direct requests to restricted surfaces or operations shall still be denied (FR-AUTH-004).
- **SRS-AUTH-006 (Confirmed):** Record scope shall be enforced on reads, lists, filters, sorting, pagination, and exports, so that no client-supplied parameter can expose out-of-scope records (FR-DASH-005, FR-EXPORT-003).

### 10.2 Role and Scope Model

The authoritative permission matrix is FRD §4 and is not reproduced here. This SRS records the scope model:

| Role | Record scope | Notes |
|---|---|---|
| Student | Own records only (own internship, reports, documents, compliance status, notifications). | Cross-student access denied (PR-01). |
| Faculty Coordinator | Students of assigned cohorts only. | Sole weekly-report approver (PRD §11.1); completion verification for assigned cohorts. |
| College Administrator | Deployment-wide (single department/cohort). | Administrative authority only; no review/approval/completion authority (PR-05). |

**SRS-AUTH-007 (Confirmed):** A Faculty Coordinator with no assigned cohort shall have no students to review and shall be denied all review operations (FR-USER-007).

**SRS-AUTH-008 (Confirmed):** A user shall be denied any operation against a record outside their scope, including via direct reference or parameter manipulation (FR-COHORT-006, FR-DASH-005, FR-EXPORT-003).

### 10.3 Pending Permission Decisions

The following permission boundaries are **not** settled and are carried forward without a fabricated default (FRD §4.3, §4.4; OD-01, OD-06, OD-08):

- **SRS-AUTH-009 (Open):** Exact data-scope boundaries: faculty cross-cohort access; administrator access to sensitive student record content; administrator self-role-change and self-deactivation; audit access for faculty (OD-01).
- **SRS-AUTH-010 (Open):** Completion-verification authority for administrators and whether multiple approvers are required (OD-08).
- **SRS-AUTH-011 (Open):** Audit viewing permissions and any audit export capability (OD-06).
- **SRS-AUTH-012 (Proposed):** The proposed permission rules PR-01 through PR-08 (FRD §4.4) require reviewer confirmation before they are treated as confirmed; the MVP shall not implement them as confirmed rules until confirmed.

## 11. Error Handling and Exceptional Conditions

### 11.1 Error Taxonomy

**SRS-ERR-001 (Confirmed):** The system shall classify and report outcomes using the FRD failure categories, so that callers and users can distinguish them (FRD §7.1):

| Category | Meaning | Required system behavior |
|---|---|---|
| Business validation failure | Request understood and permitted but violates a business rule. | Explanatory message; no state change; nothing persisted. |
| Authorization failure | Role or scope does not permit the operation. | Uniform access-denied outcome; no payload; no state change; no existence disclosure. |
| Invalid-transition failure | Operation permitted in general but the record state is incompatible. | Invalid-transition outcome; no state change. |
| Technical failure | Infrastructural, storage, persistence, or delivery failure. | Clear failure outcome; no partial or corrupt state; failure recorded. |
| Configuration-missing state | Required configuration does not exist. | Explicit "not configured" state; never a false compliant result. |

**SRS-ERR-002 (Confirmed):** These categories are functional; concrete error codes, HTTP status codes, and response schemas belong to the API Specification and are not defined here (FRD §7.1).

### 11.2 Exceptional Condition Requirements

The FRD enumerates the MVP's edge and error cases (FRD §7.2). This SRS states the system-level requirements that must hold across them:

- **SRS-ERR-003 (Confirmed):** Missing or invalid input on create, edit, or submit operations shall be rejected with a category-level message and no persistence (FRD §7.2 cases 1–4; FR-USER-006, FR-INT-003, FR-REPORT-002).
- **SRS-ERR-004 (Confirmed):** Unauthorized or out-of-scope access shall be denied with no content and no state change (FRD §7.2 cases 6–8).
- **SRS-ERR-005 (Confirmed):** Invalid workflow transitions shall be rejected with no state or history change (FRD §7.2 cases 10–11; FRD §5.4).
- **SRS-ERR-006 (Confirmed):** Storage, persistence, notification, and export failures shall be treated as technical failures that never corrupt or partially write data (FRD §7.2 cases 12, 20, 21, 28).
- **SRS-ERR-007 (Confirmed):** Concurrent or repeated conflicting actions shall result in exactly one effective change (FRD §7.2 case 22; NFR-DAT-008).
- **SRS-ERR-008 (Confirmed):** Ineligible completion attempts shall be rejected with the outstanding-requirement list and no completion record (FRD §7.2 cases 16, 23).
- **SRS-ERR-009 (Confirmed):** Configuration-missing conditions shall surface explicitly and never as a compliant result (FRD §7.2 case 27; NFR-DAT-009).
- **SRS-ERR-010 (Confirmed):** Attempts to edit or delete audit or history records shall be refused (FRD §7.2 case 30; NFR-DAT-005).

### 11.3 Consistency and Non-Disclosure

- **SRS-ERR-011 (Confirmed):** Every rejected operation shall leave data unchanged (no partial writes) and shall return an outcome in one of the taxonomy categories (FRD §7.3).
- **SRS-ERR-012 (Confirmed):** Technical failures shall never be presented as business validation results, and vice versa (FRD §7.3).
- **SRS-ERR-013 (Confirmed):** Compliance evaluation errors shall mark affected results stale or unevaluated and shall never mark a requirement satisfied (FR-COMP-007).
- **SRS-ERR-014 (Confirmed):** Error messages shall not assert a policy that the source documents leave open (FRD §7.3).

## 12. Verification and Acceptance Approach

### 12.1 Verification Methods

Each requirement is verified by one or more of the following methods. No test is executed as part of this documentation substage; this section defines how requirements will be verified later (Task 10; PRD §13.1).

| ID | Method | Description | Evidence |
|---|---|---|---|
| VM-01 | Inspection | Review of documents, configuration, or code to confirm a requirement is addressed. | Reviewer sign-off; configuration or code reference. |
| VM-02 | Unit test | Automated tests of individual components using JUnit. | Passing test results (when executed). |
| VM-03 | Integration test | Automated tests of module interaction and persistence using Spring Boot Test against a database. | Passing test results. |
| VM-04 | API test | Tests of API operations, input validation, and responses at the boundary. | Passing test results; request/response evidence. |
| VM-05 | Authorization test | Tests asserting allowed/denied/conditional outcomes for each role and scope, including direct-reference and parameter-manipulation attempts. | Passing test results covering FRD §4 cells. |
| VM-06 | Workflow / end-to-end test | Tests of the report lifecycle and the first major milestone using Playwright across the frontend and backend. | Passing end-to-end test results. |
| VM-07 | Manual verification / usability check | Human review of interface behavior, messaging, and role-appropriate navigation. | Reviewer observations/sign-off. |
| VM-08 | Security review | Review of authentication, authorization, secret handling, private storage, and export safety. | Review report; remediation status. |

### 12.2 Verification Mapping

| Requirement group | Primary verification methods |
|---|---|
| Authentication, authorization, scope (SRS-FR-001, SRS-FR-007; FRD §4) | VM-05, VM-04, VM-08 |
| Report lifecycle and first milestone (SRS-FR-003; SRS §9.1) | VM-06, VM-02, VM-03 |
| Compliance engine (SRS-FR-004; SRS §9.2) | VM-03, VM-04, VM-06 |
| Completion verification (SRS-FR-010; SRS §9.3) | VM-03, VM-05, VM-06 |
| Audit history (SRS-FR-005; SRS §7.8) | VM-03, VM-02, VM-01 |
| Documents and private storage (SRS-FR-008; SRS §7.1) | VM-04, VM-05, VM-08 |
| Notifications (SRS §7.3, FR-NOTIF-006/007) | VM-03, VM-02 |
| Reports and CSV exports (SRS §7.7, FR-EXPORT-004) | VM-04, VM-02 |
| Security NFRs (SRS §7.1) | VM-08, VM-05, VM-02 |
| Performance NFRs (SRS §7.2) | VM-04, VM-03 (once targets are approved and a load approach is defined) |
| Reliability and data integrity (SRS §7.3, §7.7) | VM-03, VM-02 |
| Usability and accessibility (SRS §7.4) | VM-07 |
| Maintainability and compatibility (SRS §7.5, §7.6) | VM-01, VM-02 |
| Privacy and data protection (SRS §7.9) | VM-05, VM-08 |

### 12.3 Evidence and Acceptance

- **SRS-VER-001 (Confirmed):** Every functional requirement in the FRD has acceptance criteria; the SRS defers to those criteria as the functional acceptance basis (FRD §6).
- **SRS-VER-002 (Confirmed):** Requirements whose definitive verification depends on an open decision (OD-03, OD-04, OD-05, OD-07, OD-08, OD-10, OD-11) shall be tested against their interim behavior, and their final tests shall be asserted after the decision resolves (FRD §8.2).
- **SRS-VER-003 (Confirmed):** The future Test Plan and Test Cases shall be derivable from the FRD (functional), this SRS (system and quality), and the traceability in SRS §13.
- **SRS-VER-004 (Constraint):** No test, security review, or manual check shall be described as passed until it has actually been executed in a later stage; this document reports verification approaches only.

## 13. Requirements Traceability

### 13.1 Traceability Model

Traceability links the PRD (product goals and scope) to the FRD (detailed functional requirements) to this SRS (system-level and non-functional requirements) and to verification methods. Many-to-many relationships are used where appropriate; the SRS does not restate each FRD requirement.

```
PRD (goals, scope, modules)
      |
      v
FRD (104 functional requirements, permissions, state model, edge cases)
      |
      v
SRS (system-level + non-functional requirements, interfaces, constraints)
      |
      v
Verification methods (VM-01 ... VM-08) -> future Test Plan
```

### 13.2 PRD-to-SRS Traceability Matrix

| # | MVP module | PRD reference | FRD requirements | SRS sections | Verification methods | Related open decisions |
|---|---|---|---|---|---|---|
| 1 | Authentication and Role-Based Access | PRD §5.1, §7.1.1, §10, §15.3 | FR-AUTH-001 … FR-AUTH-007 | SRS §5.1, §6.3, §7.1, §10, §11 | VM-05, VM-04, VM-08 | OD-15, OD-01 |
| 2 | User Management | PRD §6.3, §7.1.2, §10 | FR-USER-001 … FR-USER-008 | SRS §5.2, §6.2, §6.3, §10 | VM-04, VM-05, VM-03 | OD-01, OD-15 |
| 3 | Internship Management | PRD §6.1, §7.1.3, §8, §18.1 | FR-INT-001 … FR-INT-008 | SRS §8, §9.4, §10 | VM-03, VM-04, VM-05 | OD-12, OD-13, AS-01 |
| 4 | Cohort Management | PRD §6.3, §7.1.4, §8, §18.1 | FR-COHORT-001 … FR-COHORT-007 | SRS §8, §9.2, §10 | VM-03, VM-04, VM-05 | OD-03, OD-10, OD-14, AS-02, AS-03 |
| 5 | Document Management | PRD §7.1.5, §8 step 3–4, §11.2 | FR-DOC-001 … FR-DOC-008 | SRS §5.2, §7.1, §7.9, §8 | VM-04, VM-05, VM-08 | OD-05, OD-16 |
| 6 | Weekly Reports and Work Logs | PRD §6.1, §7.1.6, §8 step 5/7, §9 | FR-REPORT-001 … FR-REPORT-010 | SRS §7.3, §7.7, §9.1, §11 | VM-06, VM-02, VM-03 | OD-10, OD-02 |
| 7 | Faculty Review Workflow | PRD §6.2, §7.1.7, §9, §11.1 | FR-REVIEW-001 … FR-REVIEW-010 | SRS §7.4, §9.1, §10 | VM-06, VM-05, VM-03 | OD-02, OD-10, PR-01 |
| 8 | Compliance Engine | PRD §7.1.8, §9, §11.1, §18.1 | FR-COMP-001 … FR-COMP-010 | SRS §7.7, §8, §9.2 | VM-03, VM-04, VM-06 | OD-03, OD-04, OD-05 |
| 9 | Role-Specific Dashboards | PRD §6.1–§6.3, §7.1.9, §10 | FR-DASH-001 … FR-DASH-007 | SRS §5.1, §7.4, §10 | VM-06, VM-05, VM-07 | PR-01, OD-01, OD-10 |
| 10 | Notifications | PRD §7.1.10, §11.2, §9 | FR-NOTIF-001 … FR-NOTIF-007 | SRS §5.2, §7.3, §7.9 | VM-03, VM-02, VM-04 | OD-09, OD-10, OD-11 |
| 11 | Completion Verification | PRD §6.2, §7.1.11, §8 step 9, §11.1, §15.1 | FR-COMPLETE-001 … FR-COMPLETE-008 | SRS §6.3, §7.7, §9.3 | VM-03, VM-05, VM-06 | OD-08, OD-03 |
| 12 | Audit History | PRD §7.1.12, §11.1, §18.2 | FR-AUDIT-001 … FR-AUDIT-006 | SRS §7.8, §8, §11 | VM-03, VM-02, VM-08 | OD-06, OD-11 |
| 13 | Reports and CSV Exports | PRD §7.1.13, §10, §15.3, §16 | FR-EXPORT-001 … FR-EXPORT-008 | SRS §5.2, §7.7, §10 | VM-04, VM-02, VM-05 | OD-07, OD-06, OD-01 |
| — | First major milestone | PRD §9, §9.1, §15.1 | FR-REPORT-004/008, FR-REVIEW-003/004, FR-COMP-004/007/010, FR-NOTIF-001 | SRS §3.2, §9.1 | VM-06 | OD-02, OD-04, OD-10 |

### 13.3 Non-Functional Traceability

| SRS requirement group | Source | Verification |
|---|---|---|
| SRS §7.1 Security (NFR-SEC-001 … NFR-SEC-010) | PRD §12, §18.3; FR-AUTH, FR-DOC-007, FR-EXPORT-004; OD-15 | VM-05, VM-08, VM-04 |
| SRS §7.2 Performance (NFR-PERF-001 … NFR-PERF-005) | PRD §12, §18.3 | VM-04, VM-03 (after targets approved) |
| SRS §7.3 Reliability and Availability (NFR-REL-001 … NFR-REL-008) | PRD §12, §18.3; FR-NOTIF-006, FR-COMP-007, FR-REPORT-006 | VM-03, VM-02 |
| SRS §7.4 Usability and Accessibility (NFR-UX-001 … NFR-UX-006) | PRD §12; FR-AUTH-001, FR-DASH-001/002, FRD §7.3 | VM-07 |
| SRS §7.5 Maintainability (NFR-MNT-001 … NFR-MNT-006) | PRD §11.2, §12, §13.1, §18.3 | VM-01, VM-02 |
| SRS §7.6 Compatibility (NFR-CMP-001 … NFR-CMP-003) | PRD §11.2, §12, §18.3 | VM-01, VM-07 |
| SRS §7.7 Data Integrity (NFR-DAT-001 … NFR-DAT-009) | PRD §12; FR-INT-008, FR-REPORT-006, FR-USER-005, FR-COMP-007 | VM-03, VM-02 |
| SRS §7.8 Auditability and Traceability (NFR-AUD-001 … NFR-AUD-007) | PRD §7.1.12; FR-AUDIT-001 … FR-AUDIT-006; OD-06, OD-11 | VM-03, VM-02, VM-08 |
| SRS §7.9 Privacy and Data Protection (NFR-PRV-001 … NFR-PRV-004) | PRD §7.1.13, §12, §18.3; FR-DOC-007, FR-EXPORT-003, FR-NOTIF-003 | VM-05, VM-08, VM-04 |

### 13.4 Coverage and Validation Checks

| Check | Result | Notes |
|---|---|---|
| SRS exists at the expected path | Pass | `docs/requirements/SRS.md` |
| All 13 MVP modules represented | Pass | SRS §3.2, §6.2, §13.2 cover modules 1–13. |
| Functional requirements traceable to the FRD | Pass | Every module row in SRS §13.2 cites the FRD requirement range; system-level requirements cite FRD IDs (SRS §6.3). |
| FRD requirement identifiers preserved and unmodified | Pass | 104 FR headings in FRD; SRS cites identifiers without redefining them. |
| Open decision identifiers resolve | Pass | OD-01 … OD-16, AS-01 … AS-06, PR-01 … PR-08 carried from FRD §9; none invented. |
| Workflow states and roles consistent with the FRD | Pass | SRS §9.1, §10.2 use the five report states and the three MVP roles. |
| Post-MVP functionality separated | Pass | SRS §4.3; SRS-FR-002. |
| No unsupported requirement introduced as approved | Pass with noted proposals | Proposed items are labeled (SRS §7.2.1, §7.6, SRS-AUTH-012, SRS-WF-013/015/022); no open decision is resolved. |
| PRD and FRD unchanged | Pass | Verified by Git comparison in the Stage 1.3 review; only `docs/requirements/SRS.md` was created. |
| Internal references resolve | Pass | SRS §x.y references, FRD references, and PRD references validated programmatically (see the Stage 1.3 report). |
| Tables and headings structurally valid | Pass | Table row widths, heading levels, and the table of contents validated programmatically. |
| No encoding or whitespace defects | Pass | UTF-8 without BOM; LF line endings; no tabs or trailing whitespace. |

## 14. Assumptions, Dependencies, Constraints, and Open Decisions

This section carries forward the unresolved matters from the PRD and FRD. It does not resolve any of them.

### 14.1 Assumptions

The FRD assumptions apply and require confirmation (FRD §9.3). Product-level planning assumptions from PRD §14.4 also apply.

| ID | Assumption | SRS relevance | Status |
|---|---|---|---|
| AS-01 | A student has at most one active internship in the MVP. | Data model (SRS §8.1), internship module. | Assumption — confirm |
| AS-02 | A student belongs to exactly one cohort at a time. | Data model (SRS §8.1), scope resolution. | Assumption — confirm |
| AS-03 | A cohort has one primary Faculty Coordinator, with additional assignments permitted. | Scope model (SRS §10.2). | Assumption — confirm |
| AS-04 | Each user holds a single role in the MVP. | Data model (SRS §8.1), authorization. | Assumption — confirm |
| AS-05 | Accounts are created by an administrator; no self-registration. | Authentication (SRS §7.1), user management. | Assumption — confirm |
| AS-06 | No cap on correction/resubmission cycles per report. | Report lifecycle (SRS §9.1). | Assumption — confirm |

Planning assumptions from PRD §14.4 (demonstration environment, representative data, single-department scope, faculty review without a committed participant, tool/hosting availability, optional functionality kept out of the MVP) also apply.

### 14.2 Dependencies

| Dependency | Relevance | Status |
|---|---|---|
| PostgreSQL instance | Structured persistence (SRS §5.2, §8). | Required; configuration driving deployment is open |
| Private object storage | Document storage and retrieval (SRS §5.2, §7.1). | Required; provider to be determined |
| Email delivery service | Email notifications (SRS §5.2, §7.3). | Required; provider and retry policy open (OD-09) |
| In-application notification functionality | In-app notifications (SRS §5.2). | Required |
| Frontend hosting (Vercel) and backend host | Deployment and demonstration (PRD §11.2). | Direction confirmed; configuration open |
| Managed PostgreSQL deployment | Database hosting. | To be selected |
| Test frameworks (JUnit, Spring Boot Test, Playwright) | Verification (SRS §12). | Selected |
| Security Requirements and other Stage 1 documents | Resolve deferred mechanics and targets. | Pending |

### 14.3 Constraints

| Constraint | Statement | Source |
|---|---|---|
| Architecture | Modular monolith; no microservices. | PRD §5.2, §14.1 |
| Technology stack | Established stack in SRS §3.4 is not to be silently changed. | PRD §11.2 |
| Timeline | 12-week planned duration. | PRD §14.1 |
| Deployment scope | One college department or cohort. | PRD §11.1 |
| Feature priority | Work logs over attendance; attendance and Company Mentor optional. | PRD §11.1, §14.1 |
| Release focus | First major workflow before secondary modules. | PRD §9.2, §15.1 |
| Scope preservation | All 13 MVP modules remain in scope. | PRD §7.3 |
| No invented policies | Open policies shall not be committed to a value. | PRD §12, §18; FRD §2.4 |
| No standards claims | No formal standard or legal compliance is claimed without an established, verified basis. | SRS §2.1, §7.1, §7.9 |

### 14.4 Open Decisions

The FRD's open decisions are carried forward unchanged. The table records each decision's relevance to this SRS and the downstream work it affects. None is resolved here (FRD §9.1, §9.2).

| ID | Topic | SRS relevance | Blocks | Recommended stage (FRD) |
|---|---|---|---|---|
| OD-01 | Role and permission boundaries | SRS §10; SRS-AUTH-009 | Authorization design, API access rules | Security Requirements + SRS |
| OD-02 | Approved-report modification, reopening, rejection | SRS §9.1 (SRS-WF-006) | Report module design, state model | Architecture / Database Design |
| OD-03 | Requirement ownership and change propagation | SRS §8.1, §9.2 (SRS-WF-014) | Compliance design, DB schema, audit rules | Architecture / Database Design |
| OD-04 | Compliance statuses and calculation rules | SRS §9.2 (SRS-WF-013), §7.7 | Compliance engine, dashboards, completion | SRS + Database Design |
| OD-05 | Document review requirements and effects | SRS §7.1, §9.2 (SRS-WF-015) | Document module, compliance inputs | SRS |
| OD-06 | Audit event set and viewing permissions | SRS §7.8, §10 (SRS-AUTH-011) | Audit module, security, exports | SRS + Security Requirements |
| OD-07 | Exact MVP reports and CSV columns | SRS §7.2, §6.2, §12.2 | Reporting module, API Specification | SRS / API Specification |
| OD-08 | Completion verification authority and reversal | SRS §9.3, §10 (SRS-AUTH-010) | Completion module, permissions | Security Requirements / SRS |
| OD-09 | Notification triggers, timing, retries | SRS §5.2, §7.3 | Notification module | SRS |
| OD-10 | Deadline and late-submission policies | SRS §9.1, §9.4 (SRS-WF-008, SRS-WF-026) | Reports, notifications, compliance timeliness | SRS / product decision |
| OD-11 | Record and history retention | SRS §8.3 (SRS-DR-016), §7.8 | Data model, storage, legal posture | Database Design + Operations Guide |
| OD-12 | Internship field catalogue and lifecycle states | SRS §8.1 (SRS-DR-009) | Internship module, DB schema | Database Design / API Specification |
| OD-13 | Student–internship reassignment rules | SRS §8.1 (SRS-DR-009) | Internship/DB relationships | Database Design |
| OD-14 | Student cohort membership moves | SRS §8.1 (SRS-DR-009) | Cohort/DB relationships, compliance history | Database Design |
| OD-15 | Authentication mechanics | SRS §7.1 (NFR-SEC-009), §5.2 | Auth implementation, session handling | Security Requirements + SRS |
| OD-16 | Document file-type and size policy values | SRS §7.1, §7.2.1 | Upload validation configuration | Security Requirements / Deployment & Operations |

Non-functional targets that remain open (PRD §18.3): performance, availability/SLA, accessibility conformance level, security/privacy policy, backup and recovery, and code-quality/coverage targets. These are recorded as open in SRS §7 and §14.5 and must not be treated as commitments.

### 14.5 Items Requiring Human Approval

The following items require human review and cannot be finalized by this SRS without a decision. They are recorded rather than resolved.

- Confirmation of the FRD proposed permission rules PR-01 through PR-08 (FRD §4.4; SRS-AUTH-012).
- Confirmation of the FRD assumptions AS-01 through AS-06 (SRS §14.1).
- Resolution of open decisions OD-01 through OD-16 (SRS §14.4), in the recommended stages.
- Approval (or rejection) of this SRS's proposed targets: performance (SRS §7.2.1), compatibility baseline (SRS §7.6), export non-blocking behavior (NFR-PERF-005), and the interim/proposed rulings marked in SRS §9 and SRS §10.
- Approval of the document set as a whole (PRD, FRD, SRS) before it is treated as an approved baseline.

## 15. Appendices

### 15.1 Appendix A: FRD Requirement Inventory

The FRD defines 104 functional requirements across 13 modules. This SRS references, but does not redefine, them. The inventory below is a range summary; the authoritative definitions and acceptance criteria are in FRD §6.

| Module | FRD section | Requirement range | Count |
|---|---|---|---|
| Authentication and Role-Based Access | FRD §6.1 | FR-AUTH-001 … FR-AUTH-007 | 7 |
| User Management | FRD §6.2 | FR-USER-001 … FR-USER-008 | 8 |
| Internship Management | FRD §6.3 | FR-INT-001 … FR-INT-008 | 8 |
| Cohort Management | FRD §6.4 | FR-COHORT-001 … FR-COHORT-007 | 7 |
| Document Management | FRD §6.5 | FR-DOC-001 … FR-DOC-008 | 8 |
| Weekly Reports and Work Logs | FRD §6.6 | FR-REPORT-001 … FR-REPORT-010 | 10 |
| Faculty Review Workflow | FRD §6.7 | FR-REVIEW-001 … FR-REVIEW-010 | 10 |
| Compliance Engine | FRD §6.8 | FR-COMP-001 … FR-COMP-010 | 10 |
| Role-Specific Dashboards | FRD §6.9 | FR-DASH-001 … FR-DASH-007 | 7 |
| Notifications | FRD §6.10 | FR-NOTIF-001 … FR-NOTIF-007 | 7 |
| Completion Verification | FRD §6.11 | FR-COMPLETE-001 … FR-COMPLETE-008 | 8 |
| Audit History | FRD §6.12 | FR-AUDIT-001 … FR-AUDIT-006 | 6 |
| Reports and CSV Exports | FRD §6.13 | FR-EXPORT-001 … FR-EXPORT-008 | 8 |
| **Total** | | | **104** |

### 15.2 Appendix B: Document Review Checklist

| # | Check | Status |
|---|---|---|
| 1 | SRS exists at `docs/requirements/SRS.md` | Done |
| 2 | Document-control information present | Done |
| 3 | Logical, navigable structure with consistent headings | Done |
| 4 | Table of contents present and accurate | Done |
| 5 | All 13 MVP modules represented and traceable | Done |
| 6 | Functional requirements traceable to FRD identifiers | Done |
| 7 | Non-functional requirements explicit and categorized | Done |
| 8 | System context, boundaries, and interfaces described | Done |
| 9 | Assumptions, constraints, dependencies, and open decisions recorded with real identifiers | Done |
| 10 | Verification approaches defined; no unexecuted test reported as passed | Done |
| 11 | Open decisions preserved and not silently resolved | Done |
| 12 | No invented policies (percentages, durations, SLAs, file limits, retention) | Done — all isolated as Proposed/Open |
| 13 | PRD and FRD unmodified | Done — verified by Git comparison |
| 14 | Markdown tables, headings, and internal links checked | Done — validated programmatically |
| 15 | Encoding, line endings, and whitespace checked | Done — UTF-8 without BOM, LF, no tabs/trailing whitespace |
| 16 | Git state inspected and reported | Done — see the Stage 1.3 review report |

### 15.3 Appendix C: Change Control and Version History

| Version | Date | Status | Change |
|---|---|---|---|
| 1.0 | 2026-10-14 | Draft — Pending Review | Initial SRS created for Stage 1.3, derived from PRD v1.0 and FRD v1.0. |

Change control: this SRS is a draft. Changes to the PRD or FRD that affect system-level or quality requirements must be reflected here in a new version. Open decisions carried in SRS §14.4 must be reflected in this document when resolved in their recommended stages.

