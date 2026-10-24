# InternFlow API Specification (Stage 3.1)

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | InternFlow API Specification |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-API-001 |
| Version | 0.1 (Stage 3.1) |
| Status | **Draft — Pending Review.** Design only; no endpoint in this document is implemented in Stage 3.1. |
| Last Updated | 2026-10-22 |
| Purpose | Define the HTTP/JSON contract for the Stage 3 user, company, cohort and internship management modules: resources, endpoints, request/response representations, validation, authorization scope, error mapping, and traceability to FRD/SRS requirements. This document is the API Specification deferred by SRS-IF-01 and referenced by IF-DEC-12. |
| Related Source Documents | PRD v1.0; FRD v1.0 (`docs/requirements/FRD.md`); SRS v1.0 (`docs/requirements/SRS.md`); `docs/architecture/SAD.md`; `docs/architecture/DDD.md` (especially §15); `docs/architecture/DECISIONS.md` (especially IF-DEC-12, IF-DEC-14, IF-DEC-16, IF-DEC-19, IF-DEC-21, IF-DEC-22). |
| Intended Audience | Backend and frontend implementers of Stage 3; test authors; reviewers of the Stage 3.1 design. |

**Review note:** This is a design contract, not an implementation report. It records no completed endpoints and no authorization enforcement. It intentionally leaves the items listed in §12 unresolved rather than inventing a policy. The contract may change after review; changes that alter a persisted field must also produce a new database migration (IF-DEC-20).

**Status conventions:** **Confirmed** = directly required by the PRD/FRD/SRS (source cited). **Proposed** = an API design choice offered because a contract is needed; requires approval. **Open** = blocked by an FRD open decision (OD-xx) or a decision register item; not designed as a working behavior.

## 2. Scope and Implementation Status

### 2.1 In scope for this contract

- User administration and self-service profile (FRD Module 2).
- Minimal company reference data added for internship registration (IF-DEC-21).
- Cohort administration, student membership, and faculty coordinator assignment (FRD Module 4).
- Internship registration and editing (FRD Module 3).
- Common error, pagination, authorization-scope, and validation conventions.

### 2.2 Out of scope

- Authentication implementation. The approved mechanics are IF-DEC-19; the sign-in/sign-out endpoints are owned by the Stage 2 authentication work and are summarized only as a dependency in §5.1. **This branch/worktree does not contain the Stage 2 backend authentication code**; protected endpoints cannot be exercised until that work is integrated.
- Cohorts' requirement sets (OD-03), deadline configuration values (OD-10), membership moves (OD-14), internship reassignment (OD-13), the full internship lifecycle state set (OD-12), completion verification (OD-08), reports, documents, compliance, notifications, audit viewing and exports. No endpoints for these are specified here; §12 names the blocking decision for each.
- Any HTML, multipart upload, CSV export, or webhook contract.

### 2.3 Implementation status (read before testing)

| Area | Status at Stage 3.1 |
|---|---|
| Database schema | `V1` migration exists and was executed against PostgreSQL 18.3 (DDD §15). |
| JPA entities/repositories | Not created. `ddl-auto` is `none`. |
| Controllers/services for the endpoints below | **Not implemented.** |
| Authentication/authorization | Not implemented in this branch; Stage 2 dependency. No endpoint below is currently protected by role or scope. |
| Contract tests | Not written. The migration test covers only the schema. |

## 3. API Conventions

### 3.1 Base path and versioning

- Base path: `/api` (matches the existing `GET /api/health`).
- Resource paths use plural nouns and lower-kebab-case path segments; JSON fields use `camelCase`.
- **Versioning is Open (IF-DEC-14).** This document uses unversioned `/api/...` paths provisionally, consistent with the single first-party frontend. No version prefix, media type, or header is decided here. If IF-DEC-14 selects a versioning scheme, every path in this document changes accordingly before implementation.

### 3.2 Media types and encoding

- Requests and responses use `application/json; charset=utf-8`, except `204 No Content` responses.
- Dates use ISO-8601 calendar dates (`YYYY-MM-DD`).
- Timestamps use ISO-8601 instants in UTC with an explicit offset (`YYYY-MM-DDTHH:MM:SSZ`).
- Enumerated values use the exact uppercase strings shown in this document.
- Identifiers are JSON numbers representing 64-bit database identities. Clients must not assume they are sequential or reusable.

### 3.3 Authentication, CSRF, and sessions (Stage 2 dependency)

When the Stage 2 authentication work is integrated, the following applies to every endpoint except `POST /api/auth/sign-in` and `GET /api/health`:

- A valid server-side session and `HttpOnly` session cookie are required (IF-DEC-19).
- Every state-changing request (`POST`, `PUT`, `PATCH`, `DELETE`) requires a valid CSRF token, supplied as the `X-XSRF-TOKEN` header from the `XSRF-TOKEN` cookie.
- Missing/expired authentication → `401` with `UNAUTHENTICATED`.
- Invalid/missing CSRF token → `403` with `CSRF_INVALID` (distinct from an authorization denial).
- Session idle timeout is 30 minutes and absolute lifetime is 8 hours (IF-DEC-19). Exact cookie `SameSite`/`Secure` values remain dependent on IF-DEC-10 and deployment topology.

