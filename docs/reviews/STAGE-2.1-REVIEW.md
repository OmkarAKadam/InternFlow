# InternFlow — Stage 2.1 Review Report: Foundation Review & Authentication Design

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | Stage 2.1 Review Report — Foundation Review & Authentication Design |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-REV-001 |
| Version | 1.1 (v1.0 = 2026-10-18 Stage 2.1 findings; v1.1 = 2026-10-18 approval addendum after the product owner approved OD-15 — findings and verification results unchanged) |
| Status | Draft — Pending Review as a document. **OD-15 was approved by the product owner on 2026-10-18** and is recorded as Approved in §4.5/§8 below and as IF-DEC-19 in `docs/architecture/DECISIONS.md`. All other findings, proposals in §6/§7, and open decisions remain as stated. |
| Date | 2026-10-18 |
| Branch | `stage-2-ui-authentication` (Stage 2 substage branch; no substage branches were created) |
| Scope | Stage 2.1 only: inspection, analysis, and documentation. No login UI, authentication functionality, sessions, JWTs, authentication endpoints, protected routes, database migrations, or business modules were implemented. |
| Related Documents | PRD v1.0, FRD v1.0, SRS v1.0 (`docs/requirements/`); SAD, DDD, DECISIONS (`docs/architecture/`); SETUP (`docs/development/`). This report resolves no open decision and modifies no requirements baseline. |

**Status conventions** are those of the existing documents (FRD §2.4, SRS §2.6, DECISIONS §1): Confirmed (source cited), Derived, Proposed (requires approval), Open decision (OD-xx, carried unchanged), Assumption (AS-xx).

## 2. Purpose and Method

Stage 2.1 is an inspection, analysis, and documentation substage. Every statement below was verified against the repository as it exists on 2026-10-18; where behavior was checked at runtime, the command and observed outcome are recorded in §10. Nothing was assumed to be implemented merely because it is documented.

> **Update note (v1.1, 2026-10-18):** §3–§4.4 and §10 record the repository and decision state **as inspected before the product-owner decision**. Where they describe OD-15 as open or a proposal as pending, that was the inspected state; the approval recorded in the §4.5 and §8 addenda (and as IF-DEC-19 in `docs/architecture/DECISIONS.md`) supersedes those status descriptions without altering the findings themselves.

## 3. Existing Foundation — What Exists, What Is Scaffold, What Is Missing

### 3.1 Repository state

| Item | Observation |
|---|---|
| Branch | `stage-2-ui-authentication` (correct Stage 2 branch; no branch switching performed) |
| Working tree | Clean at start and end of this substage |
| Latest commit | `0c79b3e` — "merge: complete stage 1 planning baseline" |

### 3.2 Implemented (real, working code)

| Area | What exists | Evidence |
|---|---|---|
| Backend entry point | `backend/src/main/java/com/internflow/InternFlowApplication.java` | Builds and runs (§10). |
| Security placeholder | `backend/.../shared/config/SecurityConfig.java` — deny-by-default filter chain: only `GET /api/health` permitted; CSRF disabled; `STATELESS` session policy; no `UserDetailsService`, no login endpoint, no role rules. Explicitly documented in-code as not a production posture (OD-15 open). | Live-verified (§10): health = 200; `/api/anydata` = 403. |
| Health endpoint | `backend/.../shared/health/HealthController.java` — constant, non-sensitive response (IF-DEC-18, Approved). | Live-verified: `{"status":"UP","application":"internflow-backend"}`. |
| Backend configuration | `backend/src/main/resources/application.yml` — all database settings via `INTERNFLOW_DB_*` environment variables with safe local defaults; `ddl-auto` defaults to `none`; dialect pinned so the context starts without a live database; Spring Boot's default in-memory user is excluded so the app does not appear to offer a login. No secrets in tracked files. | Read and reproduced (§10). |
| Backend test | `InternFlowApplicationTests` — one context-load test. | `mvn test`: BUILD SUCCESS, 1 test, 0 failures (§10). |
| Frontend foundation | Next.js 16.4.2, React 19.3.0, TypeScript, Tailwind CSS v4; root layout with Geist fonts; a placeholder home page stating that features and authentication are not implemented; `frontend/.env.example` with `NEXT_PUBLIC_API_BASE_URL` placeholder only. | `npm run lint` and `npm run typecheck` pass (§10). |
| Documentation | PRD, FRD, SRS, SAD, DDD, DECISIONS, SETUP — all present, all marked Draft — Pending Review. | Read in full during this review. |

### 3.3 Scaffold / placeholder (present but deliberately non-functional)

