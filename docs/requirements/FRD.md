# InternFlow Functional Requirements Document (FRD)

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | InternFlow Functional Requirements Document (FRD) |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-FRD-001 |
| Version | 1.0 |
| Status | Draft — Pending Review (not approved) |
| Last Updated | 2026-10-14 |
| Relationship to PRD | Derived from and traceable to InternFlow PRD v1.0 (`docs/requirements/PRD.md`, baseline commit `9f91303`). The PRD remains the authoritative product-scope baseline; this FRD does not modify it. |
| Purpose | Translate the PRD's product scope into detailed, testable descriptions of system behavior: what the system must do, who may perform each action, when actions are allowed, what results follow, and how invalid or exceptional situations are handled. |
| Intended Audience | Project reviewer/stakeholder, the development team implementing the MVP, authors of the SRS, System Architecture Document, Database Design Document, API Specification, Security Requirements, and Test Plan. |

**Review note:** This document is a draft pending review. It must not be treated as approved. Decisions marked "Open decision" (OD-xx) require resolution in the stage indicated in Section 9 before the affected design is finalized.

## Table of Contents

- [1. Document Control](#1-document-control)
- [2. Purpose, Scope, and Definitions](#2-purpose-scope-and-definitions)
- [3. Actors and Roles](#3-actors-and-roles)
- [4. Role-Permission Matrix](#4-role-permission-matrix)
- [5. Report Lifecycle State Model](#5-report-lifecycle-state-model)
- [6. Functional Requirements](#6-functional-requirements)
  - [6.1 Module 1 — Authentication and Role-Based Access](#61-module-1--authentication-and-role-based-access)
  - [6.2 Module 2 — User Management](#62-module-2--user-management)
  - [6.3 Module 3 — Internship Management](#63-module-3--internship-management)
  - [6.4 Module 4 — Cohort Management](#64-module-4--cohort-management)
  - [6.5 Module 5 — Document Management](#65-module-5--document-management)
  - [6.6 Module 6 — Weekly Reports and Work Logs](#66-module-6--weekly-reports-and-work-logs)
  - [6.7 Module 7 — Faculty Review Workflow](#67-module-7--faculty-review-workflow)
  - [6.8 Module 8 — Compliance Engine](#68-module-8--compliance-engine)
  - [6.9 Module 9 — Role-Specific Dashboards](#69-module-9--role-specific-dashboards)
  - [6.10 Module 10 — Notifications](#610-module-10--notifications)
  - [6.11 Module 11 — Completion Verification](#611-module-11--completion-verification)
  - [6.12 Module 12 — Audit History](#612-module-12--audit-history)
  - [6.13 Module 13 — Reports and CSV Exports](#613-module-13--reports-and-csv-exports)
- [7. Validation, Errors, and Edge Cases](#7-validation-errors-and-edge-cases)
- [8. Traceability Matrix](#8-traceability-matrix)
- [9. Open Decisions and Dependencies](#9-open-decisions-and-dependencies)
- [10. Stage 1.2 Acceptance Self-Check](#10-stage-12-acceptance-self-check)

## 2. Purpose, Scope, and Definitions

### 2.1 Purpose

The PRD defines what InternFlow is, why it exists, and which modules belong to the MVP. The FRD defines the functional behavior those modules require:

- Precise system behavior per requirement, stated so it can be independently tested.
- The actor responsible for each action and the preconditions under which it is allowed.
- Postconditions and state changes produced by each action.
- Validation, authorization, and error behavior for invalid or exceptional situations.
- A single report state model used consistently across all modules.
- Traceability from every FRD requirement back to the PRD.
- An explicit register of decisions the source material does not settle.

### 2.2 Scope

**In scope (MVP):** The 13 essential MVP modules defined in PRD §7.1, for the established initial deployment target of one college department or internship cohort:

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

Also in scope: the report lifecycle state model, validation and edge-case behavior, the PRD-to-FRD traceability matrix, and the open-decision register.

**Outside the MVP (post-MVP, per PRD §7.2 and §16):**

- Company Mentor evaluations and any Company Mentor permissions or workflows.
- Attendance tracking (work logs take priority over attendance).
- Advanced analytics and PDF reports.
- Notification channels beyond email and in-app.
- Multi-department or multi-college support.
- External college-system integrations.

**Outside this document:**

- Application code of any kind (this substage is documentation only).
- Non-functional targets (performance, availability, accessibility, backup), which belong to the SRS (PRD §18.3).
- Security implementation details such as token formats, password policy, encryption, and session lifetimes, which belong to Security Requirements and the SRS (PRD §18.3, §11.2).

### 2.3 Relationship to the PRD and the Future SRS

- **PRD (v1.0):** Authoritative for product vision, goals, scope, personas, module list, established decisions, and open decisions. The FRD never contradicts the PRD; where the PRD leaves a rule open, the FRD records an open decision instead of inventing a rule.
- **FRD (this document):** Authoritative for functional behavior, role permissions, state transitions, validation behavior, and functional acceptance criteria until superseded by the SRS.
- **SRS (future):** Will refine these functional requirements into software-level requirements, add non-functional requirements, and absorb decisions deferred to it (authentication mechanics, notification retry policy, configurable file limits, measurable targets). Each FRD requirement will be traceable forward into the SRS and the Requirements Traceability Matrix.
- **PRD section references:** References of the form "PRD §7.1.N" denote the Nth module listed under PRD §7.1 (for example, §7.1.11 = Completion Verification). All other references (§5.1, §6.3, §7.1, §8, §9, §10, §11, §12, §14, §15, §18) point to PRD sections directly.

### 2.4 Requirement Status Conventions

Every requirement and rule in this document carries one of the following status labels:

| Label | Meaning |
|---|---|
| **Confirmed** | Directly established by the PRD (with the PRD section cited). |
| **Derived** | Follows necessarily from an established PRD decision or principle; not restated verbatim in the PRD. Flagged for reviewer confirmation; it does not conflict with the PRD. |
| **Proposed** | A rule the FRD proposes because the source material requires *some* testable behavior, but the PRD does not settle it. Requires explicit review before it is treated as confirmed. |
| **Open decision (OD-xx)** | Genuinely unresolved. The FRD records the question and its implications in Section 9 rather than guessing. Where an open decision blocks testability, the affected requirement states the interim behavior and cites the decision. |
| **Assumption (AS-xx)** | A planning assumption made to keep requirements testable. Not an approved decision; listed in Section 9.3. |

### 2.5 Glossary of FRD-Specific Terms

Terms defined in PRD §17 (Cohort, Work Log, Weekly Report, Compliance, Faculty Coordinator, Completion Verification, Audit History, Correction Request, RBAC, MVP) carry their PRD meaning. The following terms are introduced by this FRD:

| Term | Definition |
|---|---|
| Report | A Weekly Report record and its associated work-log content, tracked through the lifecycle states in Section 5. |
| Report state | The lifecycle status of a report (Draft, Submitted, Correction Requested, Resubmitted, Approved) as defined in Section 5.1. |
| Review action | A Faculty Coordinator decision performed on a report in a reviewable state: request corrections or approve. Review actions cause state transitions; they are not states themselves. |
| Reporting period | The week for which a report is written. One report exists per internship per reporting period (FR-REPORT-006). |
| Requirement (internship requirement) | A defined obligation configured for a cohort and applied to its internships (for example, submitting required documents or producing approved weekly reports for required periods). |
| Mandatory requirement | A requirement whose satisfaction is required for compliance and for completion verification (PRD §11.1). |
| Compliance status | The system's computed evaluation of an internship against its applicable requirements (PRD §7.1.8). |
| Assigned cohort | A cohort to which a Faculty Coordinator has been assigned by an administrator. |
| Assigned student | A student who is a member of a cohort assigned to the requesting Faculty Coordinator. |
| Authorized actor | A user whose role and assignment permit a specific restricted action under this FRD. |
| Operation | A single protected system action (for example, approve report, create user, export CSV). |
| CSV | Comma-separated values: the export file format used by Module 13 (Reports and CSV Exports). |
| Account activation | The step, distinct from account creation, that makes a created account eligible to sign in. Its mechanics are an open decision (OD-15). |

## 3. Actors and Roles

Three primary roles exist in the MVP (PRD §6). Company Mentor is post-MVP (PRD §6.4) and has no permissions, screens, or requirements in this document.

### 3.1 Student

- **Responsibilities:** Register internship details; submit required documents; create, edit, and submit weekly reports and work logs; respond to correction requests by resubmitting; review own compliance status and deadlines; provide accurate information within applicable deadlines (PRD §6.1).
- **Scope:** Acts only on own records: own internship, own reports, own documents, own compliance status, own notifications.
- **Cannot:** Review or approve any report, manage users, configure cohorts or requirements, verify completion, or view other students' records (see Section 4).

### 3.2 Faculty Coordinator

- **Responsibilities:** Review internship details and documents of assigned students; review weekly reports and work logs; approve reports or request corrections with feedback; monitor progress, compliance, and deadlines of assigned students; verify completion when mandatory requirements are satisfied (PRD §6.2, §8).
- **Established authority:** Faculty Coordinators approve weekly reports (PRD §11.1) and authorize completion (PRD §8 step 9).
- **Scope:** Acts on students within cohorts assigned to that coordinator. Access outside assigned cohorts is governed by Section 4.3 (item 2), which records it as a proposed rule pending review.
- **Cannot:** Manage user accounts or roles, create cohorts, configure internship requirements, edit students' report content, or view records outside assigned scope.

### 3.3 College Administrator

- **Responsibilities:** User and role administration; cohort management; internship requirement configuration; high-level monitoring and reporting (PRD §6.3).
- **Scope:** Deployment-wide (the initial single department or cohort scope).
- **Cannot by default:** Review or approve weekly reports, verify completion, or edit student report content. Administrative status does not confer review authority; see Section 4.3 (items 7, 8, 12) and Section 4.4.
- **Open:** Exact boundaries of administrator access to sensitive student records and requirement-change authority are recorded in OD-01 and OD-03.

### 3.4 Role Relationships

- A College Administrator assigns roles to users and assigns Faculty Coordinators to cohorts.
- A Faculty Coordinator's authority applies to students of assigned cohorts only (cohort-scoped authority).
- A Student's records belong to exactly one internship, which belongs to exactly one cohort (Section 6.3, 6.4; AS-01, AS-02).
- Users act under a single assigned role in the MVP (AS-04).

## 4. Role-Permission Matrix

### 4.1 Permission Legend

| Value | Meaning |
|---|---|
| **Allowed** | The role may perform the operation, subject to the stated preconditions. Confirmed by the PRD unless marked otherwise in the Notes column. |
| **Denied** | The role may not perform the operation. Attempts result in an authorization failure (Section 7.1). |
| **Conditional** | The role may perform the operation only when stated conditions hold (for example, only for assigned cohorts, only on own records). |
| **Proposed** | Rule derived from PRD principles but not restated in the PRD; pending reviewer confirmation (listed in Section 4.4). |
| **Open decision** | The PRD does not settle the permission; see Section 9. Do not implement a default behavior before the decision is resolved, except where an interim behavior is explicitly stated. |

Notes:

- Permission values below apply to MVP operations defined in Section 6.
- The value **Open decision** denotes a permission that cannot be granted or denied until a policy decision is made; it is equivalent to a "requires policy decision" outcome and always cites an OD-xx entry in Section 9.
- Administrative status alone never grants review, approval, or completion-verification authority (Section 4.4, PR-05).
- All denied or conditional checks are enforced server-side regardless of UI visibility (FR-AUTH-003, FR-AUTH-004).

### 4.2 Matrix by Module

#### 4.2.1 Module 1 — Authentication and Role-Based Access

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Sign in with credentials | Allowed | Allowed | Allowed | FR-AUTH-001; account must be activated (activation mechanics OD-15) |
| Sign out | Allowed | Allowed | Allowed | FR-AUTH-002 |
| Maintain an authenticated session | Allowed | Allowed | Allowed | Session mechanics deferred to Security Requirements (OD-15) |
| Access pages/operations of own role | Allowed | Allowed | Allowed | FR-AUTH-004 |
| Access pages/operations of another role | Denied | Denied | Denied | FR-AUTH-003 |
| Sign in with a deactivated account | Denied | Denied | Denied | FR-AUTH-005 |
| Retry after failed sign-in | Allowed | Allowed | Allowed | Lockout policy is OD-15 (FR-AUTH-006) |

#### 4.2.2 Module 2 — User Management

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Create user | Denied | Denied | Allowed | PRD §6.3, §7.1.2; creation is separate from activation and sign-in eligibility (FR-USER-001, OD-15) |
| Assign or change a user's role | Denied | Denied | Allowed | Changing own role: Open decision (OD-01) |
| View own profile | Allowed | Allowed | Allowed | FR-USER-003 |
| Edit own non-identity profile fields | Allowed | Allowed | Allowed | Proposed (PR-06) |
| Edit another user's directory record | Denied | Denied | Allowed | FR-USER-004 |
| List/search user accounts | Denied | Conditional (assigned cohort students) | Allowed | Proposed for faculty (PR-04) |
| Deactivate an account | Denied | Denied | Conditional | Admin capability confirmed (§6.3); separation-of-duties constraints OD-01 |
| Delete a user account | Denied | Denied | Open decision | Deletion not established by PRD; records must remain attributable (FR-USER-005) |
| View audit history of user changes | Denied | Denied | Open decision | OD-06 |

#### 4.2.3 Module 3 — Internship Management

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Create (register) an internship record | Allowed (own) | Denied | Open decision | PRD §8 step 2 assigns registration to the student; administrative backfill not settled (OD-01) |
| Edit internship details | Conditional (own) | Denied | Open decision | Edit boundaries after faculty initial review: OD-12 |
| View internship record | Conditional (own) | Conditional (assigned cohort) | Allowed (deployment scope) | FR-INT-006 |
| Associate student with internship | Allowed (own) | Denied | Open decision | OD-13 (reassignment rules) |
| Reassign/transfer a student to another internship | Denied | Denied | Open decision | OD-13 |
| Change internship lifecycle state | Denied | Denied | Open decision | State set itself is OD-12; completion transition handled by Module 11 |
| Delete an internship record | Denied | Denied | Denied | Proposed (PR-08): no deletion established by PRD |
| View internship compliance status | Conditional (own) | Conditional (assigned cohort) | Allowed | See Module 8 |

#### 4.2.4 Module 4 — Cohort Management

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Create a cohort | Denied | Denied | Allowed | PRD §8 step 1, §6.3 |
| Update cohort configuration | Denied | Denied | Allowed | Requirement-related scope limited by OD-03 |
| Assign students to a cohort | Denied | Denied | Allowed | Proposed for admin; faculty participation in membership is OD-01 |
| Move a student between cohorts | Denied | Denied | Open decision | OD-14 (effects on existing records) |
| Assign a faculty coordinator to a cohort | Denied | Denied | Allowed | PRD §6.3, §7.1.4 |
| Configure cohort deadlines | Denied | Denied | Allowed (values unsettled) | Admin may configure; deadline policy values are OD-10 |
| Create/change cohort internship requirements | Denied | Denied | Conditional | Admin responsibility confirmed (§6.3); scope and change propagation OD-03 |
| View cohort details | Conditional (own cohort) | Conditional (assigned cohorts) | Allowed | FR-COHORT-006 |

#### 4.2.5 Module 5 — Document Management

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View own submitted documents | Allowed | — | — | FR-DOC-007 |
| View documents of assigned students | Denied | Conditional (assigned cohort) | Conditional | Admin access to sensitive content: OD-01 |
| Upload/submit a document | Allowed (own internship) | Denied | Denied | PRD §6.1 |
| Replace a submitted document | Conditional (own) | Denied | Denied | Post-review replacement: OD-05 |
| Delete a document | Denied | Denied | Denied | Proposed (PR-08) |
| Review documents | — | Conditional (assigned cohort) | Denied | PRD §8 step 4; review outcomes are OD-05 |
| Define required document types | Denied | Denied | Conditional | Cohort requirement configuration, OD-03 |

#### 4.2.6 Module 6 — Weekly Reports and Work Logs

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Create a report draft | Allowed (own) | Denied | Denied | FR-REPORT-001 |
| Edit a draft | Allowed (own) | Denied | Denied | FR-REPORT-003 |
| Submit a draft | Allowed (own) | Denied | Denied | PRD §9.1 |
| Revise a `CORRECTION_REQUESTED` report before resubmitting | Allowed (own) | Denied | Denied | FR-REPORT-009; the state remains `CORRECTION_REQUESTED` until Resubmit (FR-REPORT-008) |
| Edit a `SUBMITTED`, `RESUBMITTED`, or `APPROVED` report | Denied | Denied | Denied | FR-REPORT-009; for `RESUBMITTED`/`APPROVED` no edit path exists in the MVP |
| Resubmit after corrections | Allowed (own) | Denied | Denied | PRD §8 step 7 |
| View own reports | Allowed | Conditional (assigned cohort) | Conditional | Admin report visibility: OD-01 |
| View another student's report | Denied | Conditional (assigned cohort) | Conditional | Student cross-record access: Proposed Denied (PR-01) |
| Delete any report | Denied | Denied | Denied | Proposed (PR-08) |

#### 4.2.7 Module 7 — Faculty Review Workflow

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View reports pending review | Denied | Conditional (assigned cohort) | Denied | Admin review queue access: OD-01 |
| Request corrections | Denied | Conditional (assigned cohort) | Denied | PRD §9.1; only in reviewable states |
| Approve a report | Denied | Conditional (assigned cohort) | Denied | Confirmed: PRD §11.1 "Faculty Coordinators approve weekly reports" |
| Edit report content as reviewer | Denied | Denied | Denied | Proposed (PR-02): reviewers give feedback; they do not edit student content |
| Reopen or modify an approved report | Denied | Open decision | Open decision | OD-02 |
| Reject a report (terminal rejection) | — | Open decision | Open decision | Not established by PRD; would extend the state model (OD-02) |
| View review history of own report | Allowed | Allowed (assigned cohort) | Conditional | Student visibility of feedback: Proposed (PR-03) |

#### 4.2.8 Module 8 — Compliance Engine

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View own compliance status and missing requirements | Allowed | Conditional (assigned cohort) | Allowed | FR-COMP-008 |
| View cohort-level compliance overview | Denied | Conditional (assigned cohort) | Allowed | FR-DASH-003 |
| View compliance of students outside assigned cohort | Denied | Denied | Allowed (deployment scope) | Proposed for faculty: Denied (PR-01) |
| Change internship requirements | Denied | Denied | Conditional | Admin confirmed to configure (§6.3); OD-03 governs scope/propagation |
| Trigger manual compliance recalculation | Denied | Denied | Open decision | OD-04 (automatic recalculation on events is required: FR-COMP-007) |
| Change compliance results directly (bypassing evaluation) | Denied | Denied | Denied | Proposed (PR-07): compliance is computed, not edited |

#### 4.2.9 Module 9 — Role-Specific Dashboards

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View own dashboard | Allowed | Allowed | Allowed | FR-DASH-001/003/004 |
| View assigned students' status | Denied | Allowed (assigned cohort) | Allowed | FR-DASH-005 |
| View deployment-wide operational overview | Denied | Denied | Allowed | Within single-deployment scope |
| See records outside permitted scope | Denied | Denied | Denied | FR-DASH-005 |
| Perform dashboard-presented actions | Conditional (own records) | Conditional (assigned cohort) | Conditional (admin operations) | Action authorization identical to Section 4.2.1–4.2.13 |

#### 4.2.10 Module 10 — Notifications

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Receive in-app and email notifications for own events | Allowed | Allowed | Allowed | FR-NOTIF-001/004/005 |
| View own notification list and read status | Allowed | Allowed | Allowed | FR-NOTIF-007 |
| View another user's notifications | Denied | Denied | Denied | FR-NOTIF-002 |
| Configure notification channels or timing | Denied | Denied | Open decision | Channels fixed to email + in-app (PRD §11.2); timing OD-09 |
| Send arbitrary/unsolicited notifications to other users | Denied | Denied | Open decision | Free-form messaging not established by PRD (OD-09) |

#### 4.2.11 Module 11 — Completion Verification

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| Verify completion of an internship | Denied | Conditional (assigned cohort, all mandatory requirements satisfied) | Open decision | PRD §8 step 9 confirms Faculty Coordinator; admin authority OD-08 |
| Attempt verification with unmet requirements | Denied | Denied (rejected with reasons) | Denied | FR-COMPLETE-004 |
| View completion status | Allowed (own) | Conditional (assigned cohort) | Allowed | FR-COMPLETE-006 |
| View verification history | Allowed (own) | Allowed (assigned cohort) | Allowed | Proposed (PR-03) |
| Reverse or revoke a verified completion | Denied | Open decision | Open decision | OD-08 |

#### 4.2.12 Module 12 — Audit History

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View audit history | Denied | Open decision (proposal: assigned cohort records) | Open decision (proposal: deployment-wide) | OD-06; PRD §7.1.12 requires only that "authorized users" can review history |
| Edit audit events | Denied | Denied | Denied | Confirmed: PRD §7.1.12 |
| Delete audit events | Denied | Denied | Denied | Confirmed: PRD §7.1.12 |
| Export audit history | Denied | Open decision | Open decision | Candidate export, part of OD-06/OD-07 |

#### 4.2.13 Module 13 — Reports and CSV Exports

| Operation | Student | Faculty Coordinator | College Administrator | Notes |
|---|---|---|---|---|
| View MVP operational reports | Open decision (proposal: none, or own record summary only) | Conditional (assigned cohort data) | Allowed (deployment-wide) | OD-07 |
| Export report data to CSV | Open decision | Conditional (assigned cohort data) | Allowed | Confirmed principle: exports respect requester permissions (PRD §7.1.13) |
| Export data outside own scope | Denied | Denied | — | FR-EXPORT-003 |
| Request an export containing other users' data beyond permission | Denied | Denied | — | Scope silently limited or denied per FR-EXPORT-003 |

### 4.3 Investigated Permission Questions

The following questions were investigated as required for Stage 1.2. "Resolution" states the FRD position; "Classification" says how firm it is.

| # | Question | Resolution | Classification | Basis |
|---|---|---|---|---|
| 1 | Can a student access another student's records? | No. A student may only view and act on own internship, reports, documents, compliance status, and notifications. | Proposed (PR-01) | PRD §5.1 goal 5 (restrict information according to roles) and §7.1.13 (exports respect permissions). Confirm in Security Requirements. |
| 2 | Can a faculty coordinator access students outside their assigned cohort? | Default: no. Faculty access is scoped to cohorts assigned to them by an administrator. | Proposed (PR-01) | PRD §6.2 (faculty oversight role), §7.1.4 (cohorts and coordinator assignment), §11.1 (single-department scope). The PRD does not explicitly settle cross-cohort access; confirm at review. |
| 3 | Can an administrator create users and assign roles? | Yes — both user creation and role assignment are administrator capabilities. | Confirmed | PRD §6.3, §7.1.2 ("User and role administration"). Constraint: changing one's own role is unsettled (OD-01). |
| 4 | Can an administrator change internship requirements? | The administrator is responsible for requirement configuration, but the scope of configurable requirements and the effect of changes on existing internships are unsettled. | Confirmed responsibility; Open decision on scope/propagation (OD-03) | PRD §6.3 vs §18.1 ("which requirements are configurable... whether requirement changes affect existing internships"). |
| 5 | Can a student modify a submitted report? | No. After submission the report is read-only for the student. Following a correction request the report is returned to the student in state `CORRECTION_REQUESTED`, where the student may revise the content and then resubmit; the edit does not itself change the state, and the state advances to `RESUBMITTED` only on the Resubmit action (T4). | Confirmed | PRD §8 step 7 (student revises when corrections are requested), §9.1. |
| 6 | Can a student modify an approved report? | No. Approved is terminal for student modification in the MVP. Whether reopening exists is unsettled. | Confirmed default; Open decision on reopening (OD-02) | PRD §18.1 explicitly leaves modification/reopening of approved reports open. |
| 7 | Can a faculty coordinator modify an approved report? | No. Reviewers request corrections or approve; they never edit student report content, in any state. | Proposed (PR-02) for content editing; reopening OD-02 | PRD §6.2 (review, feedback, approve) does not grant content editing. |
| 8 | Can an approved report be reopened? | Not defined by the PRD. The MVP state model treats `APPROVED` as terminal; a reopening mechanism requires OD-02 before implementation. | Open decision (OD-02) | PRD §18.1. |
| 9 | Is compliance recalculated after a requirement changes? | Recalculation after report/document/workflow events is required (FR-COMP-007). Recalculation when a *requirement definition* changes for existing internships is undefined. | Open decision (OD-03/OD-04) | PRD §18.1 (requirement configuration and change propagation; completion and compliance rules). |
| 10 | Who may view or export sensitive student information? | Students: own records only (PR-01). Faculty: assigned cohort, including documents and reports (Proposed). Admin: deployment-wide operational data; access to full sensitive document content is unsettled (OD-01). All exports are limited to the requester's permitted scope (Confirmed, PRD §7.1.13). Data classification remains open (PRD §18.3). | Mixed — see column | PRD §6.3, §7.1.13, §18.1, §18.3. |
| 11 | Who may view audit history? | The PRD requires only that "authorized users" may review relevant history and that ordinary users cannot edit or delete events. Exact viewer roles are unsettled. Proposal: administrator deployment-wide; faculty assigned-cohort records; students no audit access. | Open decision (OD-06) with proposal | PRD §7.1.12 ("exact event list, retention rules, and detailed access permissions must be defined in the FRD and supporting technical documents"). The FRD proposes; reviewer confirms. |
| 12 | Who may verify completion? | A Faculty Coordinator assigned to the internship's cohort, and only when all mandatory requirements are satisfied. Students never. Administrator authority is unsettled. | Confirmed for faculty; Open decision for administrator (OD-08) | PRD §8 step 9, §6.2, §11.1. |

### 4.4 Proposed Permission Rules Requiring Review

These rules make the matrix testable but are **not** confirmed by the PRD. Each is listed here once; requirements cite the PR-xx identifier.

| ID | Proposed rule | Reason review is needed |
|---|---|---|
| PR-01 | Record access is strictly scoped: students → own records; faculty → assigned cohorts; admin → deployment scope (single department/cohort). | PRD establishes RBAC as a goal but does not enumerate data scopes. |
| PR-02 | Reviewers never edit student report content; they provide feedback via correction requests. | PRD grants review/approve, not content editing; explicit denial is a FRD proposal. |
| PR-03 | Students may view review feedback and review history on own reports, and view verification history of own internship. | PRD implies students act on feedback but does not state history visibility. |
| PR-04 | Faculty Coordinators may list/search students of assigned cohorts. | PRD requires faculty to monitor assigned students; listing UI is not specified. |
| PR-05 | Administrative status never implies review, approval, or completion-verification authority. | PRD separates admin responsibilities (§6.3) from review authority (§6.2, §11.1) but does not state an explicit prohibition. |
| PR-06 | Users may edit their own non-identity profile fields (for example, contact details); identity fields (email/username) are administrator-managed. | PRD does not specify profile self-service. |
| PR-07 | Compliance status is computed by the system and cannot be manually overridden or edited by any role. | Follows from PRD §7.1.8 (calculated status); explicit prohibition is a proposal. |
| PR-08 | No MVP operation deletes internships, reports, documents, or cohorts; deactivation/supersession replaces deletion where applicable. | PRD establishes preservation of history but does not define deletion policy. |

## 5. Report Lifecycle State Model

This section defines the single report state model used by Modules 6, 7, 8, 10, 11, and 12 throughout this FRD.

### 5.1 States

| State | Definition | Editable by student? | Reviewable by faculty? |
|---|---|---|---|
| `DRAFT` | Report created for a reporting period; not yet visible as a submission. | Yes | No |
| `SUBMITTED` | Student has submitted the report; awaiting faculty review. | No | Yes |
| `CORRECTION_REQUESTED` | Faculty has requested revisions and provided feedback; the report is returned to the student. | Yes — the owner may revise content; the state does not change on edit | No |
| `RESUBMITTED` | Student has resubmitted after corrections; awaiting faculty review again. | No | Yes |
| `APPROVED` | Faculty has approved the report. Terminal state in the MVP. | No | No |

Notes:

- `CORRECTION_REQUESTED` returns the record to the student, who may revise the report content while the report remains in that state. Revising content is an edit, not a transition: the state stays `CORRECTION_REQUESTED` until the student performs the Resubmit action (T4, Section 5.2), which captures the revised content with a resubmission timestamp and moves the report to `RESUBMITTED`. Editing permission in this state is therefore distinct from the state transition itself (Section 5.3).
- The conceptual workflow from the Stage 1.2 brief is preserved exactly: `Draft → Submitted → Correction Requested → Resubmitted → Approved`.
- No other states exist in the MVP. In particular, there is no Rejected, Withdrawn, or Reopened state; introducing one requires OD-02.
- Report content is revised in place; the review history (faculty feedback, action types, actors, timestamps, and submission/resubmission timestamps) is preserved per FR-REVIEW-006. Retention of prior report content versions is not established by the PRD and is governed by OD-11, consistent with the document-version rule in FR-DOC-004.

### 5.2 Supported Transitions

| # | Current state | Triggering action | Actor | Preconditions | Next state | Side effects | Validation failures | Compliance effect |
|---|---|---|---|---|---|---|---|---|
| T1 | `DRAFT` | Submit report | Student (owner) | Authenticated; record belongs to actor; internship exists; required report content present (FR-REPORT-002); no other report exists for the same internship and reporting period (one report per period; FR-REPORT-006) | `SUBMITTED` | Submission timestamp recorded; audit event `report.submitted`; notification to assigned Faculty Coordinator | Missing required content → business validation error, state unchanged; not owner → authorization failure; a report already exists for the period → business validation error (FR-REPORT-006) | Period's report requirement remains unsatisfied (unapproved), but changes from "missing" to "pending review" in the missing-requirements display |
| T2 | `SUBMITTED` | Request corrections | Faculty Coordinator (assigned cohort) | Authenticated; actor is assigned coordinator for the report owner's cohort; state is `SUBMITTED`; feedback text present | `CORRECTION_REQUESTED` | Feedback recorded with actor and timestamp; audit event `report.correction_requested`; notification to student | Missing feedback → business validation error, state unchanged; unassigned/non-faculty actor → authorization failure; invalid state → invalid-transition error | Report requirement remains unsatisfied; now additionally flagged as an outstanding correction (FR-COMP-006) |
| T3 | `SUBMITTED` | Approve report | Faculty Coordinator (assigned cohort) | Same actor conditions as T2; state is `SUBMITTED` | `APPROVED` | Approval timestamp, approver identity recorded; audit event `report.approved`; notification to student; compliance recalculated | Unassigned actor → authorization failure; invalid state → invalid-transition error | Period's report requirement becomes satisfied, provided the requirement definition for that period expects an approved report (FR-COMP-004) |
| T4 | `CORRECTION_REQUESTED` | Resubmit report | Student (owner) | Owner; revised required content present; state is `CORRECTION_REQUESTED` | `RESUBMITTED` | Resubmission timestamp recorded; prior feedback preserved in review history; audit event `report.resubmitted`; notification to assigned coordinator | Missing content → business validation error, state unchanged; generic "Submit" attempted instead of resubmit → invalid-transition error (Section 5.4); not owner → authorization failure | Report requirement remains unsatisfied (still unapproved); outstanding-correction flag persists until approval |
| T5 | `RESUBMITTED` | Request corrections | Faculty Coordinator (assigned cohort) | Actor conditions as T2; state is `RESUBMITTED` | `CORRECTION_REQUESTED` | Same as T2; new feedback appended to review history | Same as T2 | Same as T2 |
| T6 | `RESUBMITTED` | Approve report | Faculty Coordinator (assigned cohort) | Actor conditions as T2; state is `RESUBMITTED` | `APPROVED` | Same as T3; latest submission version is the approved content | Same as T3 | Same as T3 |
| T7 | `APPROVED` | Any modify/review action | Any role | — | No state change | Action refused; no audit mutation event | Modify attempts → authorization or invalid-transition failure depending on actor (Section 5.4) | None; compliance unchanged |
| T8 | `SUBMITTED` | Duplicate submit request (repeated request, double-click, retry) | Student (owner) | — | No state change | Second request recognized as already-submitted (idempotent) or rejected with duplicate-submission error | Duplicate-submission error; original `SUBMITTED` state and timestamp preserved | None |
| T9 | `DRAFT` | Approve or request corrections | Faculty Coordinator | — | No state change | Invalid-transition error: report not in a reviewable state | Invalid-transition failure | None |
| T10 | Any state | Action by unauthorized actor (wrong role, non-assigned coordinator, non-owner student) | Any | — | No state change | Authorization failure; operation not performed | Authorization failure (distinct from validation failure, Section 7.1) | None |

### 5.3 Distinction Between Report Status and Review Action

- **Report status** (`DRAFT`, `SUBMITTED`, `CORRECTION_REQUESTED`, `RESUBMITTED`, `APPROVED`) is a persisted property of the report record.
- **Content editing** is a permission, not a status or a transition. A student may edit report content only in `DRAFT` (FR-REPORT-003) and in `CORRECTION_REQUESTED` (FR-REPORT-009), and only on their own report. Editing never changes the state; the state changes only through the transitions in Section 5.2. In `CORRECTION_REQUESTED`, the student edits the existing record and then triggers T4 (Resubmit) to move it to `RESUBMITTED`.
- **Review actions** are the operations `Request corrections` and `Approve`, performed by a Faculty Coordinator. A review action is not a status; it *causes* a transition and is recorded in the review history with actor, action type, feedback (if any), and timestamp.
- Compliance evaluation reads report **status**, never review actions directly (Section 6.8).
- Notifications are triggered by review **actions** (Section 6.10) and by submission/resubmission actions.

### 5.4 Invalid and Unsupported Transitions

All of the following are rejected with no state change; each must be covered by tests. Content editing that is permitted in `DRAFT` and `CORRECTION_REQUESTED` (Section 5.3) is not a transition and is therefore not listed here.

| Attempt | Result |
|---|---|
| Approve or request corrections on a `DRAFT` report (T9) | Invalid-transition error |
| Any review action on an `APPROVED` report (T7) | Invalid-transition error; reopening requires OD-02 |
| Student submits an already-`SUBMITTED` report (T8) | Duplicate-submission error or idempotent no-op; original state preserved |
| Student edits a `SUBMITTED`, `RESUBMITTED`, or `APPROVED` report | Authorization failure (FR-REPORT-009) |
| Directly setting or manipulating a report's state, bypassing the Section 5.2 transitions | Refused; a state change is accepted only through a defined transition or review action |
| Generic "Submit" used on a `CORRECTION_REQUESTED` report | Invalid-transition error; the supported action is Resubmit (T4) |
| Correction request or approval on a report whose owner is outside the actor's assigned cohort (or non-faculty actor) | Authorization failure (T10) |
| Resubmit by anyone other than the record owner | Authorization failure (T10) |
| Any transition after completion verification of the internship | Rejected while completion is verified, unless OD-02/OD-08 establishes reversal behavior |

### 5.5 Reopening and Post-Approval Modification

The PRD (§18.1) explicitly leaves open "whether and under what circumstances an approved report may be modified, reopened, or superseded." Consequently:

- The MVP treats `APPROVED` as terminal (rules T7 and the rows of Section 5.4).
- This FRD does **not** authorize post-approval editing, reopening, or rejection by any role.
- The decision request is recorded as **OD-02** in Section 9. If approved, the state model, transitions T7, compliance rules (FR-COMP-004), and permissions in Sections 4.2.6–4.2.7 must be revised in a new FRD version.

## 6. Functional Requirements

Format per requirement: **ID and title**, requirement statement, actor, preconditions, expected behavior, postconditions, validation/error behavior, acceptance criteria, PRD parent reference, status.

### 6.1 Module 1 — Authentication and Role-Based Access

#### FR-AUTH-001 — Credential sign-in

- **Statement:** The system shall authenticate a user and establish an authenticated session when valid credentials are presented.
- **Actor:** Unauthenticated visitor (any of the three roles).
- **Preconditions:** User account exists and is active (created and activated per FR-USER-001; activation mechanics are OD-15).
- **Expected behavior:** On valid credentials, the system establishes a session for that user and directs the user to the landing surface of their assigned role. On invalid credentials, sign-in is refused.
- **Postconditions:** Authenticated session exists; the user's role determines accessible surfaces.
- **Validation and error behavior:** Invalid or unknown credentials produce a single generic sign-in failure message that does not reveal whether the account exists.
- **Acceptance criteria:**
  - AC1: Valid credentials for each of the three roles result in an authenticated session and role-appropriate landing surface.
  - AC2: Invalid credentials produce the generic failure message and no session.
  - AC3: Credentials of a deactivated account are refused with the same generic message (see FR-AUTH-005).
  - AC4: An account that has been created but not yet activated (FR-USER-001) cannot sign in and receives the same generic message.
- **PRD reference:** §7.1.1, §10 (Authentication and RBAC).
- **Status:** Confirmed (activation mechanics and credentials/tokens: OD-15).

#### FR-AUTH-002 — Session behavior and sign-out

- **Statement:** The system shall maintain the authenticated state of a user until sign-out or session expiry, and shall terminate it on sign-out.
- **Actor:** Authenticated user.
- **Preconditions:** An active session exists.
- **Expected behavior:** Authenticated requests are attributed to the session's user. Sign-out ends the session; subsequent requests are treated as unauthenticated. Signing in again requires credentials.
- **Postconditions:** After sign-out, no operation can be performed under the previous identity.
- **Validation and error behavior:** Requests without a valid session to protected operations fail as unauthenticated access (Section 7.1).
- **Acceptance criteria:**
  - AC1: A signed-out user cannot perform any protected operation until signing in again.
  - AC2: Session and token representation (format, lifetime, renewal) is not assumed by any other requirement in this document.
- **PRD reference:** §7.1.1.
- **Status:** Confirmed at functional level; session/token lifetime and mechanism deferred to Security Requirements (OD-15).

#### FR-AUTH-003 — Role-based authorization enforcement

- **Statement:** The system shall verify the requesting user's role (and, where applicable, record scope) before executing every protected operation, and shall deny the operation when the role or scope is not permitted.
- **Actor:** System (enforced for all roles).
- **Preconditions:** Operation is classified as protected in Section 4.
- **Expected behavior:** Permission values in Section 4 are enforced per operation and per record scope. Denied attempts execute no business logic and change no data.
- **Postconditions:** No state change on denial.
- **Validation and error behavior:** Authorization failures produce an access-denied outcome that is distinct from business validation failures (Section 7.1) and does not disclose the content of the denied record.
- **Acceptance criteria:**
  - AC1: For each Denied cell in Section 4, an attempt by that role fails with access denied and no data change.
  - AC2: For each Conditional cell, the operation succeeds only when the stated condition holds and fails otherwise.
- **PRD reference:** §5.1 goal 5, §7.1.1, §10, §15.3.
- **Status:** Confirmed.

#### FR-AUTH-004 — Protection of role-restricted surfaces and operations

- **Statement:** The system shall restrict role-specific pages, routes, and operations to the roles permitted in Section 4, and enforcement shall occur server-side and not rely solely on interface visibility.
- **Actor:** System.
- **Preconditions:** User requests a role-restricted route or operation.
- **Expected behavior:** A user whose role is not permitted receives an access-denied outcome; permitted users proceed to the operation. Hiding navigation elements is a convenience, not the enforcement mechanism.
- **Postconditions:** No data for the restricted surface is returned to unauthorized requesters.
- **Validation and error behavior:** Unauthorized route or API access behaves as FR-AUTH-003.
- **Acceptance criteria:**
  - AC1: Directly requesting a role-restricted URL or operation (bypassing navigation) with a non-permitted role fails.
  - AC2: Responses to unauthorized requests do not contain the restricted record payload.
- **PRD reference:** §7.1.1, §10.
- **Status:** Confirmed (delivery-layer details in SRS/API Specification).

#### FR-AUTH-005 — Deactivated and disabled accounts

- **Statement:** The system shall prevent a deactivated (disabled) account from authenticating or continuing to operate through an existing session.
- **Actor:** System; account status set by College Administrator (FR-USER-005).
- **Preconditions:** Account exists with deactivated status.
- **Expected behavior:** Sign-in attempts are refused with the generic failure message. A session that existed before deactivation ceases to authorize operations no later than the next protected request.
- **Postconditions:** Deactivated user can perform no operation; historical records remain intact and attributable (FR-USER-005).
- **Validation and error behavior:** Same generic message as failed credentials; the fact of deactivation is not disclosed to the sign-in attempt.
- **Acceptance criteria:**
  - AC1: A deactivated user cannot sign in.
  - AC2: A session created before deactivation fails on its next protected request.
  - AC3: Existing reports, documents, and audit records of the deactivated user remain readable by permitted roles.
- **PRD reference:** §7.1.1, §7.1.2, §7.1.12.
- **Status:** Derived; exact session-termination timing is part of OD-15.

#### FR-AUTH-006 — Authentication failure behavior

- **Statement:** The system shall handle repeated authentication failures without revealing account existence and without inventing an unspecified lockout policy.
- **Actor:** System.
- **Preconditions:** One or more failed sign-in attempts.
- **Expected behavior:** Every failure returns the generic message from FR-AUTH-001. This FRD establishes no account-lockout threshold, delay, or CAPTCHA; those remain decisions (OD-15).
- **Postconditions:** No account state change caused solely by failed attempts.
- **Validation and error behavior:** As above.
- **Acceptance criteria:**
  - AC1: Responses for "unknown account" and "wrong password" are indistinguishable.
  - AC2: No requirement elsewhere in the FRD depends on a lockout mechanism.
- **PRD reference:** §7.1.1.
- **Status:** Confirmed for non-enumeration; lockout/rate limiting deferred (OD-15).

#### FR-AUTH-007 — Unauthorized access attempt handling

- **Statement:** The system shall respond to unauthorized access attempts with a uniform access-denied outcome and shall not execute the requested operation.
- **Actor:** Any authenticated or unauthenticated user.
- **Preconditions:** Request targets an operation or record outside the requester's permissions (Section 4).
- **Expected behavior:** Operation refused; no partial execution; no data disclosure. Recording denied attempts as audit events is proposed (see FR-AUDIT-001, event set confirmation in OD-06).
- **Postconditions:** No state change.
- **Validation and error behavior:** Distinguished from business validation failures (Section 7.1).
- **Acceptance criteria:**
  - AC1: Cross-role and cross-scope attempts (student → other student's report; faculty → non-assigned cohort report) return access denied with no record content.
  - AC2: The access-denied outcome is observably different from a validation error.
- **PRD reference:** §5.1 goal 5, §15.3.
- **Status:** Confirmed (audit capture of attempts: proposed, OD-06).

### 6.2 Module 2 — User Management

#### FR-USER-001 — User creation

- **Statement:** The system shall allow a College Administrator to create a user account record with an identity (unique email/username), a display name, and an assigned role limited to Student, Faculty Coordinator, or College Administrator.
- **Actor:** College Administrator.
- **Preconditions:** Administrator is authenticated and active.
- **Expected behavior:** Creation establishes the account record and its role. Creation alone does not make the account eligible to sign in: an account becomes sign-in eligible only after it has been provisioned and activated under the onboarding process decided for the MVP (for example administrator-set credentials, an invitation, or first-login activation). That process is not established by the PRD and is deferred to OD-15 (with no self-registration per AS-05). Until an account is activated, sign-in attempts are refused using the generic failure outcome (FR-AUTH-001, FR-AUTH-005).
- **Postconditions:** New user record exists with an activation status distinct from account creation; role assignment recorded; audit event `user.created` recorded (FR-AUDIT-001).
- **Validation and error behavior:** Duplicate identity → business validation error naming the conflict; missing required field → business validation error; role outside the three MVP roles → business validation error; non-administrator → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin creates one user per role; once each account is provisioned and activated under the process decided in OD-15, it can sign in with the correct role permissions.
  - AC2: Creating a user with an existing identity fails and creates no record.
  - AC3: Non-admin creation attempts fail with authorization failure.
  - AC4: An account that has been created but not yet activated cannot sign in.
- **PRD reference:** §6.3, §7.1.2.
- **Status:** Confirmed for account creation and role assignment; account activation and credential-setup mechanics Open decision (OD-15); no self-registration flow is required (AS-05).

#### FR-USER-002 — Role assignment and change

- **Statement:** The system shall allow a College Administrator to assign a user's role at creation and to change a user's role afterward; roles outside the three MVP roles cannot be assigned.
- **Actor:** College Administrator.
- **Preconditions:** Target user exists; requested role is one of the three MVP roles.
- **Expected behavior:** The user's effective permissions follow the new role for subsequent requests. The change is recorded in audit history with before/after values (FR-AUDIT-002).
- **Postconditions:** Role updated; permissions re-evaluated per new role; audit event `user.role_changed` recorded.
- **Validation and error behavior:** Invalid role value → business validation error; non-admin → authorization failure; changing one's own role → governed by OD-01 (no behavior implemented until resolved).
- **Acceptance criteria:**
  - AC1: Changing a Student to a Faculty Coordinator grants review capabilities (subject to cohort assignment) and removes student-only actions.
  - AC2: The role change appears in audit history with actor, timestamp, and before/after values.
  - AC3: Permission effectiveness after change is observable on the next protected request.
- **PRD reference:** §6.3, §7.1.2, §7.1.12.
- **Status:** Confirmed (self-role-change constraint: OD-01; session invalidation behavior: OD-15).

#### FR-USER-003 — Own profile view and edit

- **Statement:** The system shall allow every authenticated user to view their own profile and to edit non-identity profile fields.
- **Actor:** Any authenticated role.
- **Preconditions:** Session active; field being edited is not an identity field.
- **Expected behavior:** User views own profile; edits to permitted self-service fields are saved.
- **Postconditions:** Profile updated for permitted fields; identity fields unchanged.
- **Validation and error behavior:** Attempt to edit identity fields → business validation or authorization refusal (PR-06); malformed values → business validation error.
- **Acceptance criteria:**
  - AC1: Each role can view own profile.
  - AC2: A student editing a permitted contact field succeeds; editing identity fields does not alter them.
- **PRD reference:** §7.1.2.
- **Status:** Proposed (PR-06); PRD does not specify profile self-service.

#### FR-USER-004 — Administrator edit of other users' records

- **Statement:** The system shall allow a College Administrator to edit directory information of other users (name, contact fields, role via FR-USER-002, account status via FR-USER-005).
- **Actor:** College Administrator.
- **Preconditions:** Target user exists; administrator active.
- **Expected behavior:** Changes saved and recorded in audit history.
- **Postconditions:** Target record updated; audit event recorded for role and status changes at minimum.
- **Validation and error behavior:** Missing/invalid values → business validation error; non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin corrects a student's name; the change is reflected in views that display it.
  - AC2: Role and status changes appear in audit history.
- **PRD reference:** §6.3.
- **Status:** Confirmed for directory data; limits of admin access to *student record content* (reports, documents) are governed by Section 4 and OD-01.

#### FR-USER-005 — Account deactivation and preservation of existing records

- **Statement:** The system shall allow a College Administrator to deactivate a user account; deactivation blocks access but preserves all records the user created or is referenced in.
- **Actor:** College Administrator.
- **Preconditions:** Target account exists and is active.
- **Expected behavior:** The account can no longer authenticate or operate (FR-AUTH-005). Reports, documents, internships, compliance entries, and audit events referencing the user remain intact and attributable to that user. Reactivation restores sign-in capability without altering historical attribution.
- **Postconditions:** Account status = deactivated; record set unchanged; audit event `user.deactivated` recorded with actor and target.
- **Validation and error behavior:** Deactivating the requesting administrator's own account → governed by OD-01 (separation of duties not established); non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: After deactivation, the user cannot sign in; their submitted reports remain visible to permitted roles with unchanged authorship.
  - AC2: No cascade deletion or anonymization of historical records occurs on deactivation.
  - AC3: The deactivation action appears in audit history.
- **PRD reference:** §6.3, §7.1.12 (preserve history).
- **Status:** Derived for preservation; self-deactivation constraint OD-01. No account deletion flow exists in the MVP (PR-08; deletion policy OD-01).

#### FR-USER-006 — Duplicate and invalid user data

- **Statement:** The system shall reject user creation or update requests containing duplicate identities or missing/invalid required fields.
- **Actor:** College Administrator (creation/update), any user (own profile edits).
- **Preconditions:** Request contains user data.
- **Expected behavior:** Validation runs before persistence; invalid requests persist nothing and identify the offending field in general terms.
- **Postconditions:** No partial user record on failure.
- **Validation and error behavior:** Duplicate identity → "identity already in use" style message; missing name/role/identity → required-field error; malformed identity format → format error. All are business validation failures (Section 7.1).
- **Acceptance criteria:**
  - AC1: Duplicate identity submission fails and the database contains exactly one record.
  - AC2: Missing required field submission fails and no record is created.
- **PRD reference:** §7.1.2.
- **Status:** Confirmed.

#### FR-USER-007 — Distinguishing user administration from internship-specific permissions

- **Statement:** The system shall separate user administration (account existence, role, status) from internship-specific scope assignment (cohort membership, faculty-to-cohort assignment), so that a role alone never grants record-level authority over specific students.
- **Actor:** System design constraint; College Administrator performs both kinds of assignment.
- **Preconditions:** Role assigned; scope assignments may or may not exist.
- **Expected behavior:** A user with the Faculty Coordinator role but no assigned cohort has no students to review and can perform no review operation. A student's records are reachable only through internship → cohort → assigned-coordinator chains. Administrative role status alone confers no review/approval/completion authority (PR-05).
- **Postconditions:** Permission checks consider both role and scope (FR-AUTH-003).
- **Validation and error behavior:** Faculty with no cohort assignment attempting review → authorization failure, not a validation error.
- **Acceptance criteria:**
  - AC1: A newly created Faculty Coordinator with no assigned cohort cannot view or review any student report.
  - AC2: An Administrator without cohort-level faculty assignment cannot approve reports (Section 4.2.7).
- **PRD reference:** §6.2, §6.3, §7.1.2, §7.1.4.
- **Status:** Confirmed (prohibition stated explicitly as PR-05 pending review).

#### FR-USER-008 — User listing and search

- **Statement:** The system shall allow a College Administrator to list and search user accounts, and shall allow a Faculty Coordinator to list students of assigned cohorts only.
- **Actor:** College Administrator; Faculty Coordinator.
- **Preconditions:** Authenticated; for faculty, at least one assigned cohort.
- **Expected behavior:** Results are limited to the requester's permitted scope (admin: deployment; faculty: assigned cohorts). Students receive access denied for user listings.
- **Postconditions:** No out-of-scope records in results.
- **Validation and error behavior:** Out-of-scope listing request → authorization failure or scope-limited result per FR-EXPORT-003.
- **Acceptance criteria:**
  - AC1: Admin listing includes all users of the deployment.
  - AC2: Faculty listing includes only assigned-cohort students; a second, unassigned cohort's students never appear.
  - AC3: Student attempts to list users fail with access denied.
- **PRD reference:** §6.3, §7.1.2; scope rule PR-01.
- **Status:** Confirmed for admin; Proposed for faculty scope (PR-04).

### 6.3 Module 3 — Internship Management

#### FR-INT-001 — Internship registration

- **Statement:** The system shall allow a Student to create (register) an internship record containing at minimum: the associated student, the associated cohort, a start date, an end date, and descriptive details identifying the internship.
- **Actor:** Student (own record only).
- **Preconditions:** Student is authenticated, active, and a member of a cohort.
- **Expected behavior:** On valid input the internship record is created and associated with the student and cohort. A pre-populated administrative creation flow is not required for MVP (AS-05).
- **Postconditions:** Internship exists; lifecycle state is the initial recorded state (FR-INT-004); audit event `internship.created` recorded (FR-AUDIT-001).
- **Validation and error behavior:** Missing required fields → business validation error; invalid dates → FR-INT-003; not a cohort member → business validation error (student must be assigned to a cohort first); non-owner → authorization failure.
- **Acceptance criteria:**
  - AC1: A cohort-member student registers an internship with valid data; the record appears in own dashboard views.
  - AC2: A student not assigned to a cohort receives a clear validation error and no record is created.
  - AC3: Registration by any other user for another student fails with authorization failure.
- **PRD reference:** §8 step 2, §7.1.3, §6.1.
- **Status:** Confirmed. Exact field catalogue beyond those listed is proposed and refined in the Database Design Document (OD-12 for lifecycle states).

#### FR-INT-002 — Student–internship association

- **Statement:** The system shall associate each internship record with exactly one student and exactly one cohort, and shall enforce one active internship per student in the MVP.
- **Actor:** System (enforcement); Student (creation).
- **Preconditions:** Internship creation or association request.
- **Expected behavior:** Attempts to create a second active internship for a student who already has one are rejected.
- **Postconditions:** Each internship references one student and one cohort.
- **Validation and error behavior:** Second active internship → business validation error explaining an active internship already exists; orphan association (unknown student/cohort) → validation error.
- **Acceptance criteria:**
  - AC1: Creating a second internship for the same student fails while one exists.
  - AC2: Every internship record resolves to one student and one cohort.
- **PRD reference:** §8, §7.1.3, §14.1 (single-deployment scope).
- **Status:** Assumption AS-01 (one active internship per student), proposed because the PRD does not address multiple concurrent internships. Confirm at review.

#### FR-INT-003 — Date and field validation for internships

- **Statement:** The system shall require start and end dates on internship records and shall reject logically invalid date ranges and invalid field values.
- **Actor:** Student (registration/edit); validation enforced by the system.
- **Preconditions:** Internship create or edit request.
- **Expected behavior:** Missing dates → rejection; end date earlier than start date → rejection; malformed date values → rejection. On rejection nothing is persisted.
- **Postconditions:** Stored internship records always have a valid, ordered date range.
- **Validation and error behavior:** Business validation errors identifying the date problem; no partial record.
- **Acceptance criteria:**
  - AC1: Registration without dates fails.
  - AC2: Registration with end date before start date fails with a date-range error.
  - AC3: A valid range (end date equal to start date) succeeds.
- **PRD reference:** §7.1.3 (internship details), §10 (Data integrity, §12).
- **Status:** Derived from data-integrity expectations (PRD §12); no minimum internship duration is specified or invented (duration rules: PRD does not establish any).

#### FR-INT-004 — Internship lifecycle state

- **Statement:** The system shall track a lifecycle state for each internship, of which at minimum two points are established by the PRD: the record's creation (registration) and verified completion (Module 11).
- **Actor:** System; College Administrator/Faculty Coordinator transitions only where this FRD authorizes them (completion verification only).
- **Preconditions:** Internship exists.
- **Expected behavior:** All other lifecycle states (for example, active, in review, suspended) are not established by the PRD and shall not be invented; the exact state set is OD-12. Until resolved, requirements reference "not yet verified complete" versus "verified complete."
- **Postconditions:** State changes occur only via transitions authorized in this FRD (creation; completion verification).
- **Validation and error behavior:** Any attempt to set lifecycle state directly → authorization failure (Section 4.2.3).
- **Acceptance criteria:**
  - AC1: A registered internship is in the initial state and is not marked complete.
  - AC2: Completion state changes only through FR-COMPLETE-003.
  - AC3: No requirement or interface depends on lifecycle states beyond those established until OD-12 resolves.
- **PRD reference:** §7.1.3 (track lifecycle state), §11.1 (completion).
- **Status:** Partially open — minimum anchor states Confirmed; full state set OD-12.

#### FR-INT-005 — Assignment and reassignment rules

- **Statement:** The system shall support the initial association of a student with an internship (FR-INT-002). Any reassignment, transfer, or replacement of a student's internship is not defined by the PRD and shall not be implemented until OD-13 resolves.
- **Actor:** Student (initial association); reassignment actor unspecified (open).
- **Preconditions:** Existing internship record.
- **Expected behavior:** MVP behavior is limited to creation and detail editing (FR-INT-007). Reassignment attempts are refused until a rule exists.
- **Postconditions:** No silent reassignment occurs.
- **Validation and error behavior:** Reassignment request → operation not available (open decision), not a fabricated success.
- **Acceptance criteria:**
  - AC1: No implemented path moves an internship from one student to another.
  - AC2: OD-13 is referenced wherever transfer behavior would be required (e.g., test cases for cohort changes: OD-14).
- **PRD reference:** §7.1.3; PRD §18.1 (record relationships and lifecycle).
- **Status:** Open decision (OD-13).

#### FR-INT-006 — Access to internship information

- **Statement:** The system shall restrict internship record visibility per Section 4.2.3: owner student, assigned-cohort faculty, deployment-wide administrator.
- **Actor:** All roles.
- **Preconditions:** Authenticated request for internship data.
- **Expected behavior:** In-scope requests return the record; out-of-scope requests fail with access denied and no payload.
- **Postconditions:** No out-of-scope disclosure.
- **Validation and error behavior:** Authorization failure per FR-AUTH-003/007.
- **Acceptance criteria:**
  - AC1: Student A cannot retrieve Student B's internship record.
  - AC2: Faculty with one assigned cohort cannot retrieve an internship of a student in an unassigned cohort (subject to PR-01 confirmation).
- **PRD reference:** §5.1 goal 5, §7.1.3.
- **Status:** Scoping Proposed (PR-01); enforcement Confirmed.

#### FR-INT-007 — Editing internship details

- **Statement:** The system shall allow a Student to edit own internship details while the internship is not verified complete; editability after faculty initial review of internship details is governed by OD-12.
- **Actor:** Student (own record).
- **Preconditions:** Record owned by actor; internship not verified complete.
- **Expected behavior:** Valid edits update the record. Edits that affect compliance-relevant data (dates, cohort) trigger compliance re-evaluation (FR-COMP-007). After completion verification, editing is refused.
- **Postconditions:** Record updated; audit event for compliance-affecting changes (FR-AUDIT-001); compliance recalculated if applicable.
- **Validation and error behavior:** Invalid values → business validation error, record unchanged; verified-complete record → authorization/invalid-state failure; other student's record → authorization failure.
- **Acceptance criteria:**
  - AC1: Student edits own internship end date; change persists and compliance view remains consistent.
  - AC2: Editing after completion verification fails.
  - AC3: Editing another student's internship fails.
- **PRD reference:** §7.1.3, §8 step 4 (initial review), §12 (data integrity).
- **Status:** Base edit behavior Derived; post-review edit rules open (OD-12).

#### FR-INT-008 — Relationships between internships, students, cohorts, and requirements

- **Statement:** The system shall maintain the relationships: student → internship → cohort → requirement set, such that compliance evaluation can resolve the applicable requirements for any internship.
- **Actor:** System.
- **Preconditions:** Records exist.
- **Expected behavior:** Every internship resolves to exactly one student and one cohort (FR-INT-002); every cohort resolves to a requirement set (FR-COHORT-004); compliance evaluation consumes that chain (FR-COMP-001).
- **Postconditions:** Referential consistency maintained; no compliance evaluation without a resolvable requirement set (evaluation reports "requirement set not configured" state per FR-COMP-010 interim).
- **Validation and error behavior:** Attempts to break the chain (delete cohort with internships, unassign student) → refused as business validation error; no cascade deletion.
- **Acceptance criteria:**
  - AC1: Given a cohort with configured requirements, a student's internship resolves to that requirement set for compliance display.
  - AC2: Removing/deleting a cohort referenced by internships is refused (cohorts are deactivated at most; deletion not in MVP — PR-08).
- **PRD reference:** §18.1 (record relationships), §7.1.4, §7.1.8.
- **Status:** Derived (structure required by PRD §18.1; authoritative relationship definition remains part of OD-01/OD-03 database work).

### 6.4 Module 4 — Cohort Management

#### FR-COHORT-001 — Cohort creation and update

- **Statement:** The system shall allow a College Administrator to create a cohort and update its configuration (identifier/name, applicable period, assigned faculty coordinator, deadlines, requirement set).
- **Actor:** College Administrator.
- **Preconditions:** Administrator authenticated.
- **Expected behavior:** Valid cohort records are created/updated; updates that affect requirements or deadlines are recorded in audit history (FR-AUDIT-001) and trigger downstream re-evaluation per FR-COMP-009/OD-03.
- **Postconditions:** Cohort exists with referenced configuration; audit event `cohort.created` / `cohort.updated` recorded.
- **Validation and error behavior:** Missing required fields → business validation error; duplicate cohort identifier → business validation error; non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin creates a cohort and updates its deadline configuration; changes persist and are audited.
  - AC2: Non-admin attempts fail with authorization failure.
- **PRD reference:** §8 step 1, §7.1.4, §6.3.
- **Status:** Confirmed.

#### FR-COHORT-002 — Student assignment to cohorts

- **Statement:** The system shall allow a College Administrator to assign a student to a cohort and to change that assignment only under a decided policy (OD-14).
- **Actor:** College Administrator.
- **Preconditions:** Student and cohort exist.
- **Expected behavior:** Assignment establishes the student's scope chain (FR-INT-008). Initial assignment is supported; moving a student who already has records requires OD-14.
- **Postconditions:** Student is a member of exactly one cohort at any time (AS-02); audit event `cohort.member_added` recorded.
- **Validation and error behavior:** Unknown student/cohort → business validation error; move of student with existing records → operation refused pending OD-14; non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin assigns an unassigned student to a cohort; the student's internship resolves to that cohort.
  - AC2: Attempting to move a student with existing internship/report records is refused with an explanatory outcome and no data change.
- **PRD reference:** §7.1.4, §6.3, §18.1.
- **Status:** Initial assignment Confirmed; membership moves Open decision (OD-14).

#### FR-COHORT-003 — Faculty coordinator assignment

- **Statement:** The system shall allow a College Administrator to assign one or more Faculty Coordinators to a cohort, establishing that faculty member's record scope (FR-USER-007).
- **Actor:** College Administrator.
- **Preconditions:** Faculty user with Faculty Coordinator role exists; cohort exists.
- **Expected behavior:** Assignment grants the coordinator review/monitoring scope over the cohort's students; removal withdraws scope for future requests. Default of one primary coordinator per cohort: AS-03.
- **Postconditions:** Scope chain updated; audit event `cohort.coordinator_assigned` recorded with before/after values.
- **Validation and error behavior:** Target user lacking Faculty Coordinator role → business validation error; unknown cohort → validation error; non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin assigns a faculty coordinator; that coordinator can then view and review the cohort's student reports.
  - AC2: A faculty coordinator without assignment cannot access any student records (FR-USER-007).
- **PRD reference:** §7.1.4, §6.3, §8.
- **Status:** Confirmed.

#### FR-COHORT-004 — Cohort internship requirements

- **Statement:** The system shall allow an authorized administrator to define the internship requirement set for a cohort, where each requirement is classified as mandatory or optional, and the requirement set forms the input to compliance evaluation and completion verification.
- **Actor:** College Administrator (responsibility confirmed by PRD §6.3); the precise set of configurable requirement attributes and who else may edit them is OD-03.
- **Preconditions:** Cohort exists.
- **Expected behavior:** Requirements are created/updated for the cohort; each requirement has an identifier, a description, a mandatory/optional flag, and a type that maps to an evaluation rule supported by FR-COMP-003–006 (document submission, approved report per required period, or other types added only by decision). Changes are audited (FR-AUDIT-001).
- **Postconditions:** Internship compliance evaluations use the updated requirement set, subject to OD-03 (change propagation to existing internships is undecided).
- **Validation and error behavior:** Requirement without mandatory/optional flag → business validation error; requirement type not supported by the evaluation engine → business validation error; non-permitted actor → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin defines a mandatory document requirement; students of that cohort see it in required-documents views.
  - AC2: Admin defines a mandatory approved-weekly-report requirement per required period; compliance reflects unmet periods for existing internships only in accordance with the pending OD-03 decision (test to be finalized after resolution).
  - AC3: Every requirement record carries a mandatory/optional classification.
- **PRD reference:** §7.1.4, §6.3, §7.1.8, §11.1, §18.1.
- **Status:** Confirmed for admin configuration; scope, attribute catalogue, and change propagation Open decision (OD-03).

#### FR-COHORT-005 — Cohort deadlines

- **Statement:** The system shall allow an administrator to configure cohort-level deadline data, and the system shall associate reports and other submissions with the applicable cohort deadlines for notification and display purposes.
- **Actor:** College Administrator (configuration); System (application).
- **Preconditions:** Cohort exists.
- **Expected behavior:** Configured deadline values are stored and surfaced to students and faculty in dashboard/notification views. Whether late submissions are blocked, marked, or tolerated, and all default/grace values, are OD-10; until resolved, no late-processing rule is enforced beyond recording actual timestamps (see FR-REPORT-005 interim behavior).
- **Postconditions:** Deadline values persisted and audited; associated notifications planned per FR-NOTIF-004.
- **Validation and error behavior:** Malformed deadline values → business validation error; non-admin → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin sets a deadline value; students of the cohort see it in deadline displays.
  - AC2: No test asserts late/early handling behavior until OD-10 is resolved.
- **PRD reference:** §7.1.4, §7.1.10, §18.2 (report cadence and deadlines).
- **Status:** Configuration Confirmed; deadline semantics Open decision (OD-10).

#### FR-COHORT-006 — Access restrictions based on cohort assignment

- **Statement:** The system shall confine each Faculty Coordinator's record access to cohorts assigned to them and each Student's access to own records within their cohort.
- **Actor:** System (enforcement) for all roles.
- **Preconditions:** Authenticated request involving cohort-scoped data.
- **Expected behavior:** Scope checks apply to every cohort-scoped operation in this document (view, review, approve, monitor, export). Administrator scope is the single deployment scope (one department/cohort per PRD §11.1).
- **Postconditions:** No out-of-scope record returned.
- **Validation and error behavior:** Out-of-scope access → authorization failure with no payload.
- **Acceptance criteria:**
  - AC1: With two cohorts configured, a coordinator assigned to cohort A cannot read cohort B records through any tested operation (view, review, export).
  - AC2: Students never receive cohort-wide listings.
- **PRD reference:** §5.1 goal 5, §7.1.4, §14.1.
- **Status:** Scoping Proposed (PR-01) pending confirmation; enforcement Confirmed.

#### FR-COHORT-007 — Effects of cohort configuration changes

- **Statement:** The system shall record cohort configuration changes in audit history and shall not silently alter existing student or internship records when cohort configuration changes; the propagation rule for requirement changes is decided by OD-03 and cohort membership changes by OD-14.
- **Actor:** College Administrator (changes); System (recording and re-evaluation).
- **Preconditions:** Cohort exists with configuration and dependent records.
- **Expected behavior:** Deadline/label updates apply prospectively to displays. Requirement-set changes trigger the re-evaluation behavior defined in FR-COMP-009, which currently records the decision gap rather than inventing an outcome.
- **Postconditions:** Audit event with before/after values for the change (FR-AUDIT-002); no unintended record mutation.
- **Validation and error behavior:** Change attempt by non-admin → authorization failure; propagation attempts beyond decided rules → refused.
- **Acceptance criteria:**
  - AC1: Requirement change on a cohort with existing internships is recorded in audit history and does not alter stored report/document records.
  - AC2: The visible compliance effect of that change matches the behavior specified by OD-03 once resolved; until then, tests assert only the audit record and absence of data corruption.
- **PRD reference:** §7.1.4, §12 (data integrity), §18.1.
- **Status:** Partially open (OD-03, OD-14).

### 6.5 Module 5 — Document Management

#### FR-DOC-001 — Required document set (categories and requirements)

- **Statement:** The system shall support a configurable set of required document categories per cohort, where each required category maps to a mandatory or optional internship requirement for compliance evaluation.
- **Actor:** College Administrator (configuration, per FR-COHORT-004 and OD-03); System (evaluation).
- **Preconditions:** Cohort exists.
- **Expected behavior:** Required categories (for example, "offer letter" as an example label only — exact catalogue is OD-05/OD-03) are defined at cohort level; students see their cohort's required categories; compliance evaluation consumes this set.
- **Postconditions:** Requirement set includes document-type requirements.
- **Validation and error behavior:** Category without a name or without mandatory/optional classification → business validation error.
- **Acceptance criteria:**
  - AC1: Admin defines document categories for a cohort; students of that cohort see matching required-document entries.
  - AC2: Exact file types and size limits are not asserted anywhere in requirements; they remain configurable policy values (FR-DOC-003, OD-16).
- **PRD reference:** §7.1.4, §7.1.5, §18.2 (document requirements).
- **Status:** Confirmed for mechanism; exact document catalogue Open decision (OD-05, OD-03).

#### FR-DOC-002 — Document submission

- **Statement:** The system shall allow a Student to submit a file for a document category against their own internship, storing the file in private object storage with associated metadata.
- **Actor:** Student (own internship only).
- **Preconditions:** Student active; internship exists; selected category belongs to the student's cohort requirement set (or is otherwise permitted); file passes FR-DOC-003 validation.
- **Expected behavior:** File stored privately; metadata recorded (category, associated internship, submitting user, filename, size, content type, submission timestamp, status per FR-DOC-006); audit event `document.submitted` recorded; notification to assigned coordinator (FR-NOTIF-001).
- **Postconditions:** Document record exists with submitted status; required-document requirement moves toward satisfied (subject to review-effect decision OD-05).
- **Validation and error behavior:** Unsupported file type → business validation error, nothing stored; oversized file → business validation error (configurable limit); upload failure (storage unavailable) → technical failure with clear message, no document record; submission for another student → authorization failure.
- **Acceptance criteria:**
  - AC1: Student submits a valid file; the document appears in own document views with metadata.
  - AC2: Rejected file types produce a validation error and no stored document record.
  - AC3: Storage-side failure produces a technical failure outcome distinct from validation errors and no half-created record.
- **PRD reference:** §7.1.5, §8 step 3, §11.2 (private object storage).
- **Status:** Confirmed (exact limits/types OD-16).

#### FR-DOC-003 — File validation (type and size) as configurable policy

- **Statement:** The system shall validate every upload against configured file-type and file-size policies, where the concrete allowed extensions/MIME types and maximum size are configuration values rather than hardcoded requirements.
- **Actor:** System.
- **Preconditions:** Upload request.
- **Expected behavior:** Files violating policy are rejected before storage. Policy values are supplied by configuration; the MVP ships with a conservative default set selected during implementation, recorded in the Database Design/Deployment documents — no specific value is asserted in this FRD.
- **Postconditions:** Only policy-conformant files are stored.
- **Validation and error behavior:** Business validation error stating the file violates the type or size policy; no partial storage.
- **Acceptance criteria:**
  - AC1: A file exceeding the configured maximum size is rejected with a size error.
  - AC2: A file type outside the configured allowlist is rejected with a type error.
  - AC3: Changing configuration changes enforcement without code change (implementation-level check for SRS).
- **PRD reference:** §7.1.5, §11.2, §18 (validation rules to be defined).
- **Status:** Confirmed for mechanism; concrete values Open decision (OD-16).

#### FR-DOC-004 — Document replacement

- **Statement:** The system shall allow a Student to replace a previously submitted document in the same category while the record is eligible for replacement, subject to the document-review policy (OD-05).
- **Actor:** Student (own internship).
- **Preconditions:** Existing document record for that category; replacement file passes FR-DOC-003; record not locked by review outcome (interim rule below).
- **Expected behavior:** Interim behavior pending OD-05: replacement is permitted until a Faculty Coordinator has recorded a review action on the document; after a recorded review, replacement is refused and the decision on post-review replacement remains open. Replacement preserves the category and internship association, updates the file and timestamp, records audit event `document.replaced`, and triggers compliance re-evaluation (FR-COMP-007).
- **Postconditions:** New file content stored; prior content version not retained in MVP (version retention is part of OD-11 retention policy).
- **Validation and error behavior:** Replacement of another student's document → authorization failure; post-review replacement under interim rule → business validation error stating replacement is no longer permitted; invalid file → FR-DOC-003 errors.
- **Acceptance criteria:**
  - AC1: Replacing an unreviewed document succeeds, updates metadata, and is audited.
  - AC2: Replacing a reviewed document is refused under the interim rule (and behavior after OD-05 resolves matches that decision).
  - AC3: Compliance reflects the current file's presence after replacement.
- **PRD reference:** §7.1.5, §7.1.12.
- **Status:** Proposed interim rule pending Open decision (OD-05).

#### FR-DOC-005 — Document review

- **Statement:** The system shall allow an assigned Faculty Coordinator to review documents submitted by assigned students and record a review action with actor and timestamp.
- **Actor:** Faculty Coordinator (assigned cohort only).
- **Preconditions:** Document submitted by an assigned student; coordinator assigned; record not already under a decided review outcome (state set per OD-05).
- **Expected behavior:** The coordinator can open submitted documents and record review feedback/decisions. The exact review-outcome set (for example, accepted vs. corrections requested) and whether document approval is required for compliance remain OD-05; this FRD establishes only that review capability, actor scoping, and timestamped recording are required (PRD §8 step 4 requires initial review of internship details and documents).
- **Postconditions:** Review action recorded with actor, timestamp, and any feedback; audit event `document.reviewed` recorded; student notified of review outcome (FR-NOTIF-001); compliance effect applied only as decided by OD-05 (interim: submission alone advances the requirement; review does not change it).
- **Validation and error behavior:** Non-assigned coordinator → authorization failure; document not in a reviewable state → invalid-state error; student attempting review → authorization failure.
- **Acceptance criteria:**
  - AC1: Assigned coordinator records a review action on a submitted document; student and faculty see the recorded outcome and timestamp.
  - AC2: A coordinator without assignment cannot open or review the document.
  - AC3: A student cannot perform any review action.
- **PRD reference:** §8 step 4, §7.1.5 (review of required documents), §10 (Document workflow).
- **Status:** Review capability Confirmed; outcome set and compliance effect Open decision (OD-05).

#### FR-DOC-006 — Missing, invalid, and document status visibility

- **Statement:** The system shall expose per-document submission status (not submitted / submitted / reviewed under the OD-05 outcome set) and shall identify missing required documents for each student.
- **Actor:** System (computation); Student (own view); Faculty Coordinator (assigned cohort).
- **Preconditions:** Cohort requirement set includes document requirements (FR-DOC-001).
- **Expected behavior:** For each internship, required categories without a valid submitted document are listed as missing; submitted documents show their status; invalid uploads never yield a submitted document (FR-DOC-002/003).
- **Postconditions:** Missing-document information is consistent between document views, dashboards, and compliance display (FR-COMP-008 uses the same source).
- **Validation and error behavior:** Categories with no requirement linkage are not evaluated; configuration gaps surface as "requirement set not configured" rather than a false compliant result (FR-COMP-010).
- **Acceptance criteria:**
  - AC1: With 3 required categories and 1 submitted document, the student and assigned faculty views show 2 missing and 1 submitted.
  - AC2: A document rejected by file validation leaves the category "missing."
- **PRD reference:** §7.1.5, §7.1.8, §10.
- **Status:** Confirmed.

#### FR-DOC-007 — Storage, retrieval, and access control

- **Statement:** The system shall store documents in private object storage and shall serve document content only through authorized, permission-checked retrieval paths; no publicly accessible file URLs shall exist.
- **Actor:** System; authorized requesters (Student owner; assigned Faculty Coordinator; College Administrator subject to OD-01).
- **Preconditions:** Document record exists; requester authorized per Section 4.2.5.
- **Expected behavior:** Retrieval checks the requester's scope before returning content; unauthorized retrieval attempts return access denied without file content. Metadata visibility follows the same scope rules as content.
- **Postconditions:** No file content or direct URL exposed to unauthorized requesters.
- **Validation and error behavior:** Authorization failure per FR-AUTH-003/007; missing/corrupt stored file → technical failure with clear outcome (Section 7.1).
- **Acceptance criteria:**
  - AC1: Student A cannot fetch Student B's document through any tested path.
  - AC2: Unauthenticated requests for document content fail.
  - AC3: A non-assigned coordinator cannot fetch a document of a non-assigned cohort's student.
- **PRD reference:** §11.2 (private object storage), §5.1 goal 5.
- **Status:** Confirmed; administrator content access subject to OD-01.

#### FR-DOC-008 — Effects of document changes on compliance

- **Statement:** The system shall re-evaluate affected requirements whenever a document is submitted, replaced, or (if permitted by OD-05) withdrawn or review-outcome-changed, so compliance information never contradicts document state.
- **Actor:** System.
- **Preconditions:** Document event occurs on an internship with a resolvable requirement set.
- **Expected behavior:** The relevant document requirement is re-evaluated (satisfied when a valid submission exists for a mandatory category, subject to OD-05 review-effect) and overall compliance status updated (FR-COMP-007).
- **Postconditions:** Compliance display matches current document state.
- **Validation and error behavior:** Evaluation failures surface as stale-data detection for re-processing; they never silently mark requirements satisfied.
- **Acceptance criteria:**
  - AC1: After a successful document submission, the missing-requirement list no longer shows that category as missing.
  - AC2: After a permitted replacement, compliance remains consistent with the current document record.
- **PRD reference:** §7.1.8, §7.1.5, §12 (data integrity).
- **Status:** Confirmed mechanism; review-effect nuance tied to OD-05.

### 6.6 Module 6 — Weekly Reports and Work Logs

#### FR-REPORT-001 — Report creation (draft)

- **Statement:** The system shall allow a Student to create one report draft for a reporting period against their own internship.
- **Actor:** Student (owner of the internship).
- **Preconditions:** Student active; internship exists; no existing report for the same internship and reporting period (FR-REPORT-006); internship not verified complete.
- **Expected behavior:** A single report record is created for the internship-period pair with the reporting period and empty/incomplete content; visible only to the owner (and administrators per Section 4.2.6). This same record is used for all later states; editing, submission, correction, and resubmission never create an additional report for the period (FR-REPORT-006, FR-REPORT-008).
- **Postconditions:** Exactly one report record for the period exists in state `DRAFT`.
- **Validation and error behavior:** Duplicate period → business validation error; no internship → business validation error; creating for another student → authorization failure.
- **Acceptance criteria:**
  - AC1: Student creates a draft for the current week; state is `DRAFT`.
  - AC2: Creating a second report for the same period fails.
  - AC3: Drafts are invisible to other students and to faculty (review queue contains only `SUBMITTED`/`RESUBMITTED`).
- **PRD reference:** §7.1.6, §8 step 5.
- **Status:** Confirmed; reporting period = calendar week per PRD weekly-report scope (cadence details OD-10).

#### FR-REPORT-002 — Required report content

- **Statement:** The system shall require, at minimum, the following before a report may be submitted: associated internship (implicit in record), reporting period, and work-log content describing the work performed during that period.
- **Actor:** Student; validation enforced by the system.
- **Preconditions:** Report creation or submission request.
- **Expected behavior:** Submission is refused until all required content fields are present and non-empty. Extended attribute catalogue (hours, supervisor sign-off, attachments) is not established by the PRD and is not required (OD-12-adjacent; refine in SRS/API Specification if decided).
- **Postconditions:** No report enters `SUBMITTED` without required content.
- **Validation and error behavior:** Missing/empty required field → business validation error naming the field category; state unchanged.
- **Acceptance criteria:**
  - AC1: Submitting a draft with empty work-log content fails with a required-field error.
  - AC2: After providing content, submission succeeds.
- **PRD reference:** §7.1.6, §6.1.
- **Status:** Derived (minimum content set proposed; confirm field catalogue at SRS stage).

#### FR-REPORT-003 — Draft editing

- **Statement:** The system shall allow a Student to edit own reports while they are in state `DRAFT`.
- **Actor:** Student (owner).
- **Preconditions:** Report owned by actor; state `DRAFT`; internship not verified complete.
- **Expected behavior:** Edits persist immediately; no audit event required for draft content edits (creation/submission are audited instead).
- **Postconditions:** Draft content updated; state remains `DRAFT`.
- **Validation and error behavior:** Editing a non-owned report → authorization failure; editing a report whose state is neither `DRAFT` nor `CORRECTION_REQUESTED` → per FR-REPORT-009.
- **Acceptance criteria:**
  - AC1: Owner edits draft content; changes persist.
  - AC2: Editing a `SUBMITTED` report fails (covered by FR-REPORT-009).
- **PRD reference:** §7.1.6.
- **Status:** Confirmed.

#### FR-REPORT-004 — Report submission

- **Statement:** The system shall allow a Student to submit a `DRAFT` report of their own, provided validation passes, transitioning it to `SUBMITTED`.
- **Actor:** Student (owner).
- **Preconditions:** State `DRAFT`; required content present (FR-REPORT-002); internship exists and not verified complete; duplicate/period rule satisfied (FR-REPORT-006).
- **Expected behavior:** State becomes `SUBMITTED`; submission timestamp recorded; assigned Faculty Coordinator notified (FR-NOTIF-001); audit event `report.submitted` recorded (FR-AUDIT-001); compliance re-evaluated (FR-COMP-007).
- **Postconditions:** Report is read-only for the student; appears in the assigned coordinator's review queue.
- **Validation and error behavior:** Missing content → business validation error (state unchanged); duplicate submission request → T8 behavior; unauthorized actor → authorization failure; internship verified complete → invalid-state error.
- **Acceptance criteria:**
  - AC1: Valid draft submission changes state to `SUBMITTED` with a recorded timestamp.
  - AC2: After submission the student can no longer edit the report.
  - AC3: The assigned coordinator's pending-review list includes the report; a non-assigned coordinator's does not.
  - AC4: Audit history contains `report.submitted` with actor and timestamp.
- **PRD reference:** §7.1.6, §9.1, §9 (milestone step 1).
- **Status:** Confirmed.

#### FR-REPORT-005 — Deadline handling and late submissions

- **Statement:** The system shall record the actual submission timestamp for every submission and shall not block a submission solely because it occurs after a configured deadline until the late-submission policy (OD-10) is decided.
- **Actor:** System (interim rule); Student (submitting).
- **Preconditions:** Report otherwise valid for submission.
- **Expected behavior:** Interim rule pending OD-10: submissions are accepted regardless of deadline comparison; the timestamp is recorded; no "late" flag, penalty, or block is applied. When OD-10 is decided, this requirement shall define the exact behavior (blocked vs. flagged vs. tolerated) and any grace period.
- **Postconditions:** Submission timestamp reliably stored for later timeliness evaluation.
- **Validation and error behavior:** No deadline-related rejection under the interim rule; all other validations still apply.
- **Acceptance criteria:**
  - AC1: A submission after a configured deadline succeeds under the interim rule and records its true timestamp.
  - AC2: No compliance calculation depends on timeliness until OD-10 resolves (FR-COMP-010 note).
  - AC3: After OD-10 resolves, AC1/AC2 are updated to the decided behavior.
- **PRD reference:** §7.1.6, §18.2 (report cadence and deadlines: grace periods and late handling open).
- **Status:** Interim rule Pending Open decision (OD-10).

#### FR-REPORT-006 — Duplicate and overlapping submissions

- **Statement:** The system shall permit at most one report record per internship per reporting period, enforced from creation regardless of the report's state, and shall reject any attempt to create or submit a second report for a period that already has a report.
- **Actor:** System (enforcement).
- **Preconditions:** Report creation or submission request specifying a reporting period.
- **Expected behavior:** Uniqueness is enforced at creation (FR-REPORT-001) and re-checked at submission; a period that already holds a report in any state (including `DRAFT`) cannot receive a second report. Repeated submission requests for the same existing report are idempotent or rejected as duplicates (T8) while preserving the original state and timestamp. Correction resubmission operates on the existing report record (FR-REPORT-008) and never creates an additional report.
- **Postconditions:** At most one report exists for each internship-period pair, in any state.
- **Validation and error behavior:** A second report for an already-covered internship-period pair → business validation error; repeated submit on the same record → duplicate/idempotent outcome, no timestamp change. Uniqueness must be enforced atomically at persistence so that concurrent creation requests for the same pair cannot both succeed.
- **Acceptance criteria:**
  - AC1: A second report cannot be created or submitted for an internship-period pair that already has a report, regardless of that report's state.
  - AC2: A double-clicked (or retried) submit action leaves exactly one `SUBMITTED` report with one timestamp.
  - AC3: After a correction request, resubmitting the existing report does not create an additional report record.
- **PRD reference:** §7.1.6, §12 (data integrity).
- **Status:** Derived (one report per internship-period pair; required for the weekly workflow and consistent with the glossary and FR-REPORT-001). The definition of the reporting period itself (cadence) remains OD-10.

#### FR-REPORT-007 — Viewing submitted reports

- **Statement:** The system shall allow report viewing per Section 4.2.6: owner student always; assigned Faculty Coordinator for assigned cohort; College Administrator subject to the pending decision on admin access to record content (OD-01); other students denied.
- **Actor:** All roles.
- **Preconditions:** Authenticated; report exists.
- **Expected behavior:** In-scope viewing returns report content with state and applicable history; out-of-scope attempts fail with access denied and no content.
- **Postconditions:** No cross-record disclosure.
- **Validation and error behavior:** Authorization failure per FR-AUTH-003/007.
- **Acceptance criteria:**
  - AC1: Owner views own report in any state, including `APPROVED`.
  - AC2: Assigned coordinator views `SUBMITTED`, `RESUBMITTED`, `CORRECTION_REQUESTED`, `APPROVED` reports of assigned students.
  - AC3: Another student's attempt to view returns access denied without content.
- **PRD reference:** §5.1 goal 5, §7.1.6, §7.1.7.
- **Status:** Confirmed for owner/faculty scope; student cross-access Proposed Denied (PR-01); admin content access OD-01.

#### FR-REPORT-008 — Resubmission after corrections

- **Statement:** The system shall allow a Student to resubmit their own report from state `CORRECTION_REQUESTED` after revising its content, transitioning it to `RESUBMITTED`. The revision itself occurs while the report remains in `CORRECTION_REQUESTED` (FR-REPORT-009); this requirement defines the Resubmit transition (T4) that moves the report to `RESUBMITTED`.
- **Actor:** Student (owner).
- **Preconditions:** State `CORRECTION_REQUESTED`; record owned by actor; required content present (including revisions addressing feedback — content presence is validated; feedback satisfaction is judged by faculty at review); internship not verified complete.
- **Expected behavior:** State becomes `RESUBMITTED`; resubmission timestamp recorded; prior feedback preserved in review history; assigned coordinator notified; audit event `report.resubmitted`; compliance re-evaluated (still unapproved → still unsatisfied).
- **Postconditions:** Report returns to the review queue; student can no longer edit it.
- **Validation and error behavior:** Missing content → business validation error, state unchanged; generic "Submit" attempt → invalid-transition error (Section 5.4); unauthorized actor → authorization failure.
- **Acceptance criteria:**
  - AC1: Student resubmits a correction-requested report; state becomes `RESUBMITTED` with timestamp and preserved prior feedback.
  - AC2: After resubmission the report reappears in the coordinator's review queue.
  - AC3: Compliance still shows the period's report requirement unsatisfied until approval.
- **PRD reference:** §7.1.6, §8 step 7, §9 (milestone step 3).
- **Status:** Confirmed.

#### FR-REPORT-009 — Editing restrictions by report state

- **Statement:** The system shall permit the owning Student to edit report content only while the report is in `DRAFT` or `CORRECTION_REQUESTED`, and shall prevent any modification of report content in states `SUBMITTED`, `RESUBMITTED`, and `APPROVED`. Faculty Coordinators and College Administrators are denied report-content edits in every state (PR-02).
- **Actor:** System (enforcement) for all roles; faculty/administrators are equally denied content edits (PR-02).
- **Preconditions:** Edit request for a report.
- **Expected behavior:** An owner edit is accepted in `DRAFT` (state unchanged, FR-REPORT-003) and in `CORRECTION_REQUESTED` (state unchanged; the revised content is captured by the subsequent Resubmit action, T4). In `SUBMITTED`, `RESUBMITTED`, and `APPROVED` the edit is refused and no content changes. Editing is never itself a state transition; state changes occur only through the transitions in Section 5.2.
- **Postconditions:** In `CORRECTION_REQUESTED`, the revised content is stored on the same report record; in `SUBMITTED`/`RESUBMITTED`/`APPROVED`, the stored content is unchanged by edit attempts.
- **Validation and error behavior:** Content edit by the owner in `SUBMITTED`/`RESUBMITTED`/`APPROVED` → authorization failure (the operation is not permitted in that state); content edit by a non-owner or by a Faculty Coordinator/Administrator → authorization failure in every state. No content mutation occurs in any refusal case; Section 7.1 distinguishes authorization failure from business validation failure.
- **Acceptance criteria:**
  - AC1: Edit attempts on `SUBMITTED`, `RESUBMITTED`, and `APPROVED` reports fail with no content change for every role.
  - AC2: The owner can revise a `CORRECTION_REQUESTED` report; the state remains `CORRECTION_REQUESTED` until the Resubmit action (T4), which captures the revised content and moves the report to `RESUBMITTED`.
  - AC3: No role other than the owning Student can edit report content in any state.
- **PRD reference:** §7.1.6, §8 step 7, §9, §15.3 (workflow integrity).
- **Status:** Confirmed for `DRAFT` (FR-REPORT-003) and `CORRECTION_REQUESTED` revision (PRD §8 step 7); Confirmed for `SUBMITTED`/`APPROVED` (PRD-derived); faculty/administrator content-edit denial Proposed (PR-02).

#### FR-REPORT-010 — Report state effects on compliance

- **Statement:** The system shall evaluate each report's contribution to compliance solely from its state: only `APPROVED` satisfies a period's report requirement; `SUBMITTED`, `CORRECTION_REQUESTED`, and `RESUBMITTED` keep it unsatisfied; an absent report counts as missing (FR-COMP-003).
- **Actor:** System.
- **Preconditions:** Report state change event; internship has resolvable requirement set.
- **Expected Behavior:** Every transition in Section 5.2 triggers re-evaluation (FR-COMP-007) so compliance never lags report state.
- **Postconditions:** Compliance display consistent with report states.
- **Validation and error behavior:** Evaluation errors never mark a requirement satisfied.
- **Acceptance criteria:**
  - AC1: Submitting a report does not by itself satisfy the period requirement; approval does.
  - AC2: A correction request after a pending report (T2/T5) does not remove previously approved periods' satisfaction.
- **PRD reference:** §7.1.8, §9 (compliance update after approval).
- **Status:** Confirmed.

### 6.7 Module 7 — Faculty Review Workflow

#### FR-REVIEW-001 — Access to assigned student reports

- **Statement:** The system shall present each Faculty Coordinator with the review queue of reports in reviewable states (`SUBMITTED`, `RESUBMITTED`) belonging to students of cohorts assigned to that coordinator.
- **Actor:** Faculty Coordinator.
- **Preconditions:** Coordinator assigned to at least one cohort (FR-USER-007).
- **Expected behavior:** Queue contains only assigned-coordinator records in reviewable states; `DRAFT` and `CORRECTION_REQUESTED` reports are not reviewable; `APPROVED` reports appear only in history views, not pending queues. No records outside assigned scope are ever returned (FR-COHORT-006).
- **Postconditions:** Out-of-scope reports absent from results.
- **Validation and error behavior:** Accessing a specific out-of-scope report by direct reference → authorization failure.
- **Acceptance criteria:**
  - AC1: Coordinator's queue shows only assigned-cohort reports in `SUBMITTED`/`RESUBMITTED`.
  - AC2: A coordinator with no assigned cohort sees an empty queue and can open no report.
  - AC3: Directly referencing an out-of-scope report ID returns access denied.
- **PRD reference:** §6.2, §7.1.7, §7.1.4.
- **Status:** Confirmed for scoping mechanism; scope rule PR-01 pending confirmation.

#### FR-REVIEW-002 — Review action recording (actor, timestamp)

- **Statement:** The system shall record every review action with the acting user, action type, timestamp, and any feedback provided, and shall not perform review actions anonymously or without attribution.
- **Actor:** Faculty Coordinator (assigned).
- **Preconditions:** Report in a reviewable state; actor authorized.
- **Expected behavior:** Each `Request corrections` or `Approve` action writes a review-history entry and an audit event (FR-AUDIT-001) atomically with the state transition (Section 5.2).
- **Postconditions:** Review history contains attributable, timestamped entries; report state updated once (no partial application).
- **Validation and error behavior:** If history/audit persistence fails, the transition fails as a technical failure with no state change (consistency rule).
- **Acceptance criteria:**
  - AC1: Every approval and correction request in history shows actor identity and timestamp.
  - AC2: No transition appears in report state without a corresponding review-history entry.
- **PRD reference:** §7.1.7 (preserve review decisions and history), §7.1.12, §12.
- **Status:** Confirmed.

#### FR-REVIEW-003 — Correction request

- **Statement:** The system shall allow a Faculty Coordinator to request corrections on a report in state `SUBMITTED` or `RESUBMITTED`, providing feedback text, transitioning the report to `CORRECTION_REQUESTED`.
- **Actor:** Faculty Coordinator (assigned cohort).
- **Preconditions:** State is `SUBMITTED` or `RESUBMITTED`; actor is assigned coordinator for the owner's cohort; feedback text present and non-empty.
- **Expected behavior:** State → `CORRECTION_REQUESTED` (T2/T5); feedback recorded in review history with actor/timestamp; audit event `report.correction_requested`; student notified (FR-NOTIF-001); compliance re-evaluated (period requirement remains unsatisfied; outstanding-correction flag set — FR-COMP-006).
- **Postconditions:** Report returned to the student's resubmission flow; student regains the ability to revise (via resubmission, FR-REPORT-008).
- **Validation and error behavior:** Empty feedback → business validation error, state unchanged; invalid state (e.g., `DRAFT`, `APPROVED`) → invalid-transition error; unauthorized actor → authorization failure.
- **Acceptance criteria:**
  - AC1: Assigned coordinator requests corrections with feedback; state becomes `CORRECTION_REQUESTED` and student receives the feedback.
  - AC2: Empty-feedback request fails with state unchanged.
  - AC3: Requesting corrections on an `APPROVED` report fails (Section 5.4) — OD-02 would change this.
  - AC4: The student's dashboard shows the outstanding correction as a pending action.
- **PRD reference:** §6.2, §7.1.7, §9.1 (milestone step 2).
- **Status:** Confirmed.

#### FR-REVIEW-004 — Approval

- **Statement:** The system shall allow a Faculty Coordinator to approve a report in state `SUBMITTED` or `RESUBMITTED`, transitioning it to `APPROVED`.
- **Actor:** Faculty Coordinator (assigned cohort).
- **Preconditions:** State is `SUBMITTED` or `RESUBMITTED`; actor is assigned coordinator for the owner's cohort; report content present.
- **Expected behavior:** State → `APPROVED` (T3/T6); approver identity and timestamp recorded; audit event `report.approved`; student notified; compliance re-evaluated (period requirement satisfied — FR-REPORT-010).
- **Postconditions:** Report terminal in `APPROVED`; approval traceable to a specific coordinator and time.
- **Validation and error behavior:** Invalid state → invalid-transition error; unauthorized actor → authorization failure; report whose internship is verified complete → invalid-state error (post-verification changes governed by OD-08 note in FR-COMPLETE-005).
- **Acceptance criteria:**
  - AC1: Assigned coordinator approves a `RESUBMITTED` report; state becomes `APPROVED`, student notified, compliance updated.
  - AC2: Approval by a non-assigned coordinator fails with no state change.
  - AC3: The approval record identifies the approving coordinator and time.
- **PRD reference:** §6.2, §11.1, §9.1 (milestone step 4).
- **Status:** Confirmed.

#### FR-REVIEW-005 — Repeated correction cycles

- **Statement:** The system shall support any number of correction/resubmission cycles for a single report without a fixed cap in the MVP.
- **Actor:** Student and Faculty Coordinator alternating.
- **Preconditions:** Report follows the supported transitions (Section 5.2).
- **Expected behavior:** T2/T4 and T5/T4 sequences may repeat; each cycle is appended to review history; no automatic terminal outcome occurs after N cycles. A maximum-cycle policy is not established by the PRD; if adopted later it becomes a decision (assumption AS-06).
- **Postconditions:** Full cycle history retained and viewable per FR-REVIEW-006.
- **Validation and error behavior:** No cycle-count validation errors exist in the MVP.
- **Acceptance criteria:**
  - AC1: A report can complete at least three correction/resubmission cycles and still be approved.
  - AC2: Each cycle appears separately in review history.
- **PRD reference:** §7.1.6, §7.1.7.
- **Status:** Assumption AS-06 (no cycle cap), pending confirmation.

#### FR-REVIEW-006 — Review history

- **Statement:** The system shall maintain a chronological review history for each report containing, per entry: action type (correction request or approval), acting user, timestamp, and feedback text where applicable, together with submission and resubmission timestamps.
- **Actor:** System (recording); Faculty Coordinator (assigned) and owning Student (viewing, per PR-03); College Administrator subject to OD-01.
- **Preconditions:** Report exists with at least one action.
- **Expected behavior:** History is append-only through application workflows; entries cannot be edited or deleted by any role (aligned with PRD §7.1.12 principles for history preservation).
- **Postconditions:** History complete for the report's lifetime.
- **Validation and error behavior:** Attempts to modify history entries → authorization failure; history remains readable even if the acting faculty user is deactivated (attribution preserved).
- **Acceptance criteria:**
  - AC1: After submit → correction → resubmit → approve, history shows all four steps with actors and timestamps in order.
  - AC2: No application operation alters or removes an existing history entry.
  - AC3: The owning student can read the feedback entries (PR-03 pending confirmation).
- **PRD reference:** §7.1.7, §7.1.12.
- **Status:** Confirmed for recording; viewer set Proposed (PR-03) and partly OD-06.

#### FR-REVIEW-007 — Invalid transition handling during review

- **Statement:** The system shall reject any review action that does not correspond to a transition in Section 5.2, leaving report state and history unchanged.
- **Actor:** System (enforcement).
- **Preconditions:** Review action attempted.
- **Expected behavior:** Invalid combinations (approve `DRAFT`; correction request on `CORRECTION_REQUESTED` without intervening resubmission; any action on `APPROVED`) return an invalid-transition outcome and execute nothing.
- **Postconditions:** State unchanged; no history or audit entry for the failed attempt except any denied-attempt logging adopted under OD-06.
- **Validation and error behavior:** Invalid-transition failure, distinct from authorization failure (the actor may be authorized but the state wrong) and from technical failure.
- **Acceptance criteria:**
  - AC1: Approving a `DRAFT` report fails as invalid transition with no state/history change.
  - AC2: Approving an `APPROVED` report fails (Section 5.4).
  - AC3: Error categories are distinguishable (validation vs. authorization vs. invalid state vs. technical).
- **PRD reference:** §7.1.7, §12 (data integrity).
- **Status:** Confirmed.

#### FR-REVIEW-008 — Unauthorized review attempts

- **Statement:** The system shall reject review actions performed by users who are not assigned Faculty Coordinators for the report owner's cohort, including Students and College Administrators.
- **Actor:** System (enforcement).
- **Preconditions:** Review action attempted by an unauthorized actor.
- **Expected behavior:** Authorization failure; no transition; no feedback or approval recorded; attempt optionally logged per OD-06.
- **Postconditions:** No state change.
- **Validation and error behavior:** Authorization failure (not a validation error), no record content disclosed.
- **Acceptance criteria:**
  - AC1: A College Administrator attempting approval fails with authorization failure (PR-05).
  - AC2: A Student attempting approval or correction request fails.
  - AC3: A Faculty Coordinator assigned to a different cohort fails.
- **PRD reference:** §5.1 goal 5, §11.1 (approval authority is Faculty Coordinators), §15.3.
- **Status:** Confirmed for student/admin denial; non-assigned faculty denial Proposed (PR-01).

#### FR-REVIEW-009 — Behavior when review deadlines have passed

- **Statement:** The system shall continue to accept faculty review actions after any configured deadline passes, and shall record actual action timestamps, until a review-deadline policy is decided (OD-10).
- **Actor:** Faculty Coordinator; System.
- **Preconditions:** Report in reviewable state; deadline (if configured) already passed.
- **Expected behavior:** Interim rule pending OD-10: review actions are not blocked by deadline passage; timestamps make late review detectable. No automatic escalation, reassignment, or auto-approval exists.
- **Postconditions:** Review continues normally; timestamp reflects actual action time.
- **Validation and error behavior:** No deadline-based rejection under the interim rule.
- **Acceptance criteria:**
  - AC1: Approving a report after a configured review deadline succeeds and records the true timestamp.
  - AC2: No automatic state change occurs solely because a deadline passed.
- **PRD reference:** §18.2 (deadlines and grace periods open), §7.1.7.
- **Status:** Interim rule Pending Open decision (OD-10).

#### FR-REVIEW-010 — Actions on approved reports

- **Statement:** The system shall reject all modification and review actions on reports in state `APPROVED`, regardless of actor role.
- **Actor:** System (enforcement).
- **Preconditions:** Report in `APPROVED`.
- **Expected behavior:** Student edit, faculty re-approval, correction request, and admin edit attempts are all refused with no state or content change (T7, Section 5.4). Reopening, supersession, or post-approval correction requires OD-02.
- **Postconditions:** Approved content and compliance contribution unchanged by any attempted action.
- **Validation and error behavior:** Authorization or invalid-transition failure per Section 7.1; no data change in any case.
- **Acceptance criteria:**
  - AC1: For every role, attempting edit/correction/approval on an `APPROVED` report fails and the record is byte-identical afterward.
  - AC2: Compliance remains satisfied for that period after all attempted actions.
- **PRD reference:** §9, §18.1 (modification/reopening open).
- **Status:** Confirmed default; reopening/removal of this restriction requires OD-02.

### 6.8 Module 8 — Compliance Engine

#### FR-COMP-001 — Applicable requirement set resolution

- **Statement:** The system shall resolve, for each internship, the requirement set applicable from its cohort (student → internship → cohort → requirement set chain) and shall evaluate compliance only against that set.
- **Actor:** System.
- **Preconditions:** Internship, cohort, and requirement set records exist (FR-INT-008).
- **Expected behavior:** Evaluations always reference the internship's cohort requirement set; if no requirement set is configured, compliance is reported as "requirement set not configured" rather than compliant or non-compliant.
- **Postconditions:** Every compliance result is traceable to a specific requirement set version.
- **Validation and error behavior:** Unresolvable chain → configuration-error state visible to faculty/admin, never a silent pass.
- **Acceptance criteria:**
  - AC1: Two cohorts with different requirement sets produce different compliance results for identical student records.
  - AC2: A cohort without a requirement set never shows "compliant."
- **PRD reference:** §7.1.8, §7.1.4, §18.1.
- **Status:** Confirmed mechanism; requirement-set ownership/versioning tied to OD-03.

#### FR-COMP-002 — Compliance evaluation procedure

- **Statement:** The system shall compute compliance status for an internship by evaluating each applicable requirement's satisfaction state and combining them: an internship is fully compliant only when every mandatory requirement is satisfied; optional requirements are reported but do not block compliance or completion.
- **Actor:** System.
- **Preconditions:** Requirement set resolved; underlying records available (reports, documents).
- **Expected behavior:** Per-requirement evaluation uses the rules in FR-COMP-003 through FR-COMP-006 and their type-specific counterparts; the aggregate status derives from mandatory-requirement results only. No percentage score, weighting, grace period, or waiver exists (PRD does not establish any).
- **Postconditions:** Compliance status stored/derived consistently for all displays.
- **Validation and error behavior:** Any per-requirement evaluation error marks that requirement "unevaluated," never "satisfied."
- **Acceptance criteria:**
  - AC1: With all mandatory requirements satisfied and one optional unsatisfied, status = fully compliant (per proposed status set FR-COMP-010) and completion eligibility holds.
  - AC2: With one mandatory requirement unsatisfied, status is not fully compliant regardless of optional results.
  - AC3: No numeric score or percentage appears anywhere in MVP outputs.
- **PRD reference:** §7.1.8, §11.1 (completion: mandatory requirements), §18.1.
- **Status:** Confirmed for mandatory/optional distinction; aggregate status vocabulary Proposed pending OD-04.

#### FR-COMP-003 — Missing report requirement evaluation

- **Statement:** The system shall mark a required reporting period's report requirement unsatisfied when no report exists for that period, and shall display it as a missing report.
- **Actor:** System.
- **Preconditions:** Requirement set includes weekly-report requirements for defined periods; period exists without a report.
- **Expected behavior:** Requirement state = unsatisfied (missing); counted toward non-compliance and completion ineligibility; shown in missing-requirement displays.
- **Postconditions:** No period with an approved report is ever reported missing.
- **Validation and error behavior:** Periods without configured expectations are not evaluated.
- **Acceptance criteria:**
  - AC1: With 4 required periods and reports only for periods 1–2, periods 3–4 display as missing reports.
  - AC2: Completing period 3 (submitting) changes it from missing to pending (unapproved), not satisfied.
- **PRD reference:** §7.1.8, §10 (Compliance Engine), §18.1 (define rules).
- **Status:** Confirmed for the missing/unsatisfied determination; which periods are "required" is a requirement-definition input (OD-03/OD-10 interplay: reporting schedule must exist before periods are known).

#### FR-COMP-004 — Unapproved report requirement evaluation

- **Statement:** The system shall mark a period's report requirement unsatisfied while the report exists in any state other than `APPROVED` (`SUBMITTED`, `CORRECTION_REQUESTED`, `RESUBMITTED`).
- **Actor:** System.
- **Preconditions:** Report exists for a required period.
- **Expected behavior:** State changes re-evaluate immediately (FR-COMP-007); only `APPROVED` satisfies.
- **Postconditions:** Compliance never shows a period satisfied on submission alone.
- **Validation and error behavior:** State corruption (unknown state) → requirement "unevaluated."
- **Acceptance criteria:**
  - AC1: A `SUBMITTED` report shows its period unsatisfied with display reason "awaiting approval."
  - AC2: On approval, the period becomes satisfied immediately in dashboards and reports.
- **PRD reference:** §7.1.8, §9 (compliance updates after approval).
- **Status:** Confirmed.

#### FR-COMP-005 — Missing or invalid document requirement evaluation

- **Statement:** The system shall mark a mandatory document requirement unsatisfied when no valid submission exists for the required category, and satisfied when a valid submission exists, subject to the document-review effect decision (OD-05).
- **Actor:** System.
- **Preconditions:** Requirement set includes a mandatory document category; document records evaluated.
- **Expected behavior:** Interim rule pending OD-05: submission of a policy-valid file satisfies the requirement; recorded review does not yet change satisfaction. Rejected/invalid uploads never satisfy.
- **Postconditions:** Document views and compliance display agree (FR-DOC-008).
- **Validation and error behavior:** Evaluation errors → "unevaluated," never satisfied.
- **Acceptance criteria:**
  - AC1: Category with no submission → unsatisfied and listed as missing document.
  - AC2: Category with a valid submission → satisfied under interim rule.
  - AC3: If OD-05 requires approval, this AC2 behavior is revised to the decided rule.
- **PRD reference:** §7.1.8, §7.1.5.
- **Status:** Interim rule Pending Open decision (OD-05).

#### FR-COMP-006 — Outstanding correction requirement evaluation

- **Statement:** The system shall identify, for each internship, any report currently in state `CORRECTION_REQUESTED` as an outstanding correction, and shall treat affected mandatory report requirements as unsatisfied.
- **Actor:** System.
- **Preconditions:** At least one report in `CORRECTION_REQUESTED`.
- **Expected behavior:** Outstanding corrections are counted and displayed as a distinct category of missing/outstanding requirement (distinct from "missing report" and "awaiting approval"); cleared automatically when the report leaves that state.
- **Postconditions:** Students and faculty see outstanding corrections in compliance and dashboard views.
- **Validation and error behavior:** None beyond standard evaluation errors.
- **Acceptance criteria:**
  - AC1: When a correction is requested, the affected period moves to "outstanding correction" in displays.
  - AC2: After resubmission it moves to "awaiting approval"; after approval it is satisfied and no longer outstanding.
- **PRD reference:** §7.1.8, §10.
- **Status:** Confirmed.

#### FR-COMP-007 — Recalculation and consistency

- **Statement:** The system shall re-evaluate affected compliance information as a direct consequence of every compliance-relevant event, and all compliance displays shall derive from the same evaluation result.
- **Actor:** System.
- **Preconditions:** Compliance-relevant event occurs: report submit/resubmit/correction/approve; document submit/replace/review (if decided); internship detail change affecting evaluation; cohort requirement or deadline change (subject to OD-03); user cohort reassignment (subject to OD-14).
- **Expected behavior:** Each event triggers evaluation of the affected internship(s). Whether evaluation is synchronous or asynchronous is an implementation choice (SRS/Architecture); the requirement is that no user-visible display contradicts the latest recorded state beyond the accepted processing window, and dashboards/reports/exports never disagree (FR-DASH-006, FR-EXPORT-003).
- **Postconditions:** Compliance status updated; consistency across modules.
- **Validation and error behavior:** Evaluation failure leaves prior status marked stale rather than silently wrong, and is re-processed; failures never mark requirements satisfied.
- **Acceptance criteria:**
  - AC1: Immediately after a report approval event, student dashboard, faculty dashboard, and cohort report show the same compliance result.
  - AC2: Requirement-change events trigger re-evaluation only as specified by OD-03 (test finalized after resolution).
- **PRD reference:** §7.1.8, §9 (compliance-status update), §12 (data integrity).
- **Status:** Confirmed for event-driven recalculation; mechanism deferred to SRS; requirement-change recalculation OD-03/OD-04.

#### FR-COMP-008 — Display of missing requirements

- **Statement:** The system shall display, for each internship, the complete list of unsatisfied mandatory requirements with a plain-language reason for each (missing report for period X; report awaiting approval; outstanding correction on period X; missing document category Y).
- **Actor:** System (computation); Student (own list); Faculty Coordinator (assigned cohort); College Administrator (deployment scope).
- **Preconditions:** Requirement set resolved; evaluation complete.
- **Expected behavior:** The list is exhaustive for mandatory requirements — every unsatisfied mandatory requirement appears with its reason and reference to the underlying record where applicable; optional unsatisfied requirements shown separately and marked as non-blocking.
- **Postconditions:** The list equals the complement of satisfied requirements (no unsatisfied mandatory requirement omitted).
- **Validation and error behavior:** Unevaluated requirements appear as such with an explicit "cannot evaluate" marker rather than being omitted.
- **Acceptance criteria:**
  - AC1: Given an internship with 5 mandatory requirements of which 2 are unsatisfied, exactly those 2 appear with reasons.
  - AC2: The list shown to the student matches the list shown to the assigned faculty coordinator.
  - AC3: Optional unsatisfied requirements never block compliance (FR-COMP-002).
- **PRD reference:** §7.1.8, §3 (unclear compliance status problem), §10.
- **Status:** Confirmed.

#### FR-COMP-009 — Effect of requirement changes on existing internships

- **Statement:** When a cohort's requirement set is changed, the system shall record the change in audit history and shall not corrupt existing report, document, or approval records; the rule governing how the change affects existing internships' compliance and completion eligibility shall follow OD-03 once decided.
- **Actor:** College Administrator (change); System (recording/evaluation).
- **Preconditions:** Cohort has existing internships and dependent records.
- **Expected behavior:** Interim behavior pending OD-03: the change is recorded with before/after values, existing report/document records are untouched, and the FRD asserts no specific re-evaluation outcome for already-evaluated internships. Implementation must not hardcode either "grandfather existing" or "apply retroactively" until OD-03 decides.
- **Postconditions:** Audit trail complete; data intact; compliance effect pending decision (FR-COMP-007 AC2 scoped accordingly).
- **Validation and error behavior:** Any propagation attempt beyond decided rules → refused.
- **Acceptance criteria:**
  - AC1: Requirement change on a cohort with active internships produces an audit event with before/after values.
  - AC2: No stored report/document/approval data is altered by the requirement change.
  - AC3: Post-OD-03, a test asserts the decided propagation outcome for existing internships.
- **PRD reference:** §18.1 (requirement configuration and ownership; compliance rules), §12.
- **Status:** Open decision (OD-03) — recorded rather than guessed.

#### FR-COMP-010 — Compliance status vocabulary

- **Statement:** The system shall expose a compliance status per internship derived from mandatory-requirement results; the final status vocabulary and the exact derivation are pending OD-04, with the following as the proposed minimum set for review.
- **Actor:** System.
- **Preconditions:** Evaluation complete (or blocked by configuration).
- **Expected behavior:** Proposed status set (requires review — OD-04): `NOT_STARTED` (no internship records/requirements yet satisfied), `IN_PROGRESS` (some but not all mandatory requirements satisfied), `FULLY_COMPLIANT` (all mandatory requirements satisfied), `CONFIGURATION_MISSING` (no requirement set configured). Counts of satisfied/mandatory-total accompany the status. Non-compliance categories (late, failed) are deliberately excluded until OD-10/OD-04 define them.
- **Postconditions:** Status is computed, never edited (PR-07); identical across student, faculty, admin views and exports (FR-COMP-007).
- **Validation and error behavior:** Unknown/unevaluated inputs → `CONFIGURATION_MISSING` or explicit unevaluated marker, never `FULLY_COMPLIANT`.
- **Acceptance criteria:**
  - AC1: An internship with 0/3 mandatory satisfied → `IN_PROGRESS` (or `NOT_STARTED` per decided semantics), never `FULLY_COMPLIANT`.
  - AC2: 3/3 mandatory satisfied → `FULLY_COMPLIANT` and completion eligibility (Module 11).
  - AC3: Status naming and transitions match OD-04's final decision once issued.
- **PRD reference:** §7.1.8, §18.1 (completion and compliance rules).
- **Status:** Proposed; Open decision (OD-04).

### 6.9 Module 9 — Role-Specific Dashboards

#### FR-DASH-001 — Student dashboard content

- **Statement:** The system shall provide a Student dashboard showing: own internship information, own weekly-report states, required documents and their status, own compliance status with missing requirements, applicable deadlines, pending actions (outstanding corrections, required submissions), and completion-verification progress.
- **Actor:** Student.
- **Preconditions:** Authenticated student with (optionally) an internship record.
- **Expected behavior:** All panels reflect the student's own records only. With no internship recorded, the dashboard prompts internship registration. Completion progress is explicitly distinguished from verified completion (FR-COMPLETE-006).
- **Postconditions:** No other student's data appears on any panel.
- **Validation and error behavior:** Data-fetching technical failures show a neutral error state, not stale/false compliance data.
- **Acceptance criteria:**
  - AC1: The dashboard shows the student's internship, report states, required documents, compliance status, deadlines, and pending actions.
  - AC2: No panel displays another student's information.
  - AC3: Progress display is labeled as progress and not as verified completion.
- **PRD reference:** §7.1.9, §6.1, §10 (Dashboards).
- **Status:** Confirmed for content areas; deadline panel values depend on OD-10.

#### FR-DASH-002 — Student deadlines and pending actions

- **Statement:** The system shall surface the student's nearest applicable deadlines and a pending-action list derived from current record states (outstanding correction requests, drafts not submitted, required documents not submitted, unverified completion).
- **Actor:** Student.
- **Preconditions:** Deadline configuration exists (values per OD-10); records exist.
- **Expected behavior:** Each pending item links (conceptually) to the permitted operation; items vanish when the underlying condition clears (for example, outstanding correction clears on resubmission).
- **Postconditions:** Pending-action list consistent with state model and compliance evaluation.
- **Validation and error behavior:** Absent deadline configuration → deadline panel shows "not configured" rather than an invented date.
- **Acceptance criteria:**
  - AC1: A `CORRECTION_REQUESTED` report appears as a pending action for its owner.
  - AC2: After resubmission, that pending action is replaced by "awaiting review."
  - AC3: No deadline date is displayed unless configured.
- **PRD reference:** §7.1.9, §7.1.6, §18.2.
- **Status:** Confirmed for derivation; deadline semantics OD-10.

#### FR-DASH-003 — Faculty Coordinator dashboard content

- **Statement:** The system shall provide a Faculty Coordinator dashboard showing, for assigned cohorts only: assigned students, reports pending review (`SUBMITTED`/`RESUBMITTED`), reports with outstanding corrections, cohort compliance overview, upcoming deadlines, and students with outstanding requirements.
- **Actor:** Faculty Coordinator.
- **Preconditions:** Coordinator assigned to at least one cohort; otherwise dashboard shows an empty state.
- **Expected behavior:** All lists are limited to assigned cohorts. Aggregate views (cohort compliance) are derived from the same evaluation source as individual views (FR-COMP-007).
- **Postconditions:** No unassigned-cohort record appears in any panel; the dashboard provides no review authority beyond Section 4.2.7.
- **Validation and error behavior:** Out-of-scope data request → authorization failure; empty assigned scope → empty state, not an error.
- **Acceptance criteria:**
  - AC1: Pending-review list matches reports in `SUBMITTED`/`RESUBMITTED` for assigned students.
  - AC2: Students from a second, unassigned cohort never appear.
  - AC3: A coordinator cannot act on an item listed under another coordinator's cohort.
- **PRD reference:** §7.1.9, §6.2, §10.
- **Status:** Confirmed for content; scope rule PR-01 pending confirmation.

#### FR-DASH-004 — College Administrator dashboard content

- **Statement:** The system shall provide a College Administrator dashboard showing relevant user, cohort, and internship information; operational progress and compliance overview for the deployment scope; permitted administrative actions; and authorized reporting/export entry points.
- **Actor:** College Administrator.
- **Preconditions:** Administrator authenticated.
- **Expected behavior:** The dashboard offers administrative operations only (user management, cohort and requirement management, monitoring, exports). It does not present review or approval actions (Section 4.4, PR-05). Compliance overview is derived from the same evaluation source.
- **Postconditions:** Administrative actions available match Section 4.2.
- **Validation and error behavior:** Administrative operation failures follow their module's validation rules.
- **Acceptance criteria:**
  - AC1: Dashboard shows cohort/internship/compliance overview counts consistent with compliance evaluation.
  - AC2: No review/approval action is offered or executable by the administrator.
  - AC3: Export entry points respect Section 4.2.13 permissions.
- **PRD reference:** §7.1.9, §6.3, §10, §13.1.
- **Status:** Confirmed for content areas; sensitive-record visibility limits OD-01.

#### FR-DASH-005 — Visibility and filtering scoping

- **Statement:** The system shall enforce the requesting user's permitted scope on every dashboard panel and filter, so that no client-side filter can expose records outside that scope.
- **Actor:** System (enforcement).
- **Preconditions:** Dashboard/filter request.
- **Expected behavior:** Scope filters are applied to data retrieval, not merely to display; unsupported or out-of-scope filter values yield either no rows or an authorization failure (per FR-EXPORT-003), never out-of-scope rows.
- **Postconditions:** No cross-scope leakage via filters, sorting, pagination, or direct parameter manipulation.
- **Validation and error behavior:** Out-of-scope filter targets → authorization failure or empty result; malformed filters → business validation error.
- **Acceptance criteria:**
  - AC1: Manipulating a filter parameter to another cohort's value never returns that cohort's rows.
  - AC2: All three roles see consistent data within their permitted scopes.
- **PRD reference:** §5.1 goal 5, §7.1.9, §7.1.13 (exports respect permissions).
- **Status:** Confirmed (scope values per PR-01 / OD-01).

#### FR-DASH-006 — Consistency of dashboard data

- **Statement:** The system shall present dashboard information consistent with module-level views, reports, and exports at the same point in time (single compliance source of truth).
- **Actor:** System.
- **Preconditions:** Compliance evaluation completed for the affected records.
- **Expected behavior:** Compliance counts and statuses on dashboards equal those seen in the Compliance module and in eligible exports; report/document panels reflect current states.
- **Postconditions:** No contradictory numbers between surfaces.
- **Validation and error behavior:** If a stale/unevaluated result is displayed, it is explicitly marked as such.
- **Acceptance criteria:**
  - AC1: Cohort compliance count on the faculty dashboard equals the count from the corresponding report/export.
  - AC2: A report approval updates all surfaces consistently (FR-COMP-007 AC1).
- **PRD reference:** §7.1.8, §12 (data integrity), §7.1.9.
- **Status:** Confirmed.

#### FR-DASH-007 — Dashboard actions limited to permitted operations

- **Statement:** The system shall present dashboard actions only for operations the current user is permitted to perform, and enforcement of each action remains independent of presentation.
- **Actor:** System.
- **Preconditions:** Dashboard rendered.
- **Expected behavior:** Hiding an action is a usability measure; calling the underlying operation still enforces Section 4 (FR-AUTH-004). Actions reference the state model so that, for example, no "Approve" action is offered for a `DRAFT` report.
- **Postconditions:** No dashboard interaction can bypass permission or state rules.
- **Validation and error behavior:** Forged/invalid action request → authorization or invalid-transition failure.
- **Acceptance criteria:**
  - AC1: Every dashboard action is either permitted-and-succeeds or not offered; a forged request for a non-offered action fails.
  - AC2: No action is offered that violates Section 5.2.
- **PRD reference:** §7.1.9, §5.1 goal 5.
- **Status:** Confirmed.

### 6.10 Module 10 — Notifications

#### FR-NOTIF-001 — Notification trigger events

- **Statement:** The system shall generate notifications for the following MVP workflow events: report submitted (→ assigned Faculty Coordinator), correction requested (→ owning Student), report resubmitted (→ assigned Faculty Coordinator), report approved (→ owning Student), document submitted (→ assigned Faculty Coordinator), completion verified (→ owning Student), and meeting/approaching deadline events where deadline configuration exists (→ affected Students and, where relevant, assigned Coordinators).
- **Actor:** System.
- **Preconditions:** Triggering event completed successfully.
- **Expected behavior:** One notification is generated per event occurrence per applicable recipient (FR-NOTIF-007). The exact trigger list is Proposed (confirm in OD-09); the workflow-derived triggers (submission, correction, resubmission, approval, completion verification) are Confirmed by PRD §7.1.10 and the workflow in §9. Deadline-notification timing/frequency is OD-09/OD-10.
- **Postconditions:** Notification records exist linked to the triggering record.
- **Validation and error behavior:** Notification generation must not block or roll back the triggering business action (FR-NOTIF-006).
- **Acceptance criteria:**
  - AC1: Each of the confirmed trigger events produces a notification for the correct recipient.
  - AC2: No notification is generated for events that did not complete.
  - AC3: Notification volume per event occurrence equals one per recipient.
- **PRD reference:** §7.1.10, §9.1.
- **Status:** Workflow triggers Confirmed; full trigger list and deadline timing Proposed/open (OD-09).

#### FR-NOTIF-002 — Intended recipients

- **Statement:** The system shall resolve notification recipients from record relationships and permissions (assigned Faculty Coordinator for a submission; owning Student for review outcomes), not from arbitrary addresses or user input.
- **Actor:** System.
- **Preconditions:** Trigger event with resolvable recipients.
- **Expected behavior:** Recipients are always in-scope users; if no assigned coordinator exists, submission notifications are not sent to users outside scope (an unassigned state is surfaced operationally rather than by notifying everyone).
- **Postconditions:** Users receive only notifications for events concerning their own records or assigned cohort.
- **Validation and error behavior:** Unresolvable recipient → event recorded without user notification (no broad fallback).
- **Acceptance criteria:**
  - AC1: A student's submission notifies only the coordinator(s) assigned to that student's cohort.
  - AC2: A correction request notifies only the report owner.
  - AC3: A user never receives a notification for a record outside their permitted scope.
- **PRD reference:** §7.1.10, §5.1 goal 5.
- **Status:** Confirmed.

#### FR-NOTIF-003 — Notification content and record references

- **Statement:** The system shall include in each notification: event type, reference to the affected record (internship/report/document), the acting user where applicable, event timestamp, and a concise message; notifications shall not embed sensitive document contents.
- **Actor:** System.
- **Preconditions:** Trigger event.
- **Expected behavior:** Notifications carry enough context to identify the event and record without exposing unnecessary personal or document content.
- **Postconditions:** Notification is navigable to the permitted view of the affected record (permission re-checked on navigation).
- **Validation and error behavior:** Missing record reference → notification not generated (treated as a technical fault for the event type).
- **Acceptance criteria:**
  - AC1: Opening a notification leads to the affected record only if the recipient still has permission.
  - AC2: Notification text includes event type, record reference, and timestamp.
- **PRD reference:** §7.1.10, §7.1.12.
- **Status:** Confirmed.

#### FR-NOTIF-004 — In-app notification behavior

- **Statement:** The system shall deliver in-app notifications to a per-user notification list maintained by the application.
- **Actor:** System; recipient user.
- **Preconditions:** Notification generated.
- **Expected behavior:** The notification appears in the recipient's list; the recipient can open it and mark it read (FR-NOTIF-007). The list is private to the recipient.
- **Postconditions:** Notification visible until read; retention tied to record retention policy (OD-11).
- **Validation and error behavior:** A user cannot view or modify another user's notification list (authorization failure).
- **Acceptance criteria:**
  - AC1: Triggered events appear in the in-app list of the correct recipient.
  - AC2: Another user cannot read or alter that list.
- **PRD reference:** §7.1.10, §11.2 (in-app notifications).
- **Status:** Confirmed.

#### FR-NOTIF-005 — Email notification behavior

- **Statement:** The system shall send email notifications for the same trigger events as in-app notifications, to the recipient's registered email address, using the configured email delivery service.
- **Actor:** System.
- **Preconditions:** Trigger event; email delivery dependency configured (PRD §14.2).
- **Expected behavior:** The email communicates the same event information as the in-app notification without containing access-restricted content beyond a reference.
- **Postconditions:** Email sent or failure handled per FR-NOTIF-006.
- **Validation and error behavior:** Email-delivery (technical) failures do not affect in-app delivery or the business action; delivery is not assumed reliable.
- **Acceptance criteria:**
  - AC1: Each confirmed trigger event results in an email to the correct recipient address.
  - AC2: Email failure does not roll back or block the triggering workflow action.
- **PRD reference:** §7.1.10, §11.2 (email and in-app), §14.2.
- **Status:** Confirmed functionally; delivery guarantees/retries OD-09.

#### FR-NOTIF-006 — Notification failure handling

- **Statement:** The system shall treat notification delivery as non-blocking: failures are recorded and do not roll back, block, or alter the triggering business operation; retry policy, timing, and guarantees require OD-09.
- **Actor:** System.
- **Preconditions:** Notification generation or delivery failure.
- **Expected behavior:** Business state persists per its own transaction (for example, a report remains `SUBMITTED` even if its notification email fails); the failure is recorded for operational visibility. No retry limit or schedule is invented.
- **Postconditions:** Core workflow state independent of notification success.
- **Validation and error behavior:** Technical failure category (Section 7.1); recorded, not raised as a user-facing business error for the acting user.
- **Acceptance criteria:**
  - AC1: Simulating email failure does not change report/document/compliance state or block the action.
  - AC2: The failure is recorded/observable.
  - AC3: No retry count or schedule is assumed in tests until OD-09 resolves.
- **PRD reference:** §7.1.10, §14.2.
- **Status:** Non-blocking principle Proposed; retry/guarantee Open decision (OD-09).

#### FR-NOTIF-007 — Duplicate handling, history, and read status

- **Statement:** The system shall prevent duplicate notifications for a single event occurrence (including retries) and shall expose per-user read status and enough history for the recipient to see recent events.
- **Actor:** System; recipient user.
- **Preconditions:** Event occurrence; possible retry.
- **Expected behavior:** At most one notification per event occurrence per recipient; read status is per-user (recipient); previously read notifications remain listable per retention (OD-11). Notification history is private to its recipient; viewing notifications across users is denied (Section 4.2.10).
- **Postconditions:** No notification storms from repeated identical events; read state preserved.
- **Validation and error behavior:** Duplicate-detection failure must not create repeated visible duplicates for the same occurrence.
- **Acceptance criteria:**
  - AC1: Re-processing the same trigger event does not create a second visible notification for the same occurrence.
  - AC2: Marking a notification read is persisted and does not affect other users.
  - AC3: A user cannot query another user's notification history.
- **PRD reference:** §7.1.10.
- **Status:** Exactly-once-per-occurrence proposed; history/read requirements Confirmed as functional areas.

### 6.11 Module 11 — Completion Verification

#### FR-COMPLETE-001 — Completion eligibility conditions

- **Statement:** The system shall consider an internship eligible for completion verification only when every mandatory requirement in its applicable requirement set is satisfied (FR-COMP-002) and no mandatory requirement remains missing, unapproved, in outstanding-correction state, or unevaluated.
- **Actor:** System (eligibility computation).
- **Preconditions:** Requirement set resolved; evaluation complete.
- **Expected behavior:** Eligibility is derived from compliance evaluation; optional requirements never block eligibility; unevaluated mandatory requirements block eligibility.
- **Postconditions:** Eligibility state available to authorized verifiers.
- **Validation and error behavior:** Missing requirement configuration → not eligible (configuration-missing state), never eligible.
- **Acceptance criteria:**
  - AC1: With all mandatory requirements satisfied, the internship is eligible.
  - AC2: With any mandatory requirement unsatisfied or unevaluated, the internship is not eligible.
  - AC3: Optional unsatisfied requirements do not affect eligibility.
- **PRD reference:** §11.1 (completion), §7.1.8, §8 step 9, §15.3.
- **Status:** Confirmed.

#### FR-COMPLETE-002 — Authorized verification role

- **Statement:** The system shall permit completion verification only by a Faculty Coordinator assigned to the internship's cohort, consistent with PRD-established authority; College Administrator verification authority requires OD-08 and is not granted by default.
- **Actor:** Faculty Coordinator (assigned cohort); College Administrator (open decision).
- **Preconditions:** Internship eligible (FR-COMPLETE-001); actor is an assigned coordinator.
- **Expected behavior:** The assigned coordinator can record completion verification. Administrator attempts are refused pending OD-08.
- **Postconditions:** Verification attributed to an authorized actor.
- **Validation and error behavior:** Unauthorized actor (student, admin pending decision, non-assigned faculty) → authorization failure; ineligible internship → business validation failure (FR-COMPLETE-004).
- **Acceptance criteria:**
  - AC1: An assigned coordinator verifies an eligible internship successfully.
  - AC2: A student cannot verify completion.
  - AC3: A College Administrator cannot verify completion under default behavior (and this AC is revised if OD-08 grants it).
  - AC4: A non-assigned coordinator cannot verify.
- **PRD reference:** §8 step 9, §6.2, §11.1.
- **Status:** Confirmed for faculty; Open decision for administrator (OD-08).

#### FR-COMPLETE-003 — Completing the verification action

- **Statement:** The system shall record completion verification with the verifying user, timestamp, and the evaluated requirement results at the time of verification, and shall set the internship's completion state to verified/complete.
- **Actor:** Faculty Coordinator (assigned).
- **Preconditions:** FR-COMPLETE-001 and FR-COMPLETE-002 satisfied.
- **Expected behavior:** Completion record created referencing the internship and verifier; report/document data unchanged; audit event `completion.verified` recorded (FR-AUDIT-001); owning student notified (FR-NOTIF-001); completion status visible per FR-COMPLETE-006.
- **Postconditions:** Internship marked verified complete; subsequent edits to its reports/documents/registration are refused (Module 6/5 edit rules; post-verification change handling is FR-COMPLETE-005/OD-08).
- **Validation and error behavior:** Verification failure at persistence → no partial completion state; ineligible → rejected (FR-COMPLETE-004).
- **Acceptance criteria:**
  - AC1: Successful verification creates a completion record with verifier and timestamp, emits an audit event, and notifies the student.
  - AC2: After verification, attempted report/document/internship edits are refused.
- **PRD reference:** §8 step 9, §7.1.11, §11.1, §9 (record preservation).
- **Status:** Confirmed.

#### FR-COMPLETE-004 — Ineligible completion attempts

- **Statement:** The system shall reject a completion-verification attempt when any mandatory requirement is unsatisfied or unevaluated, returning the list of outstanding mandatory requirements, and shall not change the internship's state.
- **Actor:** System (enforcement) for any would-be verifier.
- **Preconditions:** Internship not eligible; verification attempted.
- **Expected behavior:** Request refused with the outstanding-requirement list (same source as FR-COMP-008); no completion record created; option to log the denied attempt under OD-06.
- **Postconditions:** Completion state unchanged; no partial verification record.
- **Validation and error behavior:** Business validation failure (ineligibility) distinct from authorization failure; a dashboard showing all-satisfied progress that nonetheless lacks formal verification remains ineligible only if evaluation says so — completion is never inferred from display state (FR-COMPLETE-006).
- **Acceptance criteria:**
  - AC1: Verification with one unsatisfied mandatory requirement fails and lists exactly that requirement.
  - AC2: No completion record or state change results.
  - AC3: A progress bar at "100%" without satisfied-and-evaluated mandatory requirements does not permit verification.
- **PRD reference:** §11.1, §15.1 (completion prevented until requirements satisfied), §7.1.11.
- **Status:** Confirmed.

#### FR-COMPLETE-005 — Effects of corrections or requirement changes after verification

- **Statement:** The system shall prevent new workflow modifications after verification under default rules, and shall not apply any automatic reversal of verified completion until OD-08/OD-03 establish the policy.
- **Actor:** System.
- **Preconditions:** Internship verified complete; a late-arriving correction, replacement, or requirement change event occurs.
- **Expected behavior:** Interim behavior: verified completion is immutable in the MVP. Any requirement change affecting an already-verified internship is audited but does not silently un-verify it. Whether completion can be reopened/reversed is OD-08 (and OD-03 for requirement-change propagation).
- **Postconditions:** Completion state stable; change events audited.
- **Validation and error behavior:** Attempts to reverse completion → refused as unsupported transition (no fabricated reversal path).
- **Acceptance criteria:**
  - AC1: No implemented operation reverses a verified completion.
  - AC2: Post-verification requirement changes are recorded without altering stored student records.
  - AC3: Post-OD-08 tests assert the decided reversal policy.
- **PRD reference:** §18.1 (record lifecycle), §7.1.11, §12.
- **Status:** Open decision (OD-08).

#### FR-COMPLETE-006 — Completion status and its distinction from progress

- **Statement:** The system shall expose an explicit completion status distinct from compliance progress or dashboard progress indicators, and shall not represent unverified internships as complete.
- **Actor:** System; viewing roles per Section 4.2.11.
- **Preconditions:** Internship exists.
- **Expected behavior:** Status values: not verified (with eligibility indication derived from FR-COMPLETE-001) versus verified complete. Dashboards, reports, and exports label progress separately from verification; no surface equates "all progress complete" with "completion verified."
- **Postconditions:** Authorization of completion is always traceable to a verification record.
- **Validation and error behavior:** Missing/unevaluated data never yields verified status.
- **Acceptance criteria:**
  - AC1: An internship with all mandatory requirements satisfied but no verification action shows "not verified" (eligible).
  - AC2: Only FR-COMPLETE-003 transitions to verified complete.
  - AC3: Reports/exports distinguish the two states in dedicated fields.
- **PRD reference:** §7.1.11, §8 step 9, §15.4.
- **Status:** Confirmed.

#### FR-COMPLETE-007 — Prevention of unauthorized completion verification

- **Statement:** The system shall reject any completion-verification attempt from a user who is not an authorized verifier (FR-COMPLETE-002), even if the internship is eligible.
- **Actor:** System (enforcement).
- **Preconditions:** Verification request from unauthorized actor.
- **Expected behavior:** Authorization failure; no completion record; no state change; optionally logged under OD-06.
- **Postconditions:** Completion state unchanged.
- **Validation and error behavior:** Authorization failure, distinct from ineligibility validation failure.
- **Acceptance criteria:**
  - AC1: Every role except the assigned coordinator (and administrator only if OD-08 grants it) receives authorization failure on verification attempts.
  - AC2: Forged/direct requests cannot create completion records.
- **PRD reference:** §5.1 goal 5, §11.1, §15.3.
- **Status:** Confirmed.

#### FR-COMPLETE-008 — Verification history

- **Statement:** The system shall retain a verification history for each internship recording the verification action, verifier identity, timestamp, and the requirement results at verification; the history is append-only through application workflows.
- **Actor:** System (recording); viewing roles per Section 4.2.11 (student own; faculty assigned; admin deployment).
- **Preconditions:** At least one verification action (or attempted action, per OD-06 logging).
- **Expected behavior:** History entries are viewable by authorized roles and cannot be edited or deleted; if only one verification is possible, history still records that event with full attribution.
- **Postconditions:** Verification always traceable to a responsible, authorized actor.
- **Validation and error behavior:** Attempts to edit/delete verification history → authorization failure.
- **Acceptance criteria:**
  - AC1: Verification history shows verifier and timestamp.
  - AC2: No application operation alters or removes a verification history entry.
  - AC3: Owning student can view own verification status/history (PR-03).
- **PRD reference:** §7.1.11, §7.1.12, §9 (record preservation).
- **Status:** Confirmed for recording; viewer set partly Proposed (PR-03) and OD-06.

### 6.12 Module 12 — Audit History

#### FR-AUDIT-001 — Minimum required audit events

- **Statement:** The system shall record audit events for the following MVP actions, classified as Required (mandated by the workflow/completion/administration rules) and Proposed (candidate events pending OD-06 confirmation):

  **Required minimum set (Confirmed):**

  - Report submitted (`report.submitted`)
  - Correction requested (`report.correction_requested`)
  - Report resubmitted (`report.resubmitted`)
  - Report approved (`report.approved`)
  - Document submitted (`document.submitted`)
  - Completion verified (`completion.verified`)
  - User created (`user.created`)
  - User role changed (`user.role_changed`)
  - Cohort requirement created/changed (`requirement.created`, `requirement.updated`)
  - Cohort created/updated including coordinator assignment (`cohort.created`, `cohort.updated`, `cohort.coordinator_assigned`)

  **Proposed additional events (confirm in OD-06):** internship created/edited; cohort membership added/changed; document replaced/reviewed; user deactivated; compliance-affecting internship edits; data export executed; denied authorization attempts; ineligible completion attempt.

- **Actor:** System.
- **Preconditions:** Corresponding action completes.
- **Expected behavior:** Required events are written atomically with their action (FR-REVIEW-002) so no recorded action lacks an audit entry and no audit entry exists for a failed action (except denied-attempt logging if adopted).
- **Postconditions:** Audit records available for authorized viewing.
- **Validation and error behavior:** Audit persistence failure fails the action as a technical failure (no silent unaudited action for the required set).
- **Acceptance criteria:**
  - AC1: Each required event type produces exactly one audit record per occurrence with correct actor/entity/timestamp.
  - AC2: Proposed events are present only if OD-06 adopts them; tests assert the required set regardless.
- **PRD reference:** §7.1.12, §11.1 (audit history established decision), §18.2 (audit event coverage).
- **Status:** Required set Confirmed (workflow/user/requirement events); extended set Proposed (OD-06).

#### FR-AUDIT-002 — Event content and before/after values

- **Statement:** The system shall record for each audit event: event type, actor (user identity, or "system" for system-originated events), timestamp, affected entity type and identifier, and relevant before/after values for state changes where applicable.
- **Actor:** System.
- **Preconditions:** Event recorded.
- **Expected behavior:** State-changing administrative events (role change, requirement change, coordinator assignment) include before/after values sufficient to reconstruct what changed. Content-level events (report submission/approval) record references and state transitions rather than full document contents.
- **Postconditions:** Audit record supports "what happened, to what, by whom, when, and what changed."
- **Validation and error behavior:** Missing required audit attributes → record rejected (treated as technical fault) rather than stored incompletely.
- **Acceptance criteria:**
  - AC1: A role change audit entry shows previous and new role.
  - AC2: A report approval entry shows the report reference, actor, and timestamp.
  - AC3: Every entry has event type, actor (or system), and timestamp.
- **PRD reference:** §7.1.12.
- **Status:** Confirmed (attribute catalogue refinable in Database Design Document; no retention/immutability technology assumed).

#### FR-AUDIT-003 — Event attribution

- **Statement:** The system shall associate each audit event with the responsible user where an acting user exists, and mark system-originated events explicitly.
- **Actor:** System.
- **Preconditions:** Event recorded.
- **Expected behavior:** Attribution persists even if the user is later deactivated or has their role changed (FR-USER-005); attribution never changes retroactively.
- **Postconditions:** Events always resolvable to an actor or "system."
- **Validation and error behavior:** N/A (attribution is mandatory).
- **Acceptance criteria:**
  - AC1: Deactivating a user does not blank or change their authorship on past audit events.
  - AC2: Automated events (if any) are marked as system-originated.
- **PRD reference:** §7.1.12.
- **Status:** Confirmed.

#### FR-AUDIT-004 — Authorized viewing of audit history

- **Statement:** The system shall allow only authorized users to view audit history, scoped per Section 4.3 (item 11) and OD-06: proposal — College Administrator deployment-wide and Faculty Coordinator for assigned-cohort records; students denied. The final mapping is OD-06.
- **Actor:** College Administrator; Faculty Coordinator (subject to decision); Students denied.
- **Preconditions:** Authenticated; scope resolved.
- **Expected behavior:** Authorized users can view the events relevant to their scope; no out-of-scope events returned; viewing does not modify events.
- **Postconditions:** Audit visibility restricted per the decided mapping.
- **Validation and error behavior:** Out-of-scope or unauthorized viewing → authorization failure.
- **Acceptance criteria:**
  - AC1: Under the proposal, admin views deployment-wide events; faculty views only assigned-cohort-related events; student access is denied.
  - AC2: Final AC set is updated to match OD-06.
- **PRD reference:** §7.1.12 ("allow authorized users to review relevant history"; exact permissions to be defined).
- **Status:** Proposed with Open decision (OD-06).

#### FR-AUDIT-005 — Protection against editing or deleting audit events

- **Statement:** The system shall prevent all application-level modification or deletion of recorded audit events by any role, including administrators.
- **Actor:** System (enforcement) for all roles.
- **Preconditions:** Edit/delete request targeting an audit event through application workflows.
- **Expected behavior:** Request refused; no exposure of an editing interface; no API or UI path mutates or removes events. Tamper-evidence technology (e.g., append-only storage) is an implementation concern for the Database Design/Security Requirements and is not assumed here.
- **Postconditions:** Audit history preserved as written.
- **Validation and error behavior:** Authorization failure (or absence of the operation entirely).
- **Acceptance criteria:**
  - AC1: No role can edit or delete an audit event through application workflows.
  - AC2: Automated tests confirm no exposed operation performs audit mutation.
- **PRD reference:** §7.1.12 (prevent ordinary users from modifying/deleting; MVP applies this to all roles).
- **Status:** Confirmed for ordinary users; explicit admin denial is a strengthening (Proposed) that this FRD recommends.

#### FR-AUDIT-006 — Retention

- **Statement:** The retention duration for audit history is not established by the PRD and is therefore not defined by this FRD. Retention is a distinct concern from audit immutability (FR-AUDIT-005) and audit visibility (FR-AUDIT-004): this requirement governs only how long events must be kept, not whether they may be altered or who may read them.
- **Actor:** System/operations.
- **Preconditions:** Audit events exist.
- **Expected behavior:** Within the MVP there is no automated deletion, purge, or archival operation for audit events, so recorded events remain present for the duration of the demonstration. This "no purge implemented" behavior is an interim operational statement only; it is not a retention policy and must not be cited as an agreed retention duration. A formal retention, archival, or purge policy is deferred to OD-11 and the Database Design/Deployment and Operations documents.
- **Postconditions:** No retention period is asserted; no audit event is deleted by any MVP operation.
- **Validation and error behavior:** Not applicable. No requirement in this document authorizes or assumes deletion of audit events.
- **Acceptance criteria:**
  - AC1: No implementation defines, hardcodes, or displays a specific retention duration.
  - AC2: No MVP operation deletes or purges audit events; tests do not assert retention-expiry behavior until OD-11 resolves.
  - AC3: Retention decisions do not weaken audit immutability (FR-AUDIT-005) or audit visibility (FR-AUDIT-004).
- **PRD reference:** §18.2 (record retention explicitly open), §7.1.12.
- **Status:** Open decision (OD-11); interim "no purge" behavior Proposed; no legal or regulatory retention claim is made.

### 6.13 Module 13 — Reports and CSV Exports

#### FR-EXPORT-001 — MVP operational report catalogue

- **Statement:** The system shall provide basic operational reports for internship progress and compliance; the exact MVP catalogue, filters, and columns are proposed below and require confirmation under OD-07.
- **Actor:** Authorized viewers/exporters per FR-EXPORT-002.
- **Proposed catalogue (requires review — OD-07):**
  - `RPT-01 Internship Progress Overview` — one row per internship: student identity reference, cohort, internship start/end dates, count of required periods approved, count outstanding, completion-verification status.
  - `RPT-02 Student Compliance Summary` — one row per internship: mandatory requirements total/satisfied/outstanding, per-category outstanding list, outstanding-correction count, compliance status.
  - `RPT-03 Weekly Report Status` — one row per report: student, cohort, reporting period, report state, submission/resubmission/approval timestamps, approver identity.
- **Preconditions:** Authorized viewer; records in scope exist.
- **Expected behavior:** Reports reflect the same compliance evaluation source as dashboards (FR-DASH-006).
- **Postconditions:** No report row includes out-of-scope records.
- **Validation and error behavior:** Unknown report type → business validation error.
- **Acceptance criteria:**
  - AC1: Each proposed report renders with its defined columns.
  - AC2: RPT-02 rows match dashboard compliance figures for the same scope.
  - AC3: Post-OD-07, the catalogue/filters/columns match the decided set.
- **PRD reference:** §7.1.13, §10 (Reports and CSV Exports).
- **Status:** Proposed; Open decision (OD-07).

#### FR-EXPORT-002 — Report and export access rules

- **Statement:** The system shall restrict report viewing and CSV export per Section 4.2.13: College Administrator deployment-wide; Faculty Coordinator assigned-cohort data only; Student access to operational reports is an open decision (OD-07).
- **Actor:** College Administrator; Faculty Coordinator; Student (pending OD-07).
- **Preconditions:** Authenticated; scope resolved.
- **Expected behavior:** Access is enforced on the report/export operation itself, not just the entry point.
- **Postconditions:** No out-of-scope report or export is produced.
- **Validation and error behavior:** Unauthorized report/export request → authorization failure.
- **Acceptance criteria:**
  - AC1: Admin exports include deployment-scope records; faculty exports include only assigned-cohort records.
  - AC2: Faculty cannot export an unassigned cohort's data by parameter manipulation.
  - AC3: Student report access behavior matches OD-07's decision.
- **PRD reference:** §7.1.13, §6.3, §5.1 goal 5.
- **Status:** Admin/faculty Confirmed; faculty scope PR-01 pending; student access OD-07.

#### FR-EXPORT-003 — Exports respect requesting-user permissions (row scoping)

- **Statement:** The system shall ensure every export contains only rows the requesting user is permitted to see; out-of-scope data shall be excluded (scope-limited) or the export refused with an authorization failure consistent with the chosen policy.
- **Actor:** System (enforcement).
- **Preconditions:** Export request.
- **Expected behavior:** Row filtering by requester scope occurs during export generation; no export file contains out-of-scope records; the chosen behavior (silent scoping vs. refusal on attempted out-of-scope request) is applied consistently and documented in the API Specification.
- **Postconditions:** Exported file respects permissions (PRD requirement).
- **Validation and error behavior:** Attempt to export out-of-scope records → excluded rows and/or authorization failure per the documented policy; never out-of-scope rows.
- **Acceptance criteria:**
  - AC1: A faculty export contains zero rows from unassigned cohorts.
  - AC2: An admin export contains no records outside the deployment scope.
  - AC3: Behavior for an out-of-scope request matches the documented policy in all cases.
- **PRD reference:** §7.1.13 ("ensure exported information respects the requesting user's data-access permissions"), §15.3.
- **Status:** Confirmed for the requirement; exact policy (scope-limit vs. refuse) is Proposed pending review.

#### FR-EXPORT-004 — CSV format and safe value representation

- **Statement:** The system shall produce CSV exports with a header row, one record per row, and values safely represented; in particular, cells whose values begin with characters that spreadsheet applications interpret as formulas shall be neutralized to prevent formula injection.
- **Actor:** System.
- **Preconditions:** Export produced.
- **Expected behavior:** Export uses a defined encoding and delimiter; text values containing delimiters, quotes, or line breaks are quoted/escaped correctly; values starting with `=`, `+`, `-`, or `@` (and other spreadsheet-significant prefixes adopted at implementation) are neutralized so they are treated as text when opened in a spreadsheet.
- **Postconditions:** CSV opens as intended and cannot execute injected formulas.
- **Validation and error behavior:** Any value failing to serialize is handled without corrupting the file (export fails cleanly per FR-EXPORT-007).
- **Acceptance criteria:**
  - AC1: A field containing `=1+1` (or a CSV formula payload) is neutralized in the output and does not execute.
  - AC2: Values containing commas, quotes, and newlines round-trip correctly.
  - AC3: Header row present in every export.
- **PRD reference:** §7.1.13; safety expectation implied by security principles (PRD §12).
- **Status:** Confirmed requirement; exact encoding/delimiter and full prefix-neutralization set recorded in the API Specification.

#### FR-EXPORT-005 — Invalid or missing filters

- **Statement:** The system shall validate export/report filter parameters and shall reject invalid filter values with a business validation error rather than producing a misleading result.
- **Actor:** System.
- **Preconditions:** Filtered report/export request.
- **Expected behavior:** Invalid filter values (unknown cohort, unknown report type, malformed date/period) → validation error identifying the problem; no file is produced; no fallback to an unfiltered export.
- **Postconditions:** No misleading export exists.
- **Validation and error behavior:** Business validation failure (distinct from authorization failure for out-of-scope filter targets).
- **Acceptance criteria:**
  - AC1: An unknown cohort/period filter fails with a validation error and no export file.
  - AC2: The system never silently ignores an unrecognized filter.
- **PRD reference:** §7.1.13, §12.
- **Status:** Confirmed.

#### FR-EXPORT-006 — Empty results

- **Statement:** The system shall produce a valid, well-formed export when a permitted report/export contains zero rows, representing the empty result unambiguously.
- **Actor:** System.
- **Preconditions:** Valid request that yields no rows.
- **Expected behavior:** Proposed: export completes with the header row only (valid empty CSV), optionally accompanied by an "no records matched" indication in the initiating interface; no error is raised for a legitimately empty result.
- **Postconditions:** Empty export is a success, not a failure.
- **Validation and error behavior:** Absence of records is not treated as a validation error.
- **Acceptance criteria:**
  - AC1: A valid export with no matching rows produces a file containing only the header row.
  - AC2: Empty-result handling is distinguishable from export failure.
- **PRD reference:** §7.1.13.
- **Status:** Proposed (header-only convention) pending review.

#### FR-EXPORT-007 — Export failure handling

- **Statement:** The system shall handle export-generation failures (data retrieval error, serialization error, storage/streaming error) by returning a clear technical failure outcome and producing no corrupt or partial file.
- **Actor:** System.
- **Preconditions:** Export generation error.
- **Expected behavior:** The user learns the export failed; no partial file is presented as complete; no out-of-scope data leaks through error output; the failure may be logged for operations.
- **Postconditions:** No corrupt export file exists from a failed attempt.
- **Validation and error behavior:** Technical failure category (Section 7.1), distinct from validation and authorization failures.
- **Acceptance criteria:**
  - AC1: Simulated serialization/storage failure yields an explicit failure and no file.
  - AC2: Error output contains no out-of-scope record data.
- **PRD reference:** §7.1.13, §14.2.
- **Status:** Confirmed.

#### FR-EXPORT-008 — Export audit logging

- **Statement:** Whether data exports are recorded in audit history is an open decision; the system shall support the capability, and until adoption exports are not audited.
- **Actor:** System.
- **Preconditions:** Export executed.
- **Expected behavior:** If OD-06 adopts export auditing, each export produces an audit event (actor, export type, filters, timestamp, row count). Proposed until adopted.
- **Postconditions:** Per decision.
- **Validation and error behavior:** N/A.
- **Acceptance criteria:**
  - AC1: If adopted, exports produce attributable audit events.
  - AC2: Until adopted, no requirement depends on export audit records.
- **PRD reference:** §7.1.12, §7.1.13, §18.2 (audit event coverage).
- **Status:** Proposed; Open decision (OD-06).

## 7. Validation, Errors, and Edge Cases

### 7.1 Failure Categories

All FRD requirements use the following failure categories consistently. They are functional categories, not API structures; no error codes or response schemas are defined here (those belong to the API Specification and SRS). Section 7.2 also records outcomes that are not failures (for example, a legitimately empty export or a permitted edit); those rows name a success or state outcome rather than being forced into a failure category.

| Category | Meaning | Typical symptom | Examples |
|---|---|---|---|
| **Business validation failure** | The request is understood and permitted, but the data violates a defined business rule. | Explanatory message; no state change. | Missing required fields; end date before start date; duplicate report for a period; empty correction feedback; ineligible completion attempt. |
| **Authorization failure** | The requester's role or scope does not permit the operation. | Uniform access-denied outcome; no payload; no state change. | Student reading another student's records; faculty outside assigned cohort; student approving a report; admin approving a report. |
| **Invalid-transition failure** | The operation is permitted in general, but the record is not in a compatible state. | Invalid-transition outcome; no state change. | Approving a `DRAFT` report; any action on `APPROVED`; generic Submit on `CORRECTION_REQUESTED`. |
| **Technical failure** | Infrastructure, storage, persistence, or delivery failure. | Clear failure outcome; no partial/corrupt state; failure recorded. | File storage unavailable; email delivery failure; export serialization error; audit persistence failure. |
| **Configuration-missing state** | Required configuration does not exist. | Explicit "not configured" state; never a false compliant result. | Cohort without a requirement set; no deadline configured; requirement type with no evaluation rule. |

### 7.2 Edge and Error Cases

| # | Case | Category | Required behavior | Affected requirements |
|---|---|---|---|---|
| 1 | Missing required fields on any create/edit/submit | Business validation | Reject, name the field category, persist nothing | FR-USER-006, FR-INT-003, FR-REPORT-002/004, FR-COHORT-001 |
| 2 | Invalid dates (end before start, malformed) | Business validation | Reject with date-range/format error; no partial record | FR-INT-003, FR-COHORT-005 |
| 3 | Duplicate identity (user) | Business validation | Reject; exactly one record remains | FR-USER-006 |
| 4 | Duplicate report for same internship + period | Business validation | Reject creation/submission; state preserved | FR-REPORT-006 |
| 5 | Duplicate submission request (repeat submit) | Invalid transition / idempotent | One `SUBMITTED` record, one timestamp | FR-REPORT-006, T8 |
| 6 | Unauthorized action (wrong role) | Authorization | Access denied; no state change; no disclosure | FR-AUTH-003/007 |
| 7 | Access to another student's records | Authorization | Denied; no content returned | FR-INT-006, FR-REPORT-007, FR-DOC-007, PR-01 |
| 8 | Access to unassigned cohort | Authorization | Denied; no content returned | FR-COHORT-006, FR-REVIEW-001, FR-DASH-003/005 |
| 9 | Submission after deadline | Business (interim) | Accepted under interim rule; timestamp recorded (OD-10 finalizes) | FR-REPORT-005 |
| 10 | Repeated submission requests after correction | Invalid transition | Reject; use resubmission flow | Section 5.4, FR-REPORT-008 |
| 11 | Invalid report transition (approve draft; act on approved) | Invalid transition | Reject; state/history unchanged | FR-REVIEW-007/010, Section 5.4 |
| 12 | File upload failure (storage error) | Technical | Clear failure; no document record; workflow unaffected | FR-DOC-002, FR-DOC-007 |
| 13 | Unsupported file type | Business validation | Reject with type error; category remains missing | FR-DOC-003 |
| 14 | Oversized file (configurable limit) | Business validation | Reject with size error; no storage | FR-DOC-003, OD-16 |
| 15 | Missing required document | Compliance state (non-failure) | Listed as missing; blocks compliance/completion | FR-DOC-006, FR-COMP-005, FR-COMPLETE-001 |
| 16 | Incomplete internship requirements | Business validation on verification | Verification refused with outstanding list | FR-COMPLETE-004 |
| 17 | Requirement change affecting existing internships | Open decision | Audit change; do not corrupt data; propagation per OD-03 | FR-COMP-009, FR-COHORT-007 |
| 18 | Deactivated user with existing records | Authorization + preservation | User blocked; records preserved and attributable | FR-AUTH-005, FR-USER-005, FR-AUDIT-003 |
| 19 | Empty report/export results | Success (empty) | Valid empty result (header-only CSV) | FR-EXPORT-006 |
| 20 | CSV export failure | Technical | Clear failure; no partial/corrupt file | FR-EXPORT-007 |
| 21 | Notification delivery failure | Technical (non-blocking) | Business action unaffected; failure recorded | FR-NOTIF-005/006 |
| 22 | Concurrent/repeated review actions (two approvals racing) | Invalid transition / consistency | Exactly one transition takes effect; only one approval recorded; the loser receives an invalid-transition/no-op outcome | FR-REVIEW-002/007, T3/T6, T8 |
| 23 | Premature completion verification attempt | Business validation | Refused with outstanding requirements; no completion record | FR-COMPLETE-004 |
| 24 | Unauthorized completion verification | Authorization | Refused; no completion record | FR-COMPLETE-007 |
| 25 | Duplicate notifications for one event occurrence | Technical/consistency | At most one visible notification per occurrence | FR-NOTIF-007 |
| 26 | Student not assigned to a cohort attempts internship registration | Business validation | Clear error; no record | FR-INT-001 |
| 27 | Requirement set not configured for a cohort | Configuration-missing | Explicit "not configured"; never compliant | FR-COMP-001, FR-COMP-010 |
| 28 | Audit persistence failure during an action | Technical | Action fails; no silent unaudited action for required events | FR-REVIEW-002, FR-AUDIT-001 |
| 29 | Out-of-scope filter parameter manipulation | Authorization/scoping | No out-of-scope rows returned | FR-DASH-005, FR-EXPORT-003 |
| 30 | Attempt to edit/delete audit or history records | Authorization | Refused; history unchanged | FR-AUDIT-005, FR-REVIEW-006 |
| 31 | Student revises a `CORRECTION_REQUESTED` report | Permitted edit (non-failure) | Content revised in place; state remains `CORRECTION_REQUESTED` until Resubmit (T4) | FR-REPORT-009, FR-REPORT-008 |

### 7.3 User-Visible Behavior Principles

- Every rejected operation leaves data unchanged (no partial writes) and returns an outcome in one of the Section 7.1 categories.
- Authorization failures must not disclose denied record content, and must not confirm or deny the existence of records outside the requester's scope.
- Configuration gaps and unevaluated data are shown explicitly rather than being hidden behind a default "compliant" or "complete" display.
- Error text is explanatory of the rule violated; it never asserts a policy that this FRD has left open.
- Technical failures are never presented as business validation results, and vice versa.

## 8. Traceability Matrix

### 8.1 PRD to FRD Traceability

| Module | PRD reference | FRD requirements | Acceptance evidence | Related open decisions |
|---|---|---|---|---|
| 1. Authentication and RBAC | §5.1, §7.1.1, §10, §15.3 | FR-AUTH-001 … FR-AUTH-007 | AC sets in §6.1; role-permission matrix §4.2.1; test cases for denied access | OD-15, OD-01 |
| 2. User Management | §6.3, §7.1.2, §10 | FR-USER-001 … FR-USER-008 | AC sets in §6.2; §4.2.2 | OD-01, OD-06 |
| 3. Internship Management | §6.1, §7.1.3, §8, §18.1 | FR-INT-001 … FR-INT-008 | AC sets in §6.3; §4.2.3 | OD-12, OD-13, AS-01 |
| 4. Cohort Management | §6.3, §7.1.4, §8, §18.1 | FR-COHORT-001 … FR-COHORT-007 | AC sets in §6.4; §4.2.4 | OD-03, OD-10, OD-14, AS-02, AS-03 |
| 5. Document Management | §7.1.5, §8 step 3–4, §11.2 | FR-DOC-001 … FR-DOC-008 | AC sets in §6.5; §4.2.5 | OD-05, OD-16 |
| 6. Weekly Reports and Work Logs | §6.1, §7.1.6, §8 step 5/7, §9 | FR-REPORT-001 … FR-REPORT-010 | AC sets in §6.6; state model §5; §4.2.6 | OD-10, OD-02 |
| 7. Faculty Review Workflow | §6.2, §7.1.7, §9, §11.1 | FR-REVIEW-001 … FR-REVIEW-010 | AC sets in §6.7; transition table §5.2; §4.2.7 | OD-02, OD-10, PR-01 |
| 8. Compliance Engine | §7.1.8, §9, §11.1, §18.1 | FR-COMP-001 … FR-COMP-010 | AC sets in §6.8; §4.2.8 | OD-03, OD-04, OD-05 |
| 9. Role-Specific Dashboards | §6.1–6.3, §7.1.9, §10 | FR-DASH-001 … FR-DASH-007 | AC sets in §6.9; §4.2.9 | PR-01, OD-01, OD-10 |
| 10. Notifications | §7.1.10, §11.2, §9 | FR-NOTIF-001 … FR-NOTIF-007 | AC sets in §6.10; §4.2.10 | OD-09, OD-10, OD-11 |
| 11. Completion Verification | §6.2, §7.1.11, §8 step 9, §11.1, §15.1 | FR-COMPLETE-001 … FR-COMPLETE-008 | AC sets in §6.11; §4.2.11 | OD-08, OD-03 |
| 12. Audit History | §7.1.12, §11.1, §18.2 | FR-AUDIT-001 … FR-AUDIT-006 | AC sets in §6.12; §4.2.12 | OD-06, OD-11 |
| 13. Reports and CSV Exports | §7.1.13, §10, §15.3, §16 | FR-EXPORT-001 … FR-EXPORT-008 | AC sets in §6.13; §4.2.13 | OD-07, OD-06, OD-01 |
| First major milestone | §9, §9.1, §15.1 | FR-REPORT-004/008, FR-REVIEW-003/004, FR-COMP-004/007/010, FR-NOTIF-001 | End-to-end test: submit → corrections → resubmit → approve → compliance update | OD-02, OD-04 |

### 8.2 Coverage Checks

| Check | Result | Notes |
|---|---|---|
| All 13 MVP modules have functional requirements | **Pass** | Modules 1–13 each have an FR section and a permission table. |
| PRD requirements without FRD coverage | **Pass — none identified** | Every PRD §7.1 module and §10 expectation maps to FRD requirements. PRD §8 step 10 (record preservation) is covered functionally by FR-USER-005, FR-AUDIT-006 and OD-11 (retention duration deliberately not invented). PRD §12 non-functional expectations are explicitly deferred to the SRS. |
| FRD requirements introducing unsupported scope | **Pass — none identified** | Post-MVP items (Company Mentor, attendance, analytics/PDF, extra channels, multi-college, integrations) appear only as exclusions. Compliance has no percentage/weighting; no rejection/reopening state invented. |
| Duplicate requirements | **Pass — none identified** | Requirement IDs are unique; overlapping topics (e.g., FR-DOC-008 and FR-COMP-005) are cross-referenced rather than duplicated. |
| Consistent identifiers | **Pass** | Uniform pattern `FR-<MODULE>-<NNN>`; modules AUTH, USER, INT, COHORT, DOC, REPORT, REVIEW, COMP, DASH, NOTIF, COMPLETE, AUDIT, EXPORT. |
| Verifiable requirements | **Pass with noted deferrals** | Every requirement has acceptance criteria. Requirements whose verification depends on OD-03, OD-04, OD-05, OD-08, or OD-10 state the interim behavior and note that the final test is asserted after the decision. |
| Traceability for every FRD requirement | **Pass** | Each requirement cites a PRD parent reference; the module-level matrix maps them together. |
| Report workflow single consistent state model | **Pass** | Section 5 states and transitions are used consistently in FR-REPORT, FR-REVIEW, FR-COMP, FR-NOTIF, FR-COMPLETE, FR-AUDIT. |

## 9. Open Decisions and Dependencies

### 9.1 Decision Index

| ID | Topic | Blocks | Recommended resolution stage |
|---|---|---|---|
| OD-01 | Role and permission boundaries | Authorization design, API access rules | Security Requirements + SRS (before Week 2 role enforcement hardening) |
| OD-02 | Approved-report modification, reopening, and rejection | Report module design, DB constraints, state model | System Architecture / Database Design (before Week 5–6) |
| OD-03 | Requirement ownership and change propagation | Compliance design, DB schema, audit rules | System Architecture / Database Design (before Week 3–4 cohort work) |
| OD-04 | Compliance statuses and calculation rules | Compliance engine, dashboard displays, completion | SRS + Database Design (before Week 8) |
| OD-05 | Document review requirements and effects | Document module, compliance inputs | SRS (before Week 7) |
| OD-06 | Audit event set and viewing permissions | Audit module, security, exports | SRS + Security Requirements (before Week 9–10; minimum events already implemented earlier) |
| OD-07 | Exact MVP reports and CSV columns | Reporting module, API Specification | SRS / API Specification (before Week 8) |
| OD-08 | Completion verification authority and reversal | Completion module, permissions | Security Requirements / SRS (before Week 10) |
| OD-09 | Notification triggers, timing, retries | Notification module | SRS (before Week 9) |
| OD-10 | Deadline and late-submission policies | Reports, notifications, compliance timeliness | SRS / product decision (before Week 5; affects Week 9) |
| OD-11 | Record and history retention | Data model, storage, legal posture | Database Design + Operations Guide (post-MVP posture acceptable for MVP, subject to review) |
| OD-12 | Internship field catalogue and lifecycle states | Internship module, DB schema | Database Design / API Specification (before Week 3–4) |
| OD-13 | Student–internship reassignment rules | Internship/DB relationships | Database Design (before Week 3–4) |
| OD-14 | Student cohort membership moves | Cohort/DB relationships, compliance history | Database Design (before Week 3–4) |
| OD-15 | Authentication mechanics (credentials, token/session lifetime, lockout) | Auth implementation | Security Requirements + SRS (Week 2) |
| OD-16 | Document file-type and size policy values | Upload validation configuration | Security Requirements / Deployment & Operations (Week 7) |

### 9.2 Decision Details

#### OD-01 — Role and permission boundaries

- **Question:** What are the exact data-scope boundaries for each role — specifically (a) whether faculty cross-cohort access is ever permitted, (b) how much access administrators have to sensitive student record content (reports, documents), (c) whether administrators may change their own role or deactivate themselves, and (d) whether audit access is granted to faculty?
- **Why it matters:** Every protected operation in Section 4 depends on these boundaries; the permission matrix currently marks several cells Proposed or Open.
- **Affected requirements:** FR-AUTH-003/004, FR-USER-006/007, FR-INT-006, FR-COHORT-006, FR-DOC-007, FR-REPORT-007, FR-REVIEW-001/006, FR-DASH-005, FR-AUDIT-004, FR-EXPORT-002/003.
- **Downstream dependencies:** Spring Security configuration, API authorization rules, query-level scoping, test cases for denial paths.
- **Blocks architecture/database/implementation?** Yes — authorization design and query scoping.
- **Recommended resolution stage:** Security Requirements, with the functional mapping confirmed in the SRS before access-control hardening.

#### OD-02 — Approved-report modification, reopening, and rejection

- **Question:** May an approved report ever be modified, reopened, superseded, or rejected, by whom, and under what conditions?
- **Why it matters:** Determines whether `APPROVED` is terminal, whether additional states/transitions and history semantics are required, and whether compliance contributions can change after approval.
- **Affected requirements:** Section 5 (states T7, §5.4, §5.5), FR-REPORT-009, FR-REVIEW-003/010, FR-COMP-004, FR-COMPLETE-005.
- **Downstream dependencies:** Report state persistence, review history model, compliance recalculation triggers, audit event types.
- **Blocks architecture/database/implementation?** Yes — state machine and DB representation.
- **Recommended resolution stage:** System Architecture / Database Design before the Week 5–6 report and review work; PRD §18.1 lists it as required before architecture/database finalization.

#### OD-03 — Requirement ownership and change propagation

- **Question:** Which requirement attributes are configurable at cohort level, who may create/change requirements, and how do requirement changes affect existing internships' compliance and completion eligibility (grandfather, retroactive, or versioned)?
- **Why it matters:** Compliance results and completion eligibility for existing students depend on this rule; the FRD deliberately asserts no propagation outcome.
- **Affected requirements:** FR-COHORT-004/007, FR-COMP-001/007/009, FR-COMPLETE-001/005, FR-AUDIT-001.
- **Downstream dependencies:** Requirement-set versioning in the data model, evaluation inputs, audit before/after values, migration behavior.
- **Blocks architecture/database/implementation?** Yes.
- **Recommended resolution stage:** System Architecture / Database Design before Week 3–4 cohort management.

#### OD-04 — Compliance statuses and calculation rules

- **Question:** What exact compliance statuses exist, how are they derived from mandatory/optional requirement results, and how are deadlines (once decided) reflected?
- **Why it matters:** Dashboards, reports, exports, and completion eligibility all display or depend on the status vocabulary.
- **Affected requirements:** FR-COMP-002/010, FR-DASH-006, FR-EXPORT-001/002, FR-COMPLETE-001/006.
- **Downstream dependencies:** Display contracts, export columns, completion eligibility gate.
- **Blocks architecture/database/implementation?** Partially — implementation of compliance displays.
- **Recommended resolution stage:** SRS with Database Design, before Week 8.

#### OD-05 — Document review requirements and effects

- **Question:** What review outcomes exist for documents, is document approval required for a document requirement to be satisfied, and may a document be replaced after review?
- **Why it matters:** Determines whether submission alone satisfies compliance requirements, which changes compliance and completion results.
- **Affected requirements:** FR-DOC-004/005/008, FR-COMP-005, FR-COHORT-004.
- **Downstream dependencies:** Document status model, compliance inputs, faculty review workflow breadth.
- **Blocks architecture/database/implementation?** Partially (document status model).
- **Recommended resolution stage:** SRS before Week 7.

#### OD-06 — Audit event set and viewing permissions

- **Question:** Beyond the required event set (FR-AUDIT-001), which additional events are recorded (internship edits, membership changes, document replacement/review, deactivation, exports, denied attempts), who may view audit history, and is audit history exportable?
- **Why it matters:** Determines audit coverage, storage volume, and access control; PRD §7.1.12 defers the exact event list and permissions.
- **Affected requirements:** FR-AUDIT-001/004, FR-EXPORT-008, FR-AUTH-007, FR-REVIEW-008, FR-COMPLETE-007, Section 4.2.12.
- **Downstream dependencies:** Audit table design, indexing, retention behavior (OD-11), authorization rules.
- **Blocks architecture/database/implementation?** Yes for the view-permission mapping; partially for the event list.
- **Recommended resolution stage:** SRS + Security Requirements; required minimum already implemented in Week 5–6 workflows.

#### OD-07 — Exact MVP reports and CSV columns

- **Question:** Which reports exist in the MVP (the FRD proposes RPT-01 to RPT-03), with which filters and columns, and may students access any operational report or export?
- **Why it matters:** Defines the reporting module's output contract and CSV schema.
- **Affected requirements:** FR-EXPORT-001/002, FR-DASH-006.
- **Downstream dependencies:** API Specification, test data, dashboard/report consistency tests.
- **Blocks architecture/database/implementation?** Partially.
- **Recommended resolution stage:** SRS / API Specification before Week 8.

#### OD-08 — Completion verification authority and reversal

- **Question:** May College Administrators verify completion; is verification by one coordinator sufficient or are multiple approvers required; may a verified completion be reversed and under what conditions?
- **Why it matters:** Completion is the terminal lifecycle event and must be exactly authorized (PRD §11.1). Reversal behavior affects post-verification immutability.
- **Affected requirements:** FR-COMPLETE-002/005/007/008, Section 4.2.11, FR-INT-004/007.
- **Downstream dependencies:** Permission matrix, completion record model, audit events.
- **Blocks architecture/database/implementation?** Yes for the authority mapping.
- **Recommended resolution stage:** Security Requirements / SRS before Week 10.

#### OD-09 — Notification triggers, timing, and reliability

- **Question:** Is the proposed trigger list complete; what timing/frequency applies to deadline reminders; what retry/duplicate-handling policy applies; what delivery guarantees (if any) are expected?
- **Why it matters:** Prevents invented reminders and unreliable expectations; PRD §18.2/§18.3 leave timing and guarantees open.
- **Affected requirements:** FR-NOTIF-001/005/006/007.
- **Downstream dependencies:** Scheduler design, email service configuration, in-app notification storage.
- **Blocks architecture/database/implementation?** Partially.
- **Recommended resolution stage:** SRS before Week 9.

#### OD-10 — Deadline and late-submission policies

- **Question:** What are the reporting cadence deadlines, grace periods, late-submission handling (block/flag/tolerate), and review deadlines, and how (if at all) do they affect compliance?
- **Why it matters:** Currently no timeliness rule exists; several requirements use interim behaviors (FR-REPORT-005, FR-REVIEW-009) that must be replaced.
- **Affected requirements:** FR-COHORT-005, FR-REPORT-005, FR-REVIEW-009, FR-NOTIF-001/004, FR-COMP-002/010, FR-DASH-002, FR-DASH-001.
- **Downstream dependencies:** Deadline data model, scheduler, notification content, compliance inputs.
- **Blocks architecture/database/implementation?** Partially (deadline storage).
- **Recommended resolution stage:** Product decision + SRS before Week 5, finalized before Week 9 notifications.

#### OD-11 — Record and history retention

- **Question:** How long are completed records, audit events, notifications, and document versions retained, and under what rules?
- **Why it matters:** Affects storage, archival, and whether history can be purged; PRD §18.2 leaves retention open.
- **Affected requirements:** FR-AUDIT-006, FR-NOTIF-004/007, FR-DOC-004, PRD §8 step 10.
- **Downstream dependencies:** Storage design, backup strategy (SRS/Operations).
- **Blocks architecture/database/implementation?** No for MVP — no purge operation is implemented, so retention does not block the MVP; a formal retention policy is required before operational deployment. This is an interim posture, not a retention duration.
- **Recommended resolution stage:** Database Design + Deployment and Operations Guide.

#### OD-12 — Internship field catalogue and lifecycle states

- **Question:** What fields does an internship record require beyond student, cohort, start/end dates, and descriptive details; and what lifecycle states exist between registration and verified completion?
- **Why it matters:** The FRD deliberately defines only two anchor states (registered, verified complete) and avoids inventing intermediate states.
- **Affected requirements:** FR-INT-001/004/007, FR-DASH-001, FR-COMP-001.
- **Downstream dependencies:** Database schema for internships, input validation, dashboard displays.
- **Blocks architecture/database/implementation?** Yes (DB schema).
- **Recommended resolution stage:** Database Design / API Specification before Week 3–4.

#### OD-13 — Student–internship reassignment rules

- **Question:** May a student be reassigned to a different internship or have an internship replaced; who may do it; what happens to existing reports, documents, and compliance records?
- **Why it matters:** Reassignment (audit candidate in FR-AUDIT-001) has no defined behavior; silently moving records would corrupt history.
- **Affected requirements:** FR-INT-002/005, FR-AUDIT-001, FR-COMP-009.
- **Downstream dependencies:** Relationship model, history preservation, cohort alignment.
- **Blocks architecture/database/implementation?** Partially.
- **Recommended resolution stage:** Database Design before Week 3–4.

#### OD-14 — Student cohort membership moves

- **Question:** May a student move between cohorts after records exist; what happens to the requirement set, deadlines, and prior compliance results; who authorizes the move?
- **Why it matters:** Membership drives the requirement set (FR-INT-008); undefined moves would silently change compliance.
- **Affected requirements:** FR-COHORT-002/007, FR-COMP-001/007, FR-AUDIT-001.
- **Downstream dependencies:** Membership history model, evaluation re-run rules.
- **Blocks architecture/database/implementation?** Partially.
- **Recommended resolution stage:** Database Design before Week 3–4.

#### OD-15 — Authentication mechanics

- **Question:** Which credential mechanism is used (local vs. external identity); how are accounts provisioned and activated and how are initial credentials established (administrator-set credentials, an invitation, or first-login activation); what are token/session lifetimes and renewal rules; what password policy applies; and is account lockout/rate limiting required?
- **Why it matters:** PRD establishes Spring Security but not mechanics; FRD requirements are written to avoid depending on them. Account creation (FR-USER-001) is separated from account activation and sign-in eligibility precisely because this decision is unresolved.
- **Affected requirements:** FR-AUTH-001/002/005/006, FR-USER-001/002.
- **Downstream dependencies:** Security configuration, user credential storage, deployment secrets.
- **Blocks architecture/database/implementation?** Yes for auth implementation.
- **Recommended resolution stage:** Security Requirements + SRS, Week 2.

#### OD-16 — Document file-type and size policy values

- **Question:** What concrete file types/extensions/MIME types are allowed and what is the maximum file size (per document category)? Are these configurable per cohort or global?
- **Why it matters:** FR-DOC-003 requires enforcement with configurable values; the FRD asserts no specific numbers.
- **Affected requirements:** FR-DOC-002/003, FR-DOC-004.
- **Downstream dependencies:** Upload validation config, storage quotas, user-facing guidance.
- **Blocks architecture/database/implementation?** No (mechanism is defined); yes before release.
- **Recommended resolution stage:** Security Requirements / Deployment and Operations Guide, Week 7.

### 9.3 Assumptions

| ID | Assumption | Where referenced | Review needed |
|---|---|---|---|
| AS-01 | A student has at most one active internship in the MVP. | FR-INT-002 | Confirm; multiple concurrent internships are not addressed by the PRD. |
| AS-02 | A student belongs to exactly one cohort at a time. | FR-COHORT-002 | Confirm; multi-cohort membership is not addressed. |
| AS-03 | A cohort has one primary Faculty Coordinator, with support for additional assignments as a permitted extension. | FR-COHORT-003 | Confirm; PRD uses "coordinators" (plural) without a cardinality rule. |
| AS-04 | Each user holds a single role in the MVP. | Section 3.4, FR-USER-002 | Confirm; combined roles are not addressed by the PRD. |
| AS-05 | Accounts are created by an administrator; no student self-registration flow is implemented. | FR-USER-001, FR-INT-001 | Confirm; PRD does not specify onboarding mechanics. |
| AS-06 | There is no cap on correction/resubmission cycles per report. | FR-REVIEW-005 | Confirm; PRD does not set a cycle limit. |

## 10. Stage 1.2 Acceptance Self-Check

| # | Stage 1.2 acceptance criterion | Status in this document |
|---|---|---|
| 1 | `docs/requirements/FRD.md` exists and is readable | Yes — this file |
| 2 | PRD referenced and preserved as baseline | Yes — §1, §2.3, §8; PRD unmodified |
| 3 | All 13 MVP modules have functional requirements | Yes — §6.1–6.13 |
| 4 | Consistent, unique requirement identifiers | Yes — `FR-<MODULE>-NNN`, checked in §8.2 |
| 5 | Three primary roles covered by a permission matrix | Yes — §4.1–4.4 |
| 6 | Weekly report workflow with consistent transition model | Yes — §5 (states, transitions, invalid cases) |
| 7 | Compliance and completion rules defined where supported | Yes — §6.8, §6.11; unresolved parts in OD-03/04/05/08 |
| 8 | Audit history and CSV exports have explicit minimum MVP functionality | Yes — §6.12, §6.13 |
| 9 | Validation failures and significant edge cases covered | Yes — §7 |
| 10 | Traceability to the PRD documented | Yes — §8.1 |
| 11 | Open decisions explicit and actionable | Yes — §9 (OD-01…OD-16, AS-01…AS-06) |
| 12 | MVP and post-MVP scope distinguished | Yes — §2.2 |
| 13 | No invented business policies | Yes — unsupported items isolated in §9 and marked Proposed; no percentages, grace periods, retry limits, retention periods, file limits, or durations invented |
| 14 | Markdown structure, tables, and internal links checked | Checked — heading structure, table integrity, requirement-ID uniqueness, and cross-references verified; results reported in the Stage 1.2 review report |
| 15 | Git changes inspected and reported | Performed after document creation and revision |
| 16 | No application code or unrelated files modified | Yes — only `docs/requirements/FRD.md` was created and revised; `docs/requirements/PRD.md` and all application code are untouched |