### 3.4 Authorization model

Authorization is enforced server-side on **role + record scope**. UI visibility is never enforcement (FR-AUTH-003/004; SRS-FR-001; PR-01).

| Role | Record scope |
|---|---|
| `STUDENT` | Own user profile, own internship, own cohort (read-only), own cohort's active coordinator names. No cohort-wide listings. |
| `FACULTY_COORDINATOR` | Cohorts they are actively assigned to (via `cohort_coordinator_assignments.unassigned_at IS NULL`), and the students/internships in those cohorts. No deployment-wide access. |
| `COLLEGE_ADMINISTRATOR` | Deployment-wide user, company and cohort administration. Administrative status **never** grants report-approval or completion-verification authority (PR-05; FR-USER-007). |

Scope rules used by this contract:

1. **Students** may read and edit only their own records. A student never receives cohort-wide user or internship listings.
2. **Faculty** may read students and internships in assigned cohorts only; the faculty listing endpoint returns only students of assigned cohorts (FR-USER-008; PR-04 proposed).
3. **Administrators** may read all users, companies, cohorts and internships, but may not edit another student's internship (OD-01) or change their own role/deactivation state (OD-01).
4. **Out-of-scope single-resource requests return `404 NOT_FOUND`**, not `403`, so the API does not confirm the existence of records outside the caller's scope (FRD §7.3). `403 FORBIDDEN` is reserved for an operation the caller's role may never perform (for example, a student creating a user).
5. **Out-of-scope list filters do not leak rows.** A filter naming an unassigned cohort/user yields an empty, scope-limited result rather than an authorization error (FRD §7.2 case 29; FR-EXPORT-003).

### 3.5 Pagination, filtering, and sorting

List endpoints accept:

| Parameter | Type | Default | Rules |
|---|---|---|---|
| `page` | integer | `0` | Zero-based; `>= 0`. |
| `size` | integer | `20` | `1..100`. Values outside the range are rejected with `VALIDATION_FAILED`. |
| `sort` | string | Endpoint-specific | Allow-listed fields only (documented per endpoint); unknown fields are rejected. Format `field,asc` or `field,desc`. |

Paginated response envelope:

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

`totalElements` counts only rows visible within the caller's scope. No `Link` headers are required.

### 3.6 Concurrency

IF-DEC-16's optimistic locking applies to workflow concurrency (reports/reviews), not to the Stage 3 tables; `V1` has no version columns. Stage 3 edit endpoints therefore use last-write-wins and are **not** protected by `If-Match`/ETag. If reviewers require optimistic concurrency for these entities, a migration and a contract revision are required.

## 4. Error Contract

### 4.1 Response shape

All non-2xx responses use one JSON shape:

```json
{
  "timestamp": "2026-10-22T12:00:00Z",
  "status": 409,
  "code": "ACTIVE_INTERNSHIP_EXISTS",
  "category": "BUSINESS_VALIDATION",
  "message": "An active internship already exists for this student.",
  "fieldErrors": [
    { "field": "studentUserId", "message": "An active internship already exists." }
  ]
}
```

- `category` is exactly one of the FRD §7.1 categories, normalized as `BUSINESS_VALIDATION`, `AUTHORIZATION`, `INVALID_TRANSITION`, `TECHNICAL`, `CONFIGURATION_MISSING`.
- `fieldErrors` is present for field-level validation and omitted otherwise.
- `message` explains the violated rule and never asserts a policy that an open decision has not settled (FRD §7.3; SRS-ERR-014).
- No stack traces, SQL, credential material, or out-of-scope record content is returned (NFR-SEC-003; NFR-SEC-008).
- The error body never includes a `credentialHash`, password, or session identifier.

### 4.2 HTTP status mapping