| Item | Reality |
|---|---|
| `SecurityConfig` | A development placeholder. Denies everything except health; CSRF off and sessions stateless only because no authentication mechanism exists yet. Must be revisited when OD-15 resolves (SETUP §5). |
| Database configuration | Environment-variable plumbing only. No entities, no repositories, no migrations, no schema. Hikari attempts a connection lazily and logs a connection-failure stack trace at test time; the context still loads because no component requires a database (verified, §10). |
| Module packages | `com.internflow.auth`, `users`, `internships`, … (SAD §5.14) are a **recommendation**; they do not exist. Only `shared/` exists. |
| Frontend routes/components | Only `/` exists. No route groups, no shared component library, no API client layer, no loading/error states. |

### 3.4 Missing (expected — to be built in later Stage 2 substages and beyond)

Everything MVP-facing: login UI, authenticated application shell, authentication endpoints, session/token mechanics, credential storage, the `users` table and all other schema, all 13 modules' code, role-based route protection, the typed API client, authorization tests, Playwright infrastructure.

**Minor findings (no action taken this substage):**

1. `frontend/src/app/page.tsx` and `frontend/AGENTS.md` contain mojibake (`�?"` where an em dash should be) — an encoding artifact worth fixing when those files are next edited.
2. `README.md` "Current Status" still says "Stage 1 — Planning and Project Setup (in progress)"; it is now stale. It was left unchanged to keep this substage's diff focused and traceable; updating it belongs to the Stage 2 close-out.
3. `globals.css` sets `font-family: Arial, Helvetica, sans-serif` on `body` while also registering the Geist variables; the fallback works but the registered `--font-sans` is not applied to `body`. Cosmetic; revisit with the UI foundation.

## 4. Authentication Analysis

### 4.1 Extracted requirements (Confirmed unless noted)

| Requirement | Substance | Source |
|---|---|---|
| FR-AUTH-001 | Credential sign-in; valid credentials → authenticated session → role-appropriate landing surface; invalid credentials → single generic failure message revealing nothing about account existence or activation. AC4: created-but-not-activated accounts cannot sign in. | FRD §6.1 |
| FR-AUTH-002 | Maintain authenticated state until sign-out or expiry; sign-out terminates it; AC2: no other requirement may assume session/token representation. | FRD §6.1 |
| FR-AUTH-003/004 | Role + record-scope authorization server-side on every protected operation; deny-by-default; interface hiding is never enforcement; direct URL/operation requests by wrong roles fail. | FRD §6.1; NFR-SEC-001/002 |
| FR-AUTH-005 | Deactivated accounts cannot sign in (same generic message); an existing session stops authorizing "no later than the next protected request". | FRD §6.1 |
| FR-AUTH-006 | Failed sign-in responses are indistinguishable; **no lockout threshold is invented** — lockout/rate limiting is OD-15. | FRD §6.1 |
| FR-AUTH-007 | Unauthorized attempts: uniform access-denied outcome, no data disclosure, distinguishable from business validation failures. | FRD §6.1 |
| FR-USER-001/002/005 | Admin-only account creation and role assignment; creation ≠ activation (activation mechanics OD-15); deactivation preserves records/attribution; role change effective on the next protected request. | FRD §6.2 |
| NFR-SEC-003/008 | Generic authentication failures; secrets externalized, never committed. | SRS §7.1 |
| NFR-SEC-009 | Authentication mechanics (credential model, password policy, storage, lifetime, renewal, lockout) comply with Security Requirements **once decided**; the MVP must not rely on a specific mechanism before that decision. | SRS §7.1 (status: Open — OD-15) |
| SRS-IF-02, SRS-FR-001/007, SRS-AUTH-* | Server-side enforcement of authentication, role, and scope on every protected API operation including reads/lists/exports. | SRS §5.2, §6.3, §10 |

### 4.2 Role representation

| Role | MVP status | Auth implications | Source |
|---|---|---|---|
| Student | MVP | Single role per user (AS-04); scope = own records. | PRD §6.1; FRD §3.1 |
| Faculty Coordinator | MVP | Cohort-scoped; a role grant alone grants nothing without cohort assignment (FR-USER-007); sole weekly-report approver. | PRD §6.2; FRD §3.2 |
| College Administrator | MVP | Deployment-wide administrative scope only; administrative status never grants report-approval or completion-verification authority (PR-05; OD-08). | PRD §6.3; FRD §3.3, §4.4 |
| Company Mentor | Post-MVP | No MVP permissions, screens, or requirements; must not be exposed (SRS-FR-002). Design leaves room (e.g., role enum extensibility) but implements nothing. | PRD §6.4; FRD §3 |

