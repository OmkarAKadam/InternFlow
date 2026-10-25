# InternFlow — Stage 2.3 Review Report: Authentication Backend Foundation

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | Stage 2.3 Review Report — Authentication Backend Foundation |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-REV-003 |
| Version | 1.2 |
| Status | **Approved with documented limitations by the product owner on 2026-10-22.** Covers Stage 2.3 only. Stage 2 as a whole is **not** complete: persisted accounts, an approved API contract, first-login password change, failed-login rate limiting, and role-based authorization remain for later Stage 2 substages. Revision 1.2 records the approval and the Stage 2.4 verification cross-reference; no limitation below is removed. |
| Date | 2026-10-22 |
| Branch | `stage-2-ui-authentication` (Stage 2 substage branch; no substage branches were created) |
| Scope | Stage 2.3 only: the Spring Security session-based authentication **foundation** — deny-by-default filter chain, BCrypt, server-managed sessions with HttpOnly cookie, CSRF enabled, session-fixation protection, 30-min idle / 8-h absolute lifetime, logout, error handling, and **provisional** sign-in/sign-out endpoints. No frontend change, no database schema/migrations, no approved API contract, no rate limiting, no forced password change. |
| Related Documents | Stage 2.2 Review (IF-DOC-REV-002); Stage 2.1 Review (IF-DOC-REV-001); PRD, FRD, SRS (`docs/requirements/`); SAD, DDD, DECISIONS (`docs/architecture/`); SETUP (`docs/development/`). This report resolves no open decision and changes no requirements baseline. |

**Status conventions** follow the existing documents (FRD §2.4, SRS §2.6, DECISIONS §1).

> **Revision 1.1 (2026-10-22):** incorporates the post-review security-fix pass (L-01, L-02, L-03, M-01), corrects the earlier 25-test count and the incorrect claim that the 12-character account-password minimum was enforced on the sign-in DTO, and records the remaining findings. The status remains **Draft — Pending Review**; this revision does not approve Stage 2.3. See §18 for the fix-pass evidence.

## 2. Substage Objective and Scope

Stage 2.3 implemented the authentication **backend foundation** behind the design approved as IF-DEC-19 in Stage 2.1, using the existing test infrastructure only. In scope: the security filter chain, BCrypt password encoding, server-managed sessions (HttpOnly cookie, `Secure` in production), CSRF protection, session-fixation protection, an explicit session-creation policy, the 30-minute idle and 8-hour absolute session lifetimes, logout, a deny-by-default authorization posture with an authentication entry point, error handling for the sign-in boundary, and provisional sign-in/sign-out endpoints clearly marked as **not an approved contract**.