| HTTP | `code` | `category` | When |
|---|---|---|---|
| `400` | `VALIDATION_FAILED` | `BUSINESS_VALIDATION` | Missing/malformed/out-of-range input, invalid date range, unsupported enum, malformed filter/sort. |
| `401` | `UNAUTHENTICATED` | `AUTHORIZATION` | No valid session (Stage 2 dependency). |
| `403` | `CSRF_INVALID` | `AUTHORIZATION` | Missing/invalid CSRF token on a state-changing request. |
| `403` | `FORBIDDEN` | `AUTHORIZATION` | The caller's role may never perform this operation. No record content is returned. |
| `403` | `FIELD_NOT_EDITABLE` | `AUTHORIZATION` | A self-service request attempts to change an identity/role/status field. |
| `404` | `NOT_FOUND` | `BUSINESS_VALIDATION` | The resource does not exist **or** is outside the caller's scope. |
| `405` | `METHOD_NOT_ALLOWED` | `BUSINESS_VALIDATION` | Unsupported method for an existing path. |
| `409` | `DUPLICATE_IDENTITY` | `BUSINESS_VALIDATION` | User identity already exists (FR-USER-006). |
| `409` | `DUPLICATE_COHORT_CODE` | `BUSINESS_VALIDATION` | Cohort code already exists (FR-COHORT-001). |
| `409` | `DUPLICATE_COMPANY_NAME` | `BUSINESS_VALIDATION` | Company name already exists, case-insensitively (IF-DEC-21). |
| `409` | `ACTIVE_MEMBERSHIP_EXISTS` | `BUSINESS_VALIDATION` | Student already has an active cohort membership (AS-02; FR-COHORT-002). |
| `409` | `ACTIVE_INTERNSHIP_EXISTS` | `BUSINESS_VALIDATION` | Student already has a `REGISTERED` internship (AS-01; FR-INT-002). |
| `409` | `DUPLICATE_COORDINATOR_ASSIGNMENT` | `BUSINESS_VALIDATION` | Faculty already actively assigned to that cohort. |
| `409` | `PRIMARY_COORDINATOR_EXISTS` | `BUSINESS_VALIDATION` | Cohort already has an active primary coordinator (AS-03). |
| `409` | `INVALID_STATE_TRANSITION` | `INVALID_TRANSITION` | Edit refused because the internship is `VERIFIED_COMPLETE` (FR-INT-007). |
| `409` | `DECISION_PENDING` | `BUSINESS_VALIDATION` | The operation is blocked by an identified open decision (self role change, self deactivation, membership move, reassignment). The message names the OD/PR reference. |
| `415` | `UNSUPPORTED_MEDIA_TYPE` | `BUSINESS_VALIDATION` | Request body is not JSON. |
| `500` | `TECHNICAL_FAILURE` | `TECHNICAL` | Unexpected persistence/technical failure; nothing partially written. |
| `503` | `CONFIGURATION_MISSING` | `CONFIGURATION_MISSING` | Required configuration (for example, sign-in persistence) is absent; never presented as success. |

Database uniqueness/check violations are translated to the appropriate `409`/`400` above; raw constraint names and SQLState values are never exposed. Note that PostgreSQL 18 reports `ON DELETE RESTRICT` violations as SQLSTATE `23001` (earlier versions: `23503`); both are technical failures (`500`) because Stage 3 exposes no delete operation for referenced records.

## 5. Endpoint Catalogue

### 5.1 Authentication dependency (owned by Stage 2; not implemented here)

These endpoints are **not designed by this document**. They are listed so implementers of the Stage 3 frontend know the dependency. Their contract must come from the Stage 2 authentication work, subject to IF-DEC-19.

| Method | Path | Access | Notes |
|---|---|---|---|
| `POST` | `/api/auth/sign-in` | Anonymous, CSRF-protected | Establishes a server-side session. Generic credential failure (FR-AUTH-001). Production currently reports configuration-missing (`503`) until persisted accounts exist. |
| `POST` | `/api/auth/sign-out` | Authenticated, CSRF-protected | Invalidates server-side session state and deletes the cookie. |

Carried Stage 2 limitations (not resolved here): persisted accounts/credential provisioning, rate-limiting thresholds, first-login password change, final `SameSite`/`Secure` values, and CORS/topology (IF-DEC-10). The Stage 3 API must not be deployed as if these were complete.

### 5.2 Users

#### 5.2.1 `GET /api/users/me`

Returns the authenticated caller's profile. **Allowed:** all roles (FR-USER-003).

```json
{
  "id": 42,
  "identity": "student@example.edu",
  "displayName": "Aarav Sharma",
  "role": "STUDENT",
  "accountStatus": "ACTIVE",
  "activationState": "ACTIVATED",
  "contactEmail": "student@example.edu",
  "contactPhone": null,
  "mustChangePassword": false,
  "createdAt": "2026-10-01T09:00:00Z",
  "updatedAt": "2026-10-20T11:30:00Z"
}
```

`credentialHash` is **never** returned. `mustChangePassword` reflects the first-login flag (IF-DEC-19); when `true`, the account must change the administrator-set temporary password before accessing most protected operations. The password-change endpoint and enforcement are **not implemented in Stage 3.1**; this field is returned for forward compatibility only when authentication is integrated.

#### 5.2.2 `PATCH /api/users/me`

Edits permitted self-service fields (FR-USER-003; PR-06 proposed). **Allowed:** all authenticated roles, own profile only.

Request (at least one field):

```json
{
  "displayName": "Aarav S.",
  "contactEmail": "aarav@example.edu",
  "contactPhone": "+91 98765 43210"
}
```

| Field | Rules |
|---|---|
| `displayName` | Optional; 1–200 characters after trimming; non-blank. |
| `contactEmail` | Optional; valid email; ≤254 characters; `null` clears. |
| `contactPhone` | Optional; ≤32 characters; `null` clears. |

`identity`, `role`, and `accountStatus` are **not editable** here; sending them returns `403 FIELD_NOT_EDITABLE`. Role and status changes are administrator operations (FR-USER-002/005). Returns `200` with the updated profile.