### 4.3 Current Spring Security behavior (verified, not assumed)

`SecurityConfig` (placeholder): `/api/health` permitted; every other request requires authentication; CSRF disabled; no session creation; no login mechanism; Spring Boot's default generated password disabled via auto-configuration exclusion. Live test (§10): unauthenticated `GET /api/anydata` returned **403**. Observation for Stage 2.2: once authentication exists, unauthenticated (no/invalid session) and unauthorized (wrong role/scope) outcomes must be distinguishable by the frontend — FR-AUTH-007 AC2 and SRS-IF-04 imply separate outcomes (typically 401 vs 403); the current blanket 403 is a placeholder artifact, not a design.

### 4.4 Options evaluated (recorded per OD-15; analysis retained — Option A was approved 2026-10-18, see §4.5)

| Option | Summary | Browser/security considerations |
|---|---|---|
| **A. Server-managed session, cookie-based** (Spring Security session stored server-side; frontend sends an `HttpOnly`, `Secure`, `SameSite` session cookie) | State lives in the backend; browser holds only an opaque session id. | Strong fit: revocation is immediate (deactivation FR-AUTH-005 AC2, role change FR-USER-002 AC3 fall out naturally); the cookie is unreadable by JavaScript (XSS exfiltration of the credential is mitigated); CSRF protection must be enabled because state-changing requests ride on the cookie — Spring's built-in synchronizer-token/DBL support covers this. Cross-origin deployment (Vercel frontend vs. backend host per IF-DEC-09/IF-DEC-10) forces `SameSite=None; Secure` or a same-site proxy/rewrite — a deployment-dependent detail, not a blocker. |
| **B. Stateless token (JWT)** | Signed token issued at sign-in, validated per request; no server session state. | Acceptable but weaker fit: revocation requires extra machinery (token-version/whitelist, short lifetimes + refresh tokens) to satisfy FR-AUTH-005 AC2 and FR-USER-002 AC3; storing the token in `localStorage` exposes it to XSS, storing it in a cookie reintroduces the CSRF requirement anyway; lifetime/renewal policy is additional open design (part of OD-15). Justified mainly if horizontal backend scaling or third-party API consumers existed — neither exists (single backend instance acceptable, P7; the only API client is the first-party frontend, SRS-IF-18/SRS §3.4). |
| **C. External identity provider** | Delegated authentication. | No MVP requirement or integration exists (SAD §3.3); adds an external dependency and cost; consistent with prior rejection in DECISIONS IF-DEC-04 alternatives. Post-MVP option only. |

### 4.5 Recommendation — **approved by the product owner on 2026-10-18** (recorded as IF-DEC-19 in `docs/architecture/DECISIONS.md` §5; closes OD-15; see §8)

> **Approval addendum (2026-10-18).** The product owner approved the recommended package below and supplied the policy values that were deliberately left open: minimum password length **12 characters without mandatory character-class combinations**; **30-minute idle session timeout**; **8-hour absolute session lifetime**; **BCrypt** password hashing; **failed-login rate limiting without permanent account lockout** (numeric thresholds not specified by the approver — not invented here); temporary administrator-set password with **mandatory change at first login**; **email as login identity**; **administrator-created accounts, no public registration**; **HttpOnly session cookies, Secure in production, appropriate SameSite policy**; **CSRF protection enabled**; **server-managed Spring Security sessions**; **Company Mentor excluded from the MVP**. The original analysis below is preserved unchanged as the basis of that decision.

**Recommend Option A: server-managed sessions with an `HttpOnly`/`Secure` cookie, CSRF protection enabled, and Spring Security's session-fixation protection and logout endpoint.** Reasoning:

1. **Requirement fit.** FR-AUTH-005 AC2 (session dies by the next protected request after deactivation) and FR-USER-002 AC3 (role change effective on the next protected request) are one line of design with server-side sessions; with JWT they require deliberate invalidation machinery.
2. **Threat fit.** Cookie + `HttpOnly` + CSRF defense handles both XSS-credential-theft and CSRF at once; a JWT in `localStorage` handles neither CSRF nor (if readable) XSS.
3. **Scale fit.** One college department, one backend instance acceptable (SAD P7, P9). Server-side session state costs trivially little and buys immediate revocation.
4. **Simplicity.** No token-lifetime/renewal design, no signing-key management, no refresh flow — less open design inside an already-open decision.

Trade-offs accepted: server-side state (sessions must be stored somewhere — in PostgreSQL or the backend host memory; a persistence choice inside OD-15); cross-origin cookie configuration depends on the open backend-hosting decision (IF-DEC-10) — either `SameSite=None; Secure` cookies over TLS or a same-site frontend proxy/rewrite; logout and expiry must invalidate server-side rows.