Out of scope (preserved from the plan): frontend integration of the login form (Stage 2.2's `signIn()` still returns `integration-pending` and makes no network call), database schema / first migration / JPA entities, persisted accounts, an approved API contract, public registration, JWT/bearer tokens, social/OAuth login, Company Mentor, failed-login rate limiting (thresholds unresolved), the forced password change on first login (flow + persistence unresolved), role-based authorization for business modules, Stage 3+ functionality, deployment, and any fake or hardcoded production credentials.

## 3. Initial Git State

| Item | Observation |
|---|---|
| Branch | `stage-2-ui-authentication` (correct; verified before any edit) |
| Latest commit | `bcfd895` — "feat(frontend): add login UI foundation" (Stage 2.2; the starting point for this substage) |
| Working tree at start | Contained the prior in-progress Stage 2.3 edits (not yet committed). This substage **resumed** that work rather than recreating files. |

## 4. Existing Foundation (as inspected at resume)

| Item | Observation |
|---|---|
| Stage 2.2 frontend | Login UI + API client seam committed at `bcfd895`. `signIn()` returns `integration-pending`; no endpoint is called. **Untouched in Stage 2.3.** |
| Stage 2.1 decisions | IF-DEC-19 (approved mechanics), IF-DEC-19 boundaries, open thresholds (rate-limit, final SameSite), OD-02/OD-03 (schema) all still authoritative. |
| Test infrastructure | JUnit 5, `@SpringBootTest`, MockMvc, `RANDOM_PORT`, AssertJ available. No `spring-security-test`. Maven 3.9.14, JDK 23 targeting Java 21, **no `mvnw` wrapper** (use `mvn` from `backend/`). |
| Prior debug state | Sign-in POSTs were returning **403** for every authenticated-flow test; `SecurityConfigurationTest` had 1 failure + 2 errors; CSRF debug log showed `Invalid CSRF token found`. |

## 5. Implementation Summary

All changes are **uncommitted** on `stage-2-ui-authentication`. The old deny-all `shared/config/SecurityConfig.java` was **moved** to the new package `com.internflow.auth` (old file deleted).

| Area | What was built |
|---|---|
| **Security chain** (`auth/SecurityConfig.java`) | Deny-by-default `SecurityFilterChain`. Permits `GET /api/health`, provisional `POST /api/auth/sign-in`, `POST /api/auth/sign-out`, and ERROR-dispatch requests. Explicit session-creation policy; no stateless config; no wildcard credentialed CORS. Session-fixation `migrateSession()` configured at the DSL **and** re-applied explicitly in the service (see §13). |
| **CSRF** | Enabled (IF-DEC-19) via `CookieCsrfTokenRepository.withHttpOnlyFalse()` + plain `CsrfTokenRequestAttributeHandler`; `CsrfCookieFilter` materializes the token cookie on safe requests. |
| **Password hashing** | `BCryptPasswordEncoder` bean. The approved account-password policy (minimum 12 characters, no mandatory character classes) belongs to the **not-yet-implemented** account-creation / first-login password-change flow; the sign-in DTO does **not** enforce the 12-character minimum. Sign-in validates the email/password presence and the L-02 upper bounds (email 254, password 256) only. |
| **Sessions** | HttpOnly session cookie; `Secure` in production; 30-min idle (container) + 8-h absolute lifetime (`AbsoluteSessionLifetimeFilter`, injected `Clock`). Startup guard `ProductionSessionCookieGuard` refuses to start under an active `prod`/`production` profile when the effective session-cookie `Secure` flag is not `true` (M-01; limitations in §13). |
| **Sign-in/out** (`SignInController`, `SignInService`, `SignInRequest`, `SignInOutcome`) | Provisional JSON endpoints. `SignInService` guards on `ObjectProvider<UserDetailsService>`/`AuthenticationProvider` and answers `NOT_CONFIGURED` when absent; maps `InternalAuthenticationServiceException`/unexpected → `TECHNICAL_FAILURE`. Failure cleanup (L-01) clears the holder, preserves a pre-existing session (including a signed-in user's session) and restores its previous context, and invalidates only a session created by the attempt. Logs no email, password, session id, exception message, or stack trace — technical failures log the exception class name only. |
| **Entry point / errors** (`UnauthenticatedEntryPoint`) | 401 via `sendError`; CSRF/authz failures render 403; Boot error JSON on ERROR dispatch. |
| **Config** | `application.yml` reads session/cookie/absolute-lifetime from env vars (see §11); `ClockConfig` provides an injectable `Clock`. `HealthController.HEALTH_PATH` made `public`. Added `spring-boot-starter-validation` to `pom.xml`. |

**Credential-source guard:** because persistence is blocked (OD-02/OD-03), production has **no** `UserDetailsService`. Rather than hardcode fake credentials, `SignInService` detects the missing credential source and answers an explicit `503 sign-in-not-configured`. Production sign-in therefore **cannot succeed** — this is honest, not a defect.

## 6. Provisional API Contract (NOT APPROVED — proposal only)

No approved auth API contract exists (SRS defers the endpoint catalogue, status codes, and error shapes). The following is a **proposal recorded for review only** and must not be treated as final. The frontend is deliberately **not** connected to it.

| Endpoint | Success | Error bodies |
|---|---|---|
| `POST /api/auth/sign-in` | `204` (session cookie + `XSRF-TOKEN` set) | `400 {"error":"validation-failed","fieldErrors":{...}}`; `401 {"error":"sign-in-failed","message":"Incorrect email or password. Please try again."}`; `503 {"error":"sign-in-not-configured","message":...}`; `500 {"error":"technical-failure","message":...}` |
| `POST /api/auth/sign-out` | `204` | framework defaults |

CSRF: safe request → read `XSRF-TOKEN` cookie → echo in `X-XSRF-TOKEN` header on state-changing requests.

The `503 sign-in-not-configured` response is intentionally **provisional**. Returning it to an unauthenticated client reveals that the deployment has no credential source. Whether that deployment-state disclosure is acceptable remains **pending (M-04)**; this fix pass did not change the response, invent a decision, or add a disclosure note to the response body.

## 7. Session and Cookie Behaviour

| Property | Value | Enforced by |
|---|---|---|
| Session model | Server-managed session | `HttpSessionSecurityContextRepository` |
| Session cookie | `HttpOnly`, `Secure` in production, `SameSite` **provisional `lax`** | `application.yml` env vars; production-profile `Secure` startup guard (`ProductionSessionCookieGuard`) |
| Idle timeout | 30 minutes | Servlet container (`server.servlet.session.timeout`) |
| Absolute lifetime | 8 hours | `AbsoluteSessionLifetimeFilter` (injected `Clock`) |
| Session fixation | Migration on sign-in | `ChangeSessionIdAuthenticationStrategy` (see §13) |
| CSRF | Enabled, cookie double-submit | `CookieCsrfTokenRepository` + `CsrfTokenRequestAttributeHandler` |

Tokens are never placed in `localStorage`/`sessionStorage`; no bearer tokens exist.

## 8. Files Created, Modified, or Deleted

**Main (untracked unless noted):** `backend/pom.xml` (modified), `shared/config/ClockConfig.java`, `auth/SecurityConfig.java`, `auth/UnauthenticatedEntryPoint.java`, `auth/CsrfCookieFilter.java`, `auth/SignInRequest.java`, `auth/SignInOutcome.java`, `auth/SignInService.java`, `auth/SignInController.java`, `auth/AbsoluteSessionLifetimeFilter.java`, `auth/ProductionSessionCookieGuard.java`, `application.yml` (modified), `shared/health/HealthController.java` (modified), `InternFlowApplicationTests.java` (modified). **Deleted:** `shared/config/SecurityConfig.java`.

**Test:** `TestAuthFixtures.java`, `MutableClock.java`, `TestProtectedController.java`, `TestCookies.java`, `MockMvcSecurityTestSupport.java`, `PasswordEncoderTest.java`, `SecurityConfigurationTest.java`, `SignInEndpointTest.java`, `AbsoluteSessionLifetimeEnforcementTest.java`, `SignInNotConfiguredTest.java`, `SignInTechnicalFailureTest.java`, `SignInPersistenceFailureTest.java`, `ProductionSessionCookieGuardTest.java`, `SessionCookieIntegrationTest.java`.

## 9. Dependencies Changed

Added `spring-boot-starter-validation` (server-side request validation, SRS-IF-03 / SAD §6). **No** `spring-security-test` (CSRF acquired manually). No other dependency added.

## 10. Commands Executed and Validation Results

All commands run from `backend/` unless noted. **No test is claimed as passed unless it actually executed.**

| Command | Result |
|---|---|
| `mvn -B test` | **PASS — 38 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS (actual exit code 0).** Breakdown: `SignInEndpointTest` 17, `SessionCookieIntegrationTest` 5, `ProductionSessionCookieGuardTest` 4, `SecurityConfigurationTest` 3, `SignInPersistenceFailureTest` 3, `SignInNotConfiguredTest` 2, `AbsoluteSessionLifetimeEnforcementTest` 1, `PasswordEncoderTest` 1, `SignInTechnicalFailureTest` 1, `InternFlowApplicationTests` 1. |
| `mvn -B verify` | **PASS — 38 tests, 0 failures, 0 errors, 0 skipped, jar built, BUILD SUCCESS (actual exit code 0).** |
| Targeted fix-pass run (`-Dtest=SignInEndpointTest,SignInPersistenceFailureTest,SignInTechnicalFailureTest,ProductionSessionCookieGuardTest`) | **PASS — 25 tests, 0 failures, 0 errors, BUILD SUCCESS (actual exit code 0).** |
| `git diff --check` (repo root) | **exit code 0**; no whitespace errors; one benign LF/CRLF notice on `application.yml`. |
| `git status --short` / `git diff --stat` | Expected modified/deleted/untracked set (§8); nothing unintended, nothing staged. |
| Diff + secret scan | No secrets or credentials; the only credential literal is the test-fixture password (`fixture-password-1234`), which is test-only. |
| Packaged-jar production guard (manual runtime check) | With `--spring.profiles.active=prod` and no `Secure` override: process **exit code 1** and the guard's failure message. With `--server.servlet.session.cookie.secure=true`: guard reported success and the application started (`Started InternFlowApplication`); the process was terminated manually after confirmation. This is **manual runtime verification, not a JUnit test**. |

**Passed vs. not-executed:** every automated test above was executed and passed. The **idle-timeout behavioural test was not executed** — Boot maps the timeout to whole minutes (minimum 60 s wall-clock), so a real sleep-based assertion is impractical in the suite. It is **substituted**, not faked, by config-level assertions (`SecurityConfigurationTest` asserts `ServerProperties` timeout `PT30M`, cookie flags, and BCrypt) plus a Tomcat-level assertion (`SessionCookieIntegrationTest` asserts `StandardContext.getSessionTimeout() == 30`). This is a documented coverage limitation, not a pass. The packaged-jar guard run is manual evidence; the four `ProductionSessionCookieGuardTest` context-runner tests are the automated evidence and cover the fail/pass paths at the application-context level.

## 11. Configuration and Environment Variables

No hardcoded secrets. Database via `INTERNFLOW_DB_URL` / `INTERNFLOW_DB_USERNAME` / `INTERNFLOW_DB_PASSWORD`. New session/cookie variables (all optional locally): `INTERNFLOW_SESSION_IDLE_TIMEOUT` (default `30m`), `INTERNFLOW_SESSION_ABSOLUTE_LIFETIME` (default `PT8H`), `INTERNFLOW_COOKIE_SECURE` (default `false`; **`true` on TLS**), `INTERNFLOW_COOKIE_SAMESITE` (default `lax` — **provisional**, re-decide with IF-DEC-10). `application.yml` retains `hibernate.ddl-auto: none` and the explicit PostgreSQL dialect so the context starts without a live DB.

**Production guard (M-01):** when the Spring profile `prod` or `production` is active, the effective `server.servlet.session.cookie.secure` value must be `true`, or `ProductionSessionCookieGuard` aborts startup. Set `INTERNFLOW_COOKIE_SECURE=true` (or the equivalent bound Spring Boot property). Local HTTP development may keep the default `false`. Only those two profile names are recognized; arbitrary production-like profile names do **not** activate the guard. The guard reads the bound configuration (not the environment variable directly) and does **not** verify TLS termination, forwarded-header/`X-Forwarded-Proto` handling, reverse-proxy configuration, or HSTS — those remain deployment responsibilities. `docs/development/SETUP.md` records the same contract.

## 12. Requirements Fulfilled

Server-managed sessions, HttpOnly cookie, `Secure` in production (with the M-01 startup guard), CSRF enabled, email identity, BCrypt, 30-min idle / 8-h absolute lifetime, session-fixation protection, explicit session-creation policy, deny-by-default authorization, logout, error handling at the sign-in boundary, and a credential-source guard that refuses to fake authentication — all implemented and covered by executed tests.

**Password policy boundary:** the approved account-password policy (at least 12 characters, no mandatory character-class combinations) is **not yet enforced by an account-creation or password-change implementation**, because neither flow exists yet. The sign-in DTO enforces only presence and the L-02 upper bounds; the 12-character minimum is not a sign-in validation rule and must not be claimed as implemented. It belongs with the future account-creation / first-login password-change work.

## 13. Blockers, Gaps, and Key Decisions

| Item | Status |
|---|---|
| **First DB migration / persisted accounts** | **Blocked** — OD-02 + OD-03 unresolved (FRD §9.1). Bypassed only in tests via a fixture `UserDetailsService`. Production sign-in cannot succeed. |
| **Approved API contract** | **Absent** — provisional endpoints only (§6). Frontend stays unconnected. |
| **Rate-limit thresholds (M-03)** | **Unresolved — not implemented, not invented.** Real account authentication must **not** be enabled until rate limiting is addressed or an explicit decision is approved. No technical gate currently prevents adding a `UserDetailsService`, so this is an approval condition/checklist item, not an enforced invariant. |
| **`sign-in-not-configured` disclosure (M-04)** | **Pending decision** — the provisional `503` behavior is unchanged; the deployment-state disclosure is documented (§6). No response change without authorization. |
| **Production cookie guard (M-01)** | **Implemented with limitations** — `ProductionSessionCookieGuard` fails startup under an active `prod`/`production` profile when the effective session-cookie `Secure` flag is not `true`. It recognizes only those profile names and does **not** validate TLS termination, forwarded headers, reverse-proxy configuration, or HSTS. Four automated tests plus a manual packaged-jar check (§10). |
| **Failed-attempt session handling (L-01)** | **Fixed** — the holder is cleared; a pre-existing session (including a signed-in user's session) is preserved and its previous context restored if the attempt wrote one; only a session created by the attempt is invalidated. Covered by the endpoint regression test and the three persistence-failure tests (§10, §18). |
| **Technical-failure logging** | **Fixed** — only the exception class name is logged at debug; messages and stack traces (which can carry identity/query details) are omitted. A captured-log regression test asserts the fixture email and exception message are absent. |
| **Input bounds (L-02)** | **Fixed** — email `@Size(max=254)`, password `@Size(max=256)`; boundary and enumeration tests. |
| **Dependency comment (L-03)** | **Fixed** — corrected the `pom.xml` Spring Security comment; no functional change. |
| **Final SameSite** | **Unresolved** — provisional `lax`, tied to IF-DEC-10 topology. |
| **Forced password change on first login** | **Blocked** — needs persistence + contract. Not faked. |
| **Idle-timeout behavioural test** | **Not executed** (impractical); substituted by config + Tomcat assertions (§10). |
| **Session-fixation decision** | Spring Security applies the DSL `migrateSession()` strategy only from `AbstractAuthenticationProcessingFilter` (filter-based logins); a controller-based login never triggers it via `SessionManagementFilter`. `SignInService` therefore **explicitly** invokes `ChangeSessionIdAuthenticationStrategy` (the same class the DSL installs) before `saveContext`. Verified by a RANDOM_PORT **double sign-in** test (sign in, re-present the first session cookie, assert the id migrates and the old id is dead). MockMvc cannot exercise migration, and no test-only permitAll/creating endpoint was introduced. |

## 14. Scope-Boundary Verification

Stayed on `stage-2-ui-authentication`; no branch switch, merge, push, or commit. No frontend change. No JWT/bearer/OAuth/public registration. No production credentials. No invented contract, DB decisions, rate-limit thresholds, or SameSite value. No Stage 2.4 work started.

## 15. Manual Checks (short)

These are **not** covered by the automated suite unless stated; they remain for the user's environment.

1. **Production guard:** start the packaged jar with `--spring.profiles.active=prod` (or `production`) and the effective session-cookie `Secure` flag absent/`false` → expect startup to abort with the guard's message. Repeat with `INTERNFLOW_COOKIE_SECURE=true` or `--server.servlet.session.cookie.secure=true` → expect a normal start. (The packaged-jar runs recorded in §10 were done; repeat on the target deployment.)
2. **TLS/proxy:** behind the real TLS terminator, confirm `Set-Cookie` carries `Secure`; verify `X-Forwarded-Proto`/forwarded-header handling and the final `SameSite` once IF-DEC-10 is decided. The guard does **not** check these.
3. `GET /api/health`, read `XSRF-TOKEN`, `POST /api/auth/sign-in` echoing `X-XSRF-TOKEN` → expect `503 sign-in-not-configured` (no DB yet).
4. Confirm a state-changing request without the CSRF header is rejected with `403`.
5. Confirm two anonymous requests produce distinct session cookies and a re-presented pre-auth cookie id is rejected (fixation).
6. **Failed sign-in with an existing signed-in session (browser):** sign in, then submit a bad-password sign-in in the same browser session; the failed attempt should return `401` and the existing session should remain usable. MockMvc/direct-service coverage exists; the browser check remains manual.
7. **Before enabling real accounts:** resolve/approve M-03 (rate limiting) and decide M-04 (`sign-in-not-configured` disclosure). Do not enable a real `UserDetailsService` until then.

## 16. Recommended Next Substage

Resolve **OD-02 / OD-03** so the first migration and a persisted `UserDetailsService` can land, then approve an API contract for the `/api/auth/*` endpoints and connect the Stage 2.2 frontend seam. Only after that should first-login password change be designed. **Before any real account authentication is enabled, M-03 (rate limiting) must be addressed or explicitly approved, M-04 must be decided, and the deployment must activate a recognized `prod`/`production` profile so the M-01 guard runs.**

## 17. Final Git State and Commit Status

| Item | Value |
|---|---|
| Branch | `stage-2-ui-authentication` |
| HEAD | `bcfd895` (unchanged) |
| Committed this substage | **Nothing** — no commit, merge, push, or branch switch. |
| Working tree | All Stage 2.3 changes present but **uncommitted**; approval recorded in §19; nothing staged. |
| Validation | `mvn -B test`: 38/38 pass, exit code 0. `mvn -B verify`: 38/38 pass, exit code 0, jar built. `git diff --check`: exit code 0. |

**Stage 2.3 status: approved with documented limitations (2026-10-22). Implementation and security-fix pass validated (38/38). The approval does not resolve OD-02/OD-03, the missing approved API contract, M-02 (final SameSite/topology), M-03 (rate limiting), or M-04 (503 disclosure); see §13, §18, and §19.**

## 18. Security-Fix Pass (post-review, 2026-10-22)

This section records the fixes applied after the 2026-10-21 review. They are **uncommitted** on `stage-2-ui-authentication` at HEAD `bcfd895`. The stage was subsequently approved with documented limitations on 2026-10-22 (§19); this section is retained as the fix-pass record.

| Finding | Change | Evidence |
|---|---|---|
| **L-01** — failed sign-in destroyed a pre-existing authenticated session | `SignInService.attemptSignIn` snapshots the request's session and its stored `SecurityContext` before authentication. `abandonSignInAttempt` now clears the holder, preserves a session that already existed (including a signed-in user's), and restores the previous context if the failed attempt wrote its new context into that session; only a session created by the attempt is invalidated. The success path and `ChangeSessionIdAuthenticationStrategy` fixation step are unchanged. | `SignInEndpointTest.failedSignInDoesNotDestroyAnExistingAuthenticatedSession` (sign in, submit bad password with the same session, follow-up protected GET still `200`). `SignInPersistenceFailureTest`: `persistenceFailureAfterAuthenticationAnswersTechnicalFailureAndDiscardsTheSessionItCreated`, `failedCompletionClearsTheHolderAndDiscardsTheAttemptCreatedSession`, `persistenceFailurePreservesAPreExistingSessionAndRestoresItsPreviousContext`. Existing success/fixation tests (`SignInEndpointTest.validCredentialsEstablishASessionThatLaterRequestsReuse`, `SessionCookieIntegrationTest` double sign-in) still pass. |
| **L-02** — oversized credentials handled inconsistently | `SignInRequest` keeps `@Size(max=254)` on email and `@Size(max=256)` on password, so oversized input is a boundary validation failure rather than an authentication attempt. | `SignInEndpointTest.oversizedPasswordIsAValidationFailureNotAAuthenticationAttempt`, `maximumLengthPasswordStillReachesCredentialVerification`, `oversizedPasswordFailsIdenticallyForKnownAndUnknownAccounts`, `oversizedEmailIsAValidationFailure`, `shortWellFormedEmailIsNotRejectedForLength`. |
| **L-03** — inaccurate dependency comment | Corrected the `backend/pom.xml` comment describing the Spring Security dependency; no functional change. | `mvn -B verify` resolves the same dependency set and builds successfully. |
| **M-01** — production cookie `Secure` flag not enforced | Added `ProductionSessionCookieGuard`: with an active `prod`/`production` profile, startup fails unless the effective `server.servlet.session.cookie.secure` is `true`. | `ProductionSessionCookieGuardTest`: `productionProfileWithSecureCookieDisabledFailsStartup`, `productionProfileWithSecureCookieUnsetFailsStartup`, `productionProfileWithSecureCookieEnabledStarts`, `devStartupIsUnaffectedByTheGuard`; plus the manual packaged-jar run in §10. |
| **Cookie-cleanup comment** | Corrected the `abandonSignInAttempt` javadoc: it no longer claims to delete a browser cookie. It explains that invalidation makes an attempt-created session id unusable, but the helper has no response and does not delete cookies. | Documentation/code review; behavior covered by the L-01 tests. |
| **Technical-failure logging** | Replaced `logger.debug(..., ex)` with type-only logging (`ex.getClass().getName()`). Exception messages and stack traces are no longer logged, because a credential-store exception can carry the submitted identity or query details. Class javadoc and catch comments now state this accurately. | `SignInTechnicalFailureTest` sets the service logger to DEBUG, makes the fixture throw a message containing the submitted email, asserts the type-only line is captured, and asserts the email and exception message are absent from the output. |

**Remaining findings and limitations (not fixed by this pass):**

- **M-02 — final `SameSite` / deployment topology:** still unresolved and tied to IF-DEC-10; the provisional `lax` value is unchanged.
- **M-03 — rate limiting:** not implemented and no thresholds invented. **Real account authentication must not be enabled until rate limiting is addressed or an explicit decision is approved.** No technical gate currently prevents adding a `UserDetailsService`; this is an approval/checklist condition.
- **M-04 — `sign-in-not-configured` disclosure:** the provisional `503` is unchanged. Whether revealing the missing-credential-source state is acceptable remains an open decision.
- **M-01 limitations:** the guard recognizes only the profile names `prod` and `production`; it does not verify TLS termination, forwarded-header/`X-Forwarded-Proto` handling, reverse-proxy configuration, or HSTS. It checks the session cookie's effective `Secure` setting, not the `XSRF-TOKEN` cookie.
- **D-4 (Low) — CSRF token not rotated on authentication:** the `XSRF-TOKEN` cookie is not rotated when a session is authenticated. Not changed in this pass; evaluate before enabling real accounts or a cross-site topology.
- **OD-02 / OD-03:** still block the first database migration and persisted accounts. Production sign-in still answers `503 sign-in-not-configured`; no real credential source exists.
- **Approved API contract:** still absent; the `/api/auth/*` endpoints remain provisional and the frontend remains unconnected.
- **Forced first-login password change:** not implemented; the 12-character account-password policy is **not** enforced on `SignInRequest` or any account-creation flow, because that flow does not exist yet.
- **Idle-timeout behavioural test:** not executed (impractical wall-clock sleep); substituted by configuration and Tomcat assertions (§10).
- **Manual checks:** TLS termination, forwarded headers, reverse-proxy/HSTS behavior, production-profile activation, browser cookie/fixation behavior, and the browser-level failed-sign-in check remain for the user's environment (§15).

No Critical or High findings remain in this pass. The known items above are documented limitations and pending decisions, not claims of completeness.

## 19. Approval Record and Stage 2.4 Verification (2026-10-22)

The product owner approved Stage 2.3 with the documented limitations. Approval does not resolve OD-02/OD-03, the missing approved API contract, M-02 (final `SameSite`/topology), M-03 (rate limiting), M-04 (`sign-in-not-configured` disclosure), D-4 (CSRF-token rotation), or the unimplemented first-login password change.

Stage 2.4 independently re-ran the backend suite (`mvn -B test` and `mvn -B verify`: 38/38, both exit code 0), frontend `npm run lint` and `npm run typecheck` (both exit code 0), and inspected the filter-chain, session, logout, and CSRF behavior. No new defect was found and no production code was changed. The verification record and acceptance recommendation are in `docs/reviews/STAGE-2.4-REVIEW.md`.