#### 5.2.3 `GET /api/users`

Lists/searches accounts (FR-USER-008). **Allowed:** `COLLEGE_ADMINISTRATOR` (all users); `FACULTY_COORDINATOR` (students of assigned cohorts only; PR-04 proposed). **Denied:** `STUDENT` (`403 FORBIDDEN`).

Query parameters: `role`, `accountStatus`, `query` (case-insensitive match on identity/display name), `cohortId`, `page`, `size`, `sort` (`displayName`, `identity`, `createdAt`; default `displayName,asc`).

Returns the paginated envelope with user objects. For faculty, `cohortId` may only name an assigned cohort; any other value yields an empty result.

#### 5.2.4 `POST /api/users`

Creates an account record and assigns its role (FR-USER-001). **Allowed:** `COLLEGE_ADMINISTRATOR` only.

Request:

```json
{
  "identity": "new.student@example.edu",
  "displayName": "New Student",
  "role": "STUDENT",
  "contactEmail": "new.student@example.edu",
  "contactPhone": null
}
```

| Field | Required | Rules |
|---|---|---|
| `identity` | Yes | Valid email; trimmed and stored lowercase; ≤254 characters; unique. Email is the approved login identity (IF-DEC-19). |
| `displayName` | Yes | 1–200 characters after trimming. |
| `role` | Yes | One of `STUDENT`, `FACULTY_COORDINATOR`, `COLLEGE_ADMINISTRATOR`. |
| `contactEmail` | No | Valid email; ≤254 characters. |
| `contactPhone` | No | ≤32 characters. |

Behavior:

- Creates the record with `accountStatus = ACTIVE` and `activationState = PENDING`.
- **Does not accept or return a password.** Creation alone does not make the account sign-in eligible (FR-USER-001). Credential provisioning (administrator-set temporary password, mandatory first-login change per IF-DEC-19) is part of the not-yet-defined authentication contract and is deferred; the user cannot sign in until that flow exists and completes.
- Duplicate identity → `409 DUPLICATE_IDENTITY`.
- Returns `201` with a `Location: /api/users/{id}` header and the user object.

#### 5.2.5 `GET /api/users/{userId}`

**Allowed:** the user themself; an administrator; a faculty coordinator when the target is a student in an assigned cohort. Otherwise `404 NOT_FOUND` (including when the user does not exist). Returns the user object.

#### 5.2.6 `PATCH /api/users/{userId}`

Administrator edit of directory fields (FR-USER-004). **Allowed:** `COLLEGE_ADMINISTRATOR` only. Accepts `displayName`, `contactEmail`, `contactPhone` with the same rules as §5.2.2. Does **not** accept `identity`, `role`, or `accountStatus` (`400 VALIDATION_FAILED` with a field error). Self-editing is available through §5.2.2. Returns `200` with the updated user.

#### 5.2.7 `PUT /api/users/{userId}/role`

Changes a user's role (FR-USER-002). **Allowed:** `COLLEGE_ADMINISTRATOR` only.

```json
{ "role": "FACULTY_COORDINATOR" }
```

- Invalid role → `400 VALIDATION_FAILED`.
- Changing **the caller's own role** → `409 DECISION_PENDING` (`OD-01`); no change is made. No behavior is invented until OD-01 resolves.
- Role changes take effect on the next protected request (session state is re-evaluated server-side; IF-DEC-19).
- Returns `200` with the updated user. The change must be audited when audit persistence exists (FR-USER-002; currently deferred, see DDD §15.4).

#### 5.2.8 `PUT /api/users/{userId}/status`

Activates/deactivates an account (FR-USER-005). **Allowed:** `COLLEGE_ADMINISTRATOR` only.

```json
{ "accountStatus": "DEACTIVATED" }
```

- Allowed values: `ACTIVE`, `DEACTIVATED`.
- Deactivation prevents authentication and protected operations but preserves all records and attribution (FR-USER-005 AC2). No user deletion endpoint exists (PR-08; FRD §4.2.2).
- Deactivating **the caller's own account** → `409 DECISION_PENDING` (`OD-01`).
- Returns `200` with the updated user.

### 5.3 Companies (minimal reference data — IF-DEC-21)

Companies are not an FRD module. They exist so internships can reference a stable host organisation without free-text duplication. No contact, industry, location, or mentor fields exist in the schema or contract.

#### 5.3.1 `GET /api/companies`

**Allowed:** all authenticated roles. Query: `status` (`ACTIVE`/`INACTIVE`; default `ACTIVE`), `query`, `page`, `size`, `sort` (`name`, default `name,asc`). Returns the paginated envelope of company objects:

```json
{
  "id": 7,
  "name": "Acme Labs",
  "status": "ACTIVE",
  "createdAt": "2026-10-01T09:00:00Z",
  "updatedAt": "2026-10-01T09:00:00Z"
}
```