**Sub-decisions inside OD-15 — disposition after the 2026-10-18 approval** (original options preserved; approved values as given by the approver, nothing invented):

| Sub-question | Options surfaced | Disposition (2026-10-18) |
|---|---|---|
| Credential model / identity | Email as sign-in identity vs. separate username (DDD §4.1 `identity` is open on this) | **Approved: email as identity** (doubles as notification target, FR-NOTIF-005) |
| Password storage | Argon2id or BCrypt (Spring's `PasswordEncoder` supports both) | **Approved: BCrypt** |
| Password policy | Admin-set initial credential vs. user-chosen; minimum rules | **Approved: administrator-set temporary credential; minimum length 12 characters; no mandatory character-class combinations** |
| Provisioning/activation | Administrator-set credentials, invitation email, or first-login activation (FR-USER-001 lists all three) | **Approved: administrator-set temporary password with mandatory change at first login** (invitation flow was infeasible in Stage 2 anyway — no email service exists; OD-09/IF-DEC-08 transport open) |
| Session lifetime / renewal | Idle timeout and absolute lifetime values | **Approved: 30-minute idle timeout; 8-hour absolute lifetime** |
| Lockout / rate limiting | None, in-memory counter, persistent counter | **Approved direction: failed-login rate limiting, no permanent lockout.** Numeric thresholds were **not** specified by the approver and are not invented here; any threshold is an implementation detail to be recorded in the Security Requirements when written, not an approved value |

## 5. Database Readiness

### 5.1 Configuration state

- `application.yml` reads `INTERNFLOW_DB_URL` / `INTERNFLOW_DB_USERNAME` / `INTERNFLOW_DB_PASSWORD` from the environment with safe local defaults (`jdbc:postgresql://localhost:5432/internflow`, user `internflow`, empty password). No credential appears in any tracked file; `frontend/.env.example` contains placeholders only (NFR-SEC-008 satisfied).
- No entities, no repositories, no migrations, no schema (correct: OD-02 and OD-03 are explicitly prerequisites for finalizing the first migration — FRD §9.1; DDD is a draft; **neither was bypassed**).

### 5.2 Checks performed (no secrets printed, none available, none created)

| Check | Method | Result |
|---|---|---|
| Is PostgreSQL listening locally? | TCP connect test to `localhost:5432` | **Yes — port open.** Consistent with the Stage 1.6 note in SETUP §6. |
| Can the application authenticate to the database? | Not attempted | **Not verified.** No password is configured anywhere; Stage 1.6 recorded a SCRAM authentication refusal when no password was set. Attempting credential guessing was out of scope and unsafe; application-level DB authentication remains **unverified**. |
| Does the backend start without a database? | `mvn test` (context load) | **Yes** — BUILD SUCCESS (§10). The log contains a lazy Hikari connection-failure stack trace; it is non-fatal because no component needs a connection while `ddl-auto: none` and no entities exist. This confirms "application starts" ≠ "database reachable". |
| Migrations created/executed? | — | **None.** Forbidden this substage and blocked by OD-02/OD-03 regardless. |

**Distinction (per Stage 1 requirement):** PostgreSQL is *installed and listening* on `localhost:5432`; *application authentication to the database* is **not** verified and requires a created database, a configured user/password supplied out-of-band by the product owner, and a re-test in a substage where database work is in scope.

## 6. UI Foundation Findings

### 6.1 Existing UI assets

Tailwind CSS v4 with `@import "tailwindcss"`; `@theme inline` token block with `--background`/`--foreground` CSS variables and automatic dark-mode via `prefers-color-scheme`; Geist sans/mono font variables; a single root layout (`lang="en"`, metadata title/description already correct for InternFlow); one placeholder page. **No** component library, UI dependency, route structure, API client, or state-management package is installed — and none is needed yet (dependencies must be justified; Stage 2.2+ can build on native React/Tailwind, per SAD R8 which recommends only a typed fetch wrapper, not a library).

### 6.2 Minimum UI foundation recommended for Stage 2 (not built this substage)

| Piece | Recommendation | Basis |
|---|---|---|
| Unauthenticated area | `/login` route (route group `(unauthenticated)`), single sign-in form; no self-registration link (AS-05); generic error text only | FR-AUTH-001; SRS-UI-02 |
| Authenticated shell | Route groups per role — `(student)`, `(faculty)`, `(admin)` — sharing an app-shell layout (header with role display + sign-out, side/top navigation scoped by role); post-login landing redirects to the role surface | SAD §7 (Proposed, R8); SRS-UI-02/03 |
| Route protection | Client-side guard for usability (redirect to `/login` when unauthenticated) — explicitly **not** enforcement; backend remains authoritative | SRS-UI-06; SAD §7 |
| API client | One typed fetch wrapper owning `NEXT_PUBLIC_API_BASE_URL`, `credentials` handling, and mapping of backend failure categories (FRD §7.1 five categories) to user-visible messages; components never call `fetch` directly | SAD §7 (R8) |
| Shared components | Button, input/form-field with label+error, alert/error panel, loading spinner/skeleton, empty-state panel, status-badge placeholder | SAD §7; consistent visual language before Stage 3 |
| States | Every view defines loading, empty, success, and error states; configuration gaps render as "not configured", never defaulted | SAD §7; FR-DASH-002; SRS-ERR-009 |

### 6.3 Responsive, accessibility, loading, validation, error requirements

| Concern | Requirement status | Implication for Stage 2 |
|---|---|---|
| Responsive | **Confirmed** (SRS-UI-05, NFR-UX-004): usable across typical screen sizes | Login and shell must be mobile-usable; no device baseline established |
| Accessibility | **Open** (SRS-UI-07, NFR-UX-005): no conformance level claimed | Practice basics (labels, focus, contrast, keyboard) without claiming WCAG conformance |
| Loading | SAD §7 (Proposed): explicit loading states; NFR-PERF targets open | Show pending state on sign-in submit; disable double-submit |
| Client validation | **Confirmed** advisory only (NFR-SEC-005, SRS-IF-03): identical rules enforced server-side; server responses authoritative | Login form validates presence/shape only; no security-relevant client logic |
| Error handling | **Confirmed** (NFR-UX-003, SRS-IF-04, FR-AUTH-001/006/007): generic auth-failure message; errors categorized per FRD §7.1; error text must not assert open policies | One generic sign-in failure message; distinguish 401 vs 403 behavior in the client mapping |

## 7. Proposed Stage 2 Acceptance Criteria (measurable; all **Proposed** pending approval)

Each criterion cites the confirmed requirement it serves; criteria introducing anything beyond an existing requirement are marked **(new proposal)**.

### 7.1 Shared UI foundation and login interface

| # | Criterion |
|---|---|
| AC-2.1-01 | A `/login` route renders on a 360 px-wide viewport and a 1440 px-wide viewport without horizontal overflow (SRS-UI-05). |
| AC-2.1-02 | The login form has labeled inputs, visible keyboard focus, and submits via keyboard only (basic practice; **no conformance claim** — SRS-UI-07). |
| AC-2.1-03 | Submitting shows a loading state and prevents duplicate submissions **(new proposal)**. |
| AC-2.1-04 | The shared component library contains at minimum: button, input/form-field with error, alert panel, loading indicator, empty-state panel; the login screen and app shell use only these **(new proposal)**. |
| AC-2.1-05 | The typed API client is the only place `fetch` is called; base URL comes from `NEXT_PUBLIC_API_BASE_URL`; no `NEXT_PUBLIC_` variable carries a secret (SAD §7; NFR-SEC-008). |

### 7.2 Backend authentication and credential validation

| # | Criterion |
|---|---|
| AC-2.2-01 | Valid credentials for each of the three MVP roles establish an authenticated session and the role-appropriate landing surface (FR-AUTH-001 AC1). |
| AC-2.2-02 | Invalid credentials, credentials of a deactivated account, and credentials of a created-but-not-activated account all return the **same** generic failure message and no session (FR-AUTH-001 AC2/AC3/AC4; FR-AUTH-005 AC1; FR-AUTH-006 AC1 — responses indistinguishable by test). |
| AC-2.2-03 | Passwords are stored only as password hashes; no credential material appears in logs, audit payloads, or API responses (DDD §4.1 §12; NFR-SEC-008). |
| AC-2.2-04 | No authentication mechanism is active that contradicts the approved OD-15 outcome; mechanics not covered by the approval are not implemented (NFR-SEC-009). |
| AC-2.2-05 | The Company Mentor role does not exist in any enum, endpoint, or UI surface (SRS-FR-002). |

### 7.3 Session/token lifecycle (per the approved OD-15 design)

| # | Criterion |
|---|---|
| AC-2.3-01 | Session establishment, expiry, and renewal behave exactly as the approved OD-15 outcome specifies; any lifetime value tested is a value the product owner approved, not an implementation default **(approved values: 30-minute idle timeout; 8-hour absolute lifetime)**. |
| AC-2.3-02 | Deactivation invalidates the account's ability to operate no later than its next protected request (FR-AUTH-005 AC2). |
| AC-2.3-03 | A role change is effective on the user's next protected request (FR-USER-002 AC3). |
| AC-2.3-04 | If a cookie-based mechanism is approved: cookies are `HttpOnly` + `Secure` (in deployed TLS environments) and CSRF protection is enabled on state-changing endpoints **(proposal within the recommendation; the cookie mechanism was approved 2026-10-18)**. If a token mechanism is approved: the storage location and anti-XSS/CSRF posture match the approved design. |

### 7.4 Logout and unauthenticated access

| # | Criterion |
|---|---|
| AC-2.4-01 | After sign-out, no protected operation succeeds until sign-in (FR-AUTH-002 AC1); sign-out invalidates server-side state, not just client display. |
| AC-2.4-02 | An unauthenticated API request receives a distinct unauthenticated outcome (e.g., 401) distinguishable from an authorization failure (e.g., 403) so the frontend can route correctly (FR-AUTH-007 AC2; SRS-IF-04; observed gap §4.3). |
| AC-2.4-03 | An unauthenticated browser request to any authenticated frontend route redirects to `/login` without flashing protected data (usability only — SRS-UI-06; enforcement verified server-side). |

### 7.5 Role-based authorization (backend endpoints and frontend routes)

| # | Criterion |
|---|---|
| AC-2.5-01 | An automated test exists for every authentication-relevant Denied cell of FRD §4 (wrong-role attempts at login-adjacent operations), each asserting access denied and no data disclosure (FR-AUTH-003 AC1; SAD §16 highest priority). |
| AC-2.5-02 | Directly requesting a role-restricted frontend URL with a non-permitted role fails at the backend when any API data is involved, and the frontend shows a denial state (FR-AUTH-004 AC1). |
| AC-2.5-03 | Faculty with no cohort assignment can be tested to authorize nothing beyond role-level access (FR-USER-007 AC2) — placeholder until cohorts exist in Stage 3; test scaffolded in Stage 2 only if a user/role store exists. **(new proposal)** |
| AC-2.5-04 | Deny-by-default: a new endpoint added without an authorization rule fails a regression test rather than being publicly reachable (NFR-SEC-002). **(new proposal)** |

### 7.6 Authentication error handling

| # | Criterion |
|---|---|
| AC-2.6-01 | The frontend renders exactly one sign-in failure message for all authentication failure causes, matching the backend's generic message (FR-AUTH-001; NFR-SEC-003). |
| AC-2.6-02 | Backend error responses map to the FRD §7.1 category taxonomy in the API contract; authorization failures never include record content or existence (FR-AUTH-003/007; SRS-IF-04). |
| AC-2.6-03 | No error text, log line, or response asserts a policy that remains open (e.g., lockout wording before lockout is decided) (NFR-UX-003). |

### 7.7 Automated tests and frontend/backend integration

| # | Criterion |
|---|---|
| AC-2.7-01 | Backend: JUnit + Spring Boot Test suite covering sign-in success/failure/deactivation/activation paths and session lifecycle per the approved design; `mvn test` passes (IF-DEC-11). |
| AC-2.7-02 | Frontend: Playwright test covering at least one journey per role — sign in → role landing → sign out — against the real backend (IF-DEC-11; SAD §16). |
| AC-2.7-03 | `npm run lint` and `npm run typecheck` pass with zero errors on the Stage 2 branch. **(new proposal — no quantitative gate exists in the baseline)** |
| AC-2.7-04 | The existing context-load test and health endpoint test continue to pass (no regression of the Stage 1 foundation). |

## 8. Decision Record — OD-15 (**Approved 2026-10-18**; recorded as IF-DEC-19 in `docs/architecture/DECISIONS.md` §5)

> **Approval addendum (2026-10-18).** The product owner **approved the package below in full**, supplying the open policy values as recorded in §4.5 and in IF-DEC-19: minimum password length 12 characters without mandatory character-class combinations; 30-minute idle timeout; 8-hour absolute lifetime; BCrypt hashing; failed-login rate limiting without permanent account lockout (numeric thresholds not specified — not invented); temporary administrator-set password with mandatory first-login change; email identity; administrator-created accounts, no public registration; HttpOnly cookies (Secure in production, appropriate SameSite policy); CSRF enabled; server-managed Spring Security sessions; Company Mentor excluded from the MVP. **OD-15 is Closed.** The original request below is preserved as the record of what was asked. The register was updated accordingly (DECISIONS.md §3/§5/§6/§7/§11).

~~**OD-15 remains OPEN. Nothing below is approved.**~~ *(superseded by the addendum above; original text retained)* The decision register (`docs/architecture/DECISIONS.md` §6) was updated only with a pointer to this analysis; no status change was made.

**Exact question requiring approval (as asked on 2026-10-18):**

> **OD-15 (authentication mechanics).** For InternFlow's MVP, do you approve the following package?
> **(1)** Server-managed sessions using Spring Security with an `HttpOnly`, `Secure`, `SameSite` session cookie (CSRF protection enabled), sessions persisted server-side so that deactivation and role changes take effect on the next protected request; **(2)** email address as the sign-in identity; **(3)** administrator-created accounts activated by an administrator-set temporary credential that the user must change at first login (no self-registration); **(4)** password hashing with BCrypt or Argon2id via Spring's `PasswordEncoder`.
> **Also required (no defaults proposed):** minimum password policy (e.g., length/complexity rules of your choosing); session idle/absolute lifetime values; and whether account lockout or rate limiting is required.
> **If any part is declined**, the alternatives are stateless JWT tokens (weaker fit for the deactivation/role-change requirements — see §4.4) or an external identity provider (out of MVP scope per current requirements — see §4.4).

**Implications of approval (now realized on 2026-10-18):** Stage 2.2–2.4 may implement login, sessions, logout, and role-based endpoint authorization against this design; the DDD `users` table `activation_state`/`credential_hash` columns (already Proposed/OD-15) become implementable; the API Specification gains its authentication contract.

**Implications of deferral (superseded — the package was approved):** Stage 2.2 cannot begin authentication implementation without inventing mechanics, which NFR-SEC-009 forbids; the UI foundation work (login page layout, app shell) can still proceed as it does not depend on the mechanics.

## 9. Files Created, Modified, or Deleted

### v1.0 — Stage 2.1 findings (original pass, 2026-10-18)

| File | Action | Justification |
|---|---|---|
| `docs/reviews/STAGE-2.1-REVIEW.md` | Created | Stage 2.1 requires a review report. No existing directory fits (requirements/ = baseline, architecture/ = design documents, development/ = environment setup); a new `docs/reviews/` directory was created to avoid duplicating or contradicting those documents, matching their status-label conventions. |
| `docs/architecture/DECISIONS.md` | Modified (one paragraph added to §6; one review action added to §11.2) | Recorded that the OD-15 analysis exists and where it lives, while explicitly keeping OD-15 **Open** at that time. No register status changed; no counts changed. |

### v1.1 — approval recording pass (2026-10-18, after product-owner approval)

| File | Action | Justification |
|---|---|---|
| `docs/architecture/DECISIONS.md` | Modified | Records the approval in the document's existing format: new **IF-DEC-19** entry (§5) with the full approved package and boundaries; register row and counts updated (§3: 10 Confirmed / 7 Approved / 2 Open, total 19); IF-DEC-04 status/decision/consequences cross-reference IF-DEC-19 (§4); §6 open-items row resolved and the OD-15 paragraph updated to record the closure and the exact approved values; §7 OD-15 row marked closed with a note that the FRD v1.0 register predates the closure; §9 rejected alternatives extended (JWT, external IdP, character-class rules, permanent lockout); §10 traceability extended; §11 checks re-executed/updated (checks 3 and 7) and review action 7 marked completed; §11.3 Stage 2.1 disposition added; document-control last-updated and review note amended. |
| `docs/reviews/STAGE-2.1-REVIEW.md` | Modified (this file) | Version 1.1: approval addenda in §4.5 and §8, sub-decision table updated to approved dispositions, acceptance-criteria annotations updated with the approved values, §8 converted from decision request to decision record, §9/§11/§12 updated. **All original findings, options analysis, and verification results (§3, §4.1–§4.4, §5, §6, §7, §10) are preserved unchanged.** |
| `docs/architecture/SAD.md` | Modified (9 minimal annotations) | OD-15 status references updated to avoid contradictions with the approval: §3.3 (integration-boundary line), §5.1 (×2), §6 table, §7, §8.4 (heading + intro + provisioning paragraph), §10, §17.4. The options table in §8.4 is retained as the analysis record. No architectural content was redesigned. |
| `docs/architecture/DDD.md` | Modified (5 minimal annotations) | `users` table §4.1: `identity` note (email identity approved), `activation_state`/`credential_hash` status cells changed from "Proposed / OD-15" to "Approved (IF-DEC-19)", table sensitive-data note, §12 password-data note, and the §13.2 data-dictionary summary row. No schema change, no migration — column definitions and constraints untouched. |
| `docs/development/SETUP.md` | Modified (one paragraph in §5) | Security-status paragraph updated: mechanics approved 2026-10-18 but **not implemented**; the placeholder warning stands. |
| Nothing else | — | **No source code, configuration, test, or requirements-baseline document (PRD/FRD/SRS) was touched.** No file was deleted or overwritten. FRD §9 and SRS §14.4 still list OD-15 as open in the v1.0 baseline drafts; amending the baseline is a change-control follow-up, recorded in §11 below rather than performed silently. |

## 10. Verification Log (commands actually executed, outcomes observed)

| Command / check | Outcome |
|---|---|
| `git status`, `git branch --show-current` | On `stage-2-ui-authentication`; working tree clean (start and end of substage). |
| Read all of PRD, FRD, SRS, SAD, DDD, DECISIONS, SETUP, README, all backend/frontend source and config files | Done; findings in §3–§6. |
| `mvn test` (backend) | **BUILD SUCCESS** — `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`. Log contains a non-fatal Hikari connection-failure stack trace (no database configured) and a dialect-deprecation warning; context loads regardless. |
| `npm run lint`, `npm run typecheck` (frontend) | Both exit clean, no errors. |
| TCP connect test `localhost:5432` | **Open** — PostgreSQL listening locally. |
| TCP connect test `localhost:8080` | Closed before backend start (backend not running). |
| Backend started from packaged jar; `GET /api/health` | **200** — `{"status":"UP","application":"internflow-backend"}`. |
| Backend running; `GET /api/anydata` (unauthenticated) | **403** — deny-by-default confirmed; blanket-403 observation recorded in §4.3. |
| Backend process stopped; port 8080 re-checked | Closed. No stray processes left running. |
| Database authentication test | **Not attempted** (no credentials configured; unsafe and out of scope — §5.2). |

## 11. Unresolved Decisions and Blockers

| ID | Status | Bearing on Stage 2 |
|---|---|---|
| **OD-15** | **Closed 2026-10-18 — approved by the product owner; recorded as IF-DEC-19 in `docs/architecture/DECISIONS.md` §5** | No longer a blocker: authentication implementation in a later Stage 2 substage may proceed against the approved package exactly. Numeric rate-limiting thresholds and the exact cookie `SameSite` value were not specified by the approver and are not invented; they are implementation details to be recorded in the Security Requirements when written. |
| FRD §9 / SRS §14.4 baseline registers | Follow-up (not a decision) | Both Stage 1 baseline documents still list OD-15 as open in their v1.0 drafts. The closure is recorded in `DECISIONS.md` (the decision register); amending the FRD/SRS baseline should happen under the project's normal change control, not silently. |
| OD-01 | Open (carried) | Admin permission boundaries (self-role-change, content access) — relevant to authorization edge cases later in Stage 2/3. |
| OD-02, OD-03 | Open (carried) | **Block the first database migration** (FRD §9.1); no migration may be created until resolved. Not bypassed. |
| OD-09 | Open (carried) | Invitation-based account activation depends on email transport; the §8 recommendation therefore avoids email-dependent onboarding for Stage 2. |
| IF-DEC-10, IF-DEC-14 | Open (carried) | Backend hosting affects cross-origin cookie configuration (§4.4); API versioning affects the API Specification — both tolerable for Stage 2 development. |
| SRS-UI-07 (accessibility target) | Open (carried) | No conformance claimed in any Stage 2 UI work. |
| NFR-SEC-008-adjacent: DB credentials | Not a decision — an action | A product owner must supply local database credentials out-of-band before any database-backed work; nothing was or will be committed. |

## 12. Next Step

1. ~~**Product owner approves, amends, or rejects the OD-15 package in §8**~~ **Done on 2026-10-18:** the product owner approved the package; it is recorded as IF-DEC-19 in `docs/architecture/DECISIONS.md` §5, and OD-15 is closed.
2. Stage 2.2 may begin the **UI foundation and login interface** (per §6.2), building the shell and form against the now-approved design (generic error copy, no self-registration entry point, loading/disabled submit states).
3. Authentication backend implementation (Stage 2.3+) proceeds against the IF-DEC-19 package exactly (NFR-SEC-009 is satisfied for the approved items; anything outside the package needs a new decision). The Security Requirements document, when written, records the approved package rather than reopening it.
4. Still required before/alongside implementation, and **not** resolved by this approval: the concrete authentication endpoint contract (API Specification), the backend-hosting selection (IF-DEC-10) which determines the cross-origin `SameSite`/`Secure` configuration, and the FRD/SRS baseline-register follow-up noted in §11.

*End of Stage 2.1 report — status: findings complete and OD-15 approved/closed (2026-10-18, IF-DEC-19); no other decision was resolved by this substage.*