Students/faculty see `ACTIVE` companies only; an explicit `status=INACTIVE` from those roles returns an empty page. Administrators may list either status. No deletion is exposed.

#### 5.3.2 `POST /api/companies`

**Allowed:** `COLLEGE_ADMINISTRATOR` only (proposed API rule; no FRD permission exists). Request: `{ "name": "Acme Labs" }`.

| Field | Required | Rules |
|---|---|---|
| `name` | Yes | 1–200 characters after trimming; case-insensitively unique. |

Duplicate → `409 DUPLICATE_COMPANY_NAME`. Returns `201` + `Location`.

#### 5.3.3 `PATCH /api/companies/{companyId}`

**Allowed:** `COLLEGE_ADMINISTRATOR` only. Accepts `name` and/or `status`. Deactivation is `status: "INACTIVE"`; referenced companies are never deleted (`ON DELETE RESTRICT`). Returns `200`.

### 5.4 Cohorts

#### 5.4.1 `GET /api/cohorts`

**Allowed:** administrators (all cohorts); faculty (assigned cohorts); students (own active cohort only). Query: `status`, `query`, `page`, `size`, `sort` (`cohortCode`, `name`, `periodStart`; default `cohortCode,asc`).

Cohort object:

```json
{
  "id": 3,
  "cohortCode": "C-2026-A",
  "name": "Computer Science 2026 A",
  "periodStart": "2026-01-01",
  "periodEnd": "2026-06-30",
  "status": "ACTIVE",
  "createdAt": "2026-10-01T09:00:00Z",
  "updatedAt": "2026-10-01T09:00:00Z"
}
```

`deadlineConfig` is **not** represented because OD-10 has not defined keys or values. It must not be exposed as an arbitrary JSON blob.

#### 5.4.2 `GET /api/cohorts/{cohortId}`

Scoped as §5.4.1; out-of-scope or nonexistent → `404 NOT_FOUND`.

#### 5.4.3 `POST /api/cohorts`

**Allowed:** `COLLEGE_ADMINISTRATOR` only (FR-COHORT-001).

```json
{
  "cohortCode": "C-2026-A",
  "name": "Computer Science 2026 A",
  "periodStart": "2026-01-01",
  "periodEnd": "2026-06-30"
}
```

| Field | Required | Rules |
|---|---|---|
| `cohortCode` | Yes | 1–50 characters after trimming; unique. |
| `name` | Yes | 1–200 characters after trimming. |
| `periodStart`, `periodEnd` | No | Both present or both absent; `periodEnd >= periodStart`. |

No `deadlineConfig` field is accepted (OD-10). Duplicate code → `409 DUPLICATE_COHORT_CODE`. Returns `201` + `Location`, with `status = ACTIVE`.

#### 5.4.4 `PATCH /api/cohorts/{cohortId}`

**Allowed:** `COLLEGE_ADMINISTRATOR` only. Accepts `cohortCode`, `name`, `periodStart`, `periodEnd`, `status` (`ACTIVE`/`INACTIVE`). Sending one period boundary without the other is `400 VALIDATION_FAILED`; sending both as `null` clears the period. Setting `status = INACTIVE` is the supported way to retire a cohort; deletion is not exposed (FR-INT-008 AC2; PR-08). Returns `200`.

No endpoint accepts requirement definitions (OD-03) or deadline values (OD-10).

#### 5.4.5 `GET /api/cohorts/{cohortId}/members`

Lists **active** memberships (FR-COHORT-002; FR-COHORT-006). **Allowed:** administrator; faculty assigned to that cohort. Students receive `403 FORBIDDEN` (students never receive cohort-wide listings).

Paginated envelope of:

```json
{
  "student": {
    "id": 42,
    "identity": "student@example.edu",
    "displayName": "Aarav Sharma",
    "role": "STUDENT",
    "accountStatus": "ACTIVE"
  },
  "joinedAt": "2026-10-05T09:00:00Z"
}
```

Only active students' identity/display name are exposed; no contact fields, credentials, or other-cohort memberships.

#### 5.4.6 `POST /api/cohorts/{cohortId}/members`

Initial student assignment (FR-COHORT-002). **Allowed:** `COLLEGE_ADMINISTRATOR` only.

```json
{ "studentUserId": 42 }
```

- Target must be an existing `STUDENT`; otherwise `400 VALIDATION_FAILED`.
- If the student already has an active membership (in this or any cohort) → `409 ACTIVE_MEMBERSHIP_EXISTS`. Moving/reassigning a student is **not supported** until OD-14 resolves; the error message cites OD-14 and makes no change.
- Returns `201` with the membership representation above.

No `DELETE` member endpoint is defined: unassignment/move is OD-14. The schema's `left_at` supports a future migration/contract without redesign.

#### 5.4.7 `GET /api/cohorts/{cohortId}/coordinators`

Lists active coordinator assignments (FR-COHORT-003). **Allowed:** administrator; faculty assigned to that cohort; students of that cohort (read-only, names only). Paginated envelope of:

```json
{
  "faculty": {
    "id": 9,
    "identity": "faculty@example.edu",
    "displayName": "Dr. Meera Iyer",
    "role": "FACULTY_COORDINATOR"
  },
  "isPrimary": true,
  "assignedAt": "2026-10-02T09:00:00Z"
}
```

Student-facing responses must not include `identity` (email); only `id` and `displayName` are required for display.

#### 5.4.8 `POST /api/cohorts/{cohortId}/coordinators`

Assigns a Faculty Coordinator to a cohort (FR-COHORT-003). **Allowed:** `COLLEGE_ADMINISTRATOR` only.

```json
{ "facultyUserId": 9, "isPrimary": true }
```

- Target must exist and hold `FACULTY_COORDINATOR`; otherwise `400 VALIDATION_FAILED`.
- Existing active assignment for the same cohort/faculty → `409 DUPLICATE_COORDINATOR_ASSIGNMENT`.
- `isPrimary = true` when an active primary already exists → `409 PRIMARY_COORDINATOR_EXISTS` (AS-03). To replace the primary, remove the existing assignment first, then assign the new primary (two operations, no implicit transfer).
- `isPrimary` defaults to `false`; multiple non-primary coordinators are allowed.
- Returns `201` with the assignment representation.

#### 5.4.9 `DELETE /api/cohorts/{cohortId}/coordinators/{facultyUserId}`

Withdraws scope (FR-COHORT-003). **Allowed:** `COLLEGE_ADMINISTRATOR` only. Sets `unassigned_at = now()`; the row is retained for history. Returns `204 No Content`. If no active assignment exists → `404 NOT_FOUND`.

### 5.5 Internships

#### 5.5.1 `GET /api/internships`

**Allowed:** student (own); faculty (assigned cohorts); administrator (all). Query: `studentUserId`, `cohortId`, `lifecycleState` (`REGISTERED`/`VERIFIED_COMPLETE`), `page`, `size`, `sort` (`startDate`, `endDate`, `createdAt`; default `startDate,desc`). Out-of-scope filters produce an empty page.

Internship object:

```json
{
  "id": 15,
  "student": {
    "id": 42,
    "identity": "student@example.edu",
    "displayName": "Aarav Sharma",
    "role": "STUDENT",
    "accountStatus": "ACTIVE"
  },
  "cohort": {
    "id": 3,
    "cohortCode": "C-2026-A",
    "name": "Computer Science 2026 A"
  },
  "company": {
    "id": 7,
    "name": "Acme Labs"
  },
  "startDate": "2026-01-05",
  "endDate": "2026-06-20",
  "title": "Software Engineering Intern",
  "description": "Backend platform team.",
  "lifecycleState": "REGISTERED",
  "createdAt": "2026-10-10T09:00:00Z",
  "updatedAt": "2026-10-10T09:00:00Z"
}
```

`company` is `null` when none was supplied. `student.identity` is included only for administrators and for the owning student; faculty responses omit it. (One representation is shown for brevity; the server must apply this field-level scoping.)

#### 5.5.2 `GET /api/internships/{internshipId}`

Scoped as §5.5.1. Out-of-scope or nonexistent → `404 NOT_FOUND`.

#### 5.5.3 `POST /api/internships`

Registers the caller's internship (FR-INT-001). **Allowed:** `STUDENT` only. Administrative backfill is an open decision (OD-01), so no admin variant is exposed.

```json
{
  "startDate": "2026-01-05",
  "endDate": "2026-06-20",
  "title": "Software Engineering Intern",
  "description": "Backend platform team.",
  "companyId": 7
}
```

| Field | Required | Rules |
|---|---|---|
| `startDate` | Yes | ISO date. |
| `endDate` | Yes | ISO date; `>= startDate` (equal allowed). |
| `title` | No | ≤200 characters after trimming. |
| `description` | No | ≤5000 characters. |
| `companyId` | No | Must reference an existing `ACTIVE` company; otherwise `400 VALIDATION_FAILED`. |

Behavior:

- The student and cohort are **derived server-side**: the internship is attached to the caller and the caller's active cohort membership. The request cannot name another student or cohort.
- No active cohort membership → `400 VALIDATION_FAILED` with field `cohort`, message citing FR-INT-001 (student must be assigned to a cohort first). Nothing is persisted.
- Existing `REGISTERED` internship → `409 ACTIVE_INTERNSHIP_EXISTS` (AS-01; FR-INT-002).
- `lifecycleState` is **not writable**; it is always `REGISTERED` on creation (FR-INT-004). Attempts to supply it → `400 VALIDATION_FAILED`.
- Returns `201` + `Location`.

#### 5.5.4 `PATCH /api/internships/{internshipId}`

Edits the caller's internship (FR-INT-007). **Allowed:** the owning `STUDENT` only. Faculty and administrators cannot edit internship details through this contract.

Accepted: `startDate`, `endDate`, `title`, `description`, `companyId` (§5.5.3 rules). Not accepted:

- `studentUserId`, `cohortId` — reassignment/membership moves are OD-13/OD-14. Any attempt → `400 VALIDATION_FAILED` with a message citing the open decision.
- `lifecycleState` — no direct state writes (FR-INT-004 AC3). Completion is not exposed in Stage 3.
- `companyId = null` clears the company.

If the internship is `VERIFIED_COMPLETE` → `409 INVALID_STATE_TRANSITION`; no change (FR-INT-007 AC2). Returns `200` with the updated object.

## 6. Request/Response Summary

| Resource | Create | Read | Update | Delete |
|---|---|---|---|---|
| Current user | — | `GET /api/users/me` | `PATCH /api/users/me` | — |
| Users | `POST /api/users` | `GET /api/users`, `GET /api/users/{id}` | `PATCH /api/users/{id}`, `PUT /api/users/{id}/role`, `PUT /api/users/{id}/status` | Not exposed (FR-USER-005; PR-08) |
| Companies | `POST /api/companies` | `GET /api/companies` | `PATCH /api/companies/{id}` | Not exposed (deactivate instead) |
| Cohorts | `POST /api/cohorts` | `GET /api/cohorts`, `GET /api/cohorts/{id}` | `PATCH /api/cohorts/{id}` | Not exposed (deactivate instead) |
| Memberships | `POST /api/cohorts/{id}/members` | `GET /api/cohorts/{id}/members` | Not exposed (OD-14) | Not exposed (OD-14) |
| Coordinator assignments | `POST /api/cohorts/{id}/coordinators` | `GET /api/cohorts/{id}/coordinators` | Not exposed | `DELETE /api/cohorts/{id}/coordinators/{facultyUserId}` |
| Internships | `POST /api/internships` | `GET /api/internships`, `GET /api/internships/{id}` | `PATCH /api/internships/{id}` | Not exposed (PR-08) |

## 7. Validation Rules

### 7.1 Shared rules

- Unknown JSON properties are rejected with `400 VALIDATION_FAILED` (fail-closed; typo protection). Clients must send only documented fields.
- Trimmed text fields that are required must be non-blank after trimming.
- Enum values are case-sensitive and must be one of the documented values.
- Null is accepted only where explicitly documented; otherwise it is `400 VALIDATION_FAILED`.
- Request body size limits are not defined by the source documents; deployment must set a safe reverse-proxy limit before production (Security Requirements / Deployment Guide, not invented here).

### 7.2 Field rules

| Field | Rule | Enforced in DB? |
|---|---|---|
| `users.identity` | valid email, lowercase, ≤254, unique | Yes (normalization + unique) |
| `users.displayName` | non-blank, ≤200 | Yes |
| `users.role` | three MVP roles only | Yes |
| `users.accountStatus` | `ACTIVE`/`DEACTIVATED` | Yes |
| `users.contactEmail` | valid email, ≤254 | API only |
| `users.contactPhone` | ≤32 characters | API only |
| `cohorts.cohortCode` | non-blank, ≤50, unique | Yes |
| `cohorts.name` | non-blank, ≤200 | Yes |
| `cohorts.periodStart/End` | both or neither; end ≥ start | Yes |
| `companies.name` | non-blank, ≤200, case-insensitively unique | Yes |
| `internships.startDate/endDate` | required; end ≥ start | Yes |
| `internships.title` | ≤200 (API guardrail) | No |
| `internships.description` | ≤5000 (API guardrail) | No |

The API guardrails on `title`/`description` are input-safety choices, not domain rules; they are not encoded as database constraints because the internship field catalogue is OD-12.

## 8. State and Side Effects

- `POST /api/internships` always creates `lifecycleState = REGISTERED`. No endpoint changes it in Stage 3. `VERIFIED_COMPLETE` is reserved for the completion-verification module (OD-08), which is not implemented.
- Cohort deactivation is a status change, not deletion. Existing internships, memberships and assignments remain intact and resolvable; the database forbids deletion of referenced cohorts (`ON DELETE RESTRICT`; FR-INT-008 AC2).
- User deactivation preserves records and attribution; no cascade deletion or anonymization occurs (FR-USER-005 AC2).
- Coordinator removal sets `unassigned_at` and withdraws scope for subsequent requests (FR-COHORT-003).
- Notifications, audit events, compliance recalculation and report/document side effects are **not** triggered by this contract; their persistence tables do not exist yet (DDD §15.4). Implementers must not claim those side effects until the relevant substage.

## 9. Traceability

| Endpoint(s) | Requirement(s) | Status |
|---|---|---|
| `GET/PATCH /api/users/me` | FR-USER-003; PR-06 | Self-service fields Proposed |
| `POST/GET/PATCH /api/users...`, role/status | FR-USER-001/002/004/005/006/008; FR-AUTH-003/004 | Confirmed (scope rules PR-01/PR-04 proposed) |
| `GET/POST/PATCH /api/companies...` | FR-INT-001/008; SRS-IF-07; IF-DEC-21 | Proposed (no FRD module) |
| `POST/GET/PATCH /api/cohorts...` | FR-COHORT-001/005/006/007 | Confirmed (deadline values OD-10) |
| `GET/POST /api/cohorts/{id}/members` | FR-COHORT-002; AS-02; OD-14 | Initial assignment confirmed; moves Open |
| `GET/POST/DELETE /api/cohorts/{id}/coordinators...` | FR-COHORT-003; FR-USER-007; AS-03 | Confirmed; primary cardinality assumption |
| `GET/POST/PATCH /api/internships...` | FR-INT-001/002/003/004/006/007/008; AS-01; OD-12/OD-13 | Registration/edit confirmed with open field catalogue |
| Error contract | SRS-IF-03/04; SRS-ERR-001…014; FRD §7 | Confirmed function; codes are Proposed |
| Session/CSRF rules | FR-AUTH-002/003; IF-DEC-19 | Approved mechanics; implementation Stage 2 |
| No delete endpoints | FR-INT-008 AC2; FR-USER-005; PR-08 | Proposed/Confirmed |

## 10. Examples

### 10.1 Student registers an internship

```http
POST /api/internships
Content-Type: application/json
X-XSRF-TOKEN: <token>

{
  "startDate": "2026-01-05",
  "endDate": "2026-06-20",
  "title": "Software Engineering Intern",
  "companyId": 7
}
```

`201 Created`:

```json
{
  "id": 15,
  "student": {
    "id": 42,
    "identity": "student@example.edu",
    "displayName": "Aarav Sharma",
    "role": "STUDENT",
    "accountStatus": "ACTIVE"
  },
  "cohort": { "id": 3, "cohortCode": "C-2026-A", "name": "Computer Science 2026 A" },
  "company": { "id": 7, "name": "Acme Labs" },
  "startDate": "2026-01-05",
  "endDate": "2026-06-20",
  "title": "Software Engineering Intern",
  "description": null,
  "lifecycleState": "REGISTERED",
  "createdAt": "2026-10-22T12:00:00Z",
  "updatedAt": "2026-10-22T12:00:00Z"
}
```

### 10.2 Validation failure

```json
{
  "timestamp": "2026-10-22T12:00:00Z",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "category": "BUSINESS_VALIDATION",
  "message": "The internship end date cannot be earlier than its start date.",
  "fieldErrors": [
    { "field": "endDate", "message": "Must be on or after startDate." }
  ]
}
```

### 10.3 Out-of-scope access

A faculty coordinator not assigned to cohort B requests an internship in cohort B. The response is `404 NOT_FOUND` with a generic message; it does not confirm whether the internship exists (FRD §7.3; FR-COHORT-006).

## 11. Compatibility and Change Rules

- Adding a response field is backward-compatible; removing/renaming a field, changing a type, or tightening validation is a breaking change and requires review.
- Once implemented, a field removed from the contract but still used by the database must not be dropped without a migration (IF-DEC-20).
- The API must never expose `credential_hash`, session identifiers, password material, or unrelated users' data.
- IF-DEC-14 (versioning) must be resolved before any external/second consumer integrates.

## 12. Open Items Blocking This Contract

| Item | Effect on this contract | Owner/decision |
|---|---|---|
| API versioning not decided | Paths are provisionally unversioned | IF-DEC-14 |
| Authentication implementation absent in this branch | Endpoints cannot be exercised securely; no role/scope enforcement exists yet | Stage 2 work + IF-DEC-19 |
| Credential provisioning / first-login password change API | User creation leaves accounts `PENDING`; no password contract | Stage 2 authentication / IF-DEC-19 |
| Self role change and self deactivation | Endpoints return `409 DECISION_PENDING` | OD-01 |
| Administrative internship creation/edit | No admin create/edit endpoint | OD-01 |
| Deadline configuration values | No `deadlineConfig` field or endpoint | OD-10 |
| Cohort requirements | No requirement endpoints | OD-03 |
| Membership moves/unassignment | No member update/delete endpoint | OD-14 |
| Internship reassignment | `studentUserId`/`cohortId` not writable | OD-13 |
| Full lifecycle state set | Only `REGISTERED`/`VERIFIED_COMPLETE` values exist; only `REGISTERED` is set by this API | OD-12 |
| Completion verification | No completion endpoint; `VERIFIED_COMPLETE` unreachable in Stage 3 | OD-08 |
| Optimistic concurrency for Stage 3 edits | Last-write-wins; no ETag/`If-Match` | IF-DEC-16 boundary / reviewer decision |
| Company write permission | Proposed as administrator-only (no FRD rule) | Stage 3.1 review |
| Faculty user listing scope | Proposed (PR-04) | Reviewer confirmation |
| Rate-limiting thresholds and final `SameSite`/`Secure` | Not specified; deployment concern | IF-DEC-19 boundary / IF-DEC-10 |
| Reverse-proxy request-size limits | Not specified by source documents | Deployment/Security Requirements |

---

*End of draft. No Stage 3 endpoint in this document is implemented. The contract is pending Stage 3.1 review; open decisions above must not be silently defaulted during implementation.*
