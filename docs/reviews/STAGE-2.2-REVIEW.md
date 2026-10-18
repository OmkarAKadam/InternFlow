# InternFlow — Stage 2.2 Review Report: UI Foundation & Login Interface

**Project:** InternFlow

## 1. Document Control

| Field | Value |
|---|---|
| Document Title | Stage 2.2 Review Report — UI Foundation & Login Interface |
| Project Name | InternFlow |
| Document Identifier | IF-DOC-REV-002 |
| Version | 1.0 |
| Status | Draft — Pending Review. Covers Stage 2.2 only. Stage 2 as a whole is **not** complete: backend authentication, sessions, and role-based sections remain for later Stage 2 substages. |
| Date | 2026-10-18 |
| Branch | `stage-2-ui-authentication` (Stage 2 substage branch; no substage branches were created) |
| Scope | Stage 2.2 only: frontend UI foundation, a responsive `/login` interface with client-side validation, loading/error states, a minimal API client foundation, and an explicit integration boundary. No backend code, Spring Security changes, sessions, cookies, CSRF, password hashing, database schema, or migrations were touched. |
| Related Documents | Stage 2.1 Review Report (IF-DOC-REV-001); PRD, FRD, SRS (`docs/requirements/`); SAD, DDD, DECISIONS (`docs/architecture/`); SETUP (`docs/development/`). This report resolves no open decision and changes no requirements baseline. |

**Status conventions** follow the existing documents (FRD §2.4, SRS §2.6, DECISIONS §1).

## 2. Substage Objective and Scope

Stage 2.2 established the frontend UI foundation and a login interface built strictly on the authentication design approved as IF-DEC-19 in Stage 2.1 (server-managed sessions, HttpOnly cookies, CSRF enabled, email as login identity, admin-created accounts, BCrypt, 12-character minimum, 30-minute idle / 8-hour absolute lifetimes, rate limiting without permanent lockout, Company Mentor excluded). **That approval covers the design; no backend implementation exists yet.**

In scope: UI foundation (typography, color/spacing tokens, focus states), reusable components needed by the login page, the `/login` route, client-side validation, loading and error states, a minimal typed API client foundation, and accurate documentation.

Out of scope (per the substage plan and preserved here): backend login endpoint, Spring Security changes, sessions, cookies, CSRF, password hashing, first-login password change, account creation, database schema, role-based backend authorization, protected business modules, deployment, and any fake or simulated authentication.

## 3. Initial Git State

| Item | Observation |
|---|---|
| Branch | `stage-2-ui-authentication` (correct; verified before any edit) |
| Working tree | Clean at start (`git status --short` empty) |
| Latest commit | `c019ae7` — "docs: record approved authentication decisions" (matches the expected Stage 2.1 commit; Stage 2.1 documentation was committed, so this substage began from a clean tree) |

## 4. Existing Frontend Foundation (as inspected)

| Item | Observation |
|---|---|
| Stack | Next.js 16.4.2 (App Router, Turbopack), React 19.3.0, TypeScript (strict), Tailwind CSS v4 (`@import "tailwindcss"`), ESLint 9 flat config. Scripts: `dev`, `build`, `start`, `lint`, `typecheck`. No test script and no test framework. |
| Dependencies | Only `next`, `react`, `react-dom` at runtime; Tailwind/TypeScript/ESLint as dev tooling. No component library, no state manager, no data-fetching library. None was added this substage. |
| Routes | Only `/` (placeholder landing page). |
| Layout | Root `layout.tsx` with Geist/Geist Mono via `next/font`, metadata for InternFlow, `lang="en"`. |
| Design tokens | `globals.css` had `--background`/`--foreground` (light/dark) and registered `--font-sans: var(--font-geist-sans)`. |
| Encoding | All files inspected are valid UTF-8. An earlier PowerShell scan flagged `page.tsx`/`README.md`; a byte-level scan confirmed these were legitimate em dashes (`—`), not mojibake. No encoding fix was needed or made. |
| API conventions | None existed. `frontend/.env.example` defines `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080` (placeholder). |

**Defects found and fixed:** `body { font-family: Arial, … }` overrode the registered Geist variable, so the intended font never applied (noted as finding 3 in Stage 2.1 §3.4). Fixed by referencing `var(--font-geist-sans)` with fallbacks.

## 5. Implementation Summary

1. **UI foundation** — `globals.css` now defines a small token set (background, foreground, surface, border, primary, danger, on-primary; light and dark) consumed through `@theme inline`; the body font bug is fixed; a single global `:focus-visible` outline provides consistent keyboard focus styling.
2. **Shared components** — `Button`, `FormField`, `Alert`, `LoadingIndicator`, typed and small, no added dependencies.
3. **Login page** — `/login` inside an `(unauthenticated)` route group (per SAD §7 and the Stage 2.1 recommendation); email + password form with labels, `type`/`autoComplete` attributes, required and email-format validation, loading state with disabled submit, generic error presentation, and accessible announcements.
4. **Integration boundary** — `lib/auth/sign-in.ts` separates the form from any API call. Because **no approved authentication API contract exists** (SAD §10; SRS-IF-01 defers the endpoint catalogue and payload schemas), `attemptSignIn` performs **no network request** and returns `integration-pending`, which the UI presents honestly. It cannot produce an authenticated state.
5. **API client foundation** — `lib/api/client.ts` reads `NEXT_PUBLIC_API_BASE_URL`, exposes `apiRequest` for generic JSON requests, and maps failures to configuration/network/HTTP categories without leaking technical detail to users. It defines **no endpoints** and is **not yet called** by the login form; `credentials` is deliberately left at the fetch default because cross-origin cookie behavior depends on the unresolved deployment topology (IF-DEC-10) and cookie policy (IF-DEC-19 implementation).
6. **Landing page** — `/` remains a neutral landing page and now links to `/login`. Redirect-vs-link behavior for authenticated users is deferred to the routing/session substages rather than invented (no requirement specifies it).

## 6. UI Components and Routes Created

| Path | Type | Purpose |
|---|---|---|
| `frontend/src/app/(unauthenticated)/login/page.tsx` | Created | `/login` route: page chrome, headings, renders `SignInForm`. |
| `frontend/src/features/auth/sign-in-form.tsx` | Created | Client component: validation, submission state, error/pending messaging. |
| `frontend/src/components/ui/button.tsx` | Created | Button (primary/secondary/ghost, sizes, `loading`/`disabled`). |
| `frontend/src/components/ui/form-field.tsx` | Created | Label + input + hint + error, wired via `htmlFor`/`aria-describedby`/`aria-invalid`. |
| `frontend/src/components/ui/alert.tsx` | Created | Message box; `role="alert"` for errors, `role="status"` for info. |
| `frontend/src/components/ui/loading-indicator.tsx` | Created | Visual spinner with visible label (announcement handled by the caller's live region). |
| `frontend/src/lib/auth/sign-in.ts` | Created | Auth integration boundary (seam for the future real request). |
| `frontend/src/lib/api/client.ts` | Created | Minimal typed API client foundation (no endpoints defined). |
| `frontend/src/app/page.tsx` | Modified | Added "Go to sign in" link; updated copy to remain accurate. |
| `frontend/src/app/globals.css` | Modified | Design tokens, font fix, focus-visible rule. |

No component library or dependency was introduced. No future role dashboards, logout, session handling, or protected routes were built.

## 7. Login Form Behavior and Validation

- **Fields:** email (`type="email"`, `autoComplete="email"`, `inputMode="email"`) and password (`type="password"`, `autoComplete="current-password"`), both `required`, both with visible labels. `noValidate` is set on the form so validation is ours and announced consistently.
- **Validation (advisory only; server remains authoritative per NFR-SEC-005/SRS-IF-03):** empty email → "Email is required."; malformed email (single permissive pattern) → "Enter a valid email address."; empty password → "Password is required.". Errors clear as the user edits. On failed validation focus moves to the first invalid field, which is marked `aria-invalid` with the message linked via `aria-describedby`.
- **Submission:** while a submission is in flight the button is `disabled` and shows "Signing in…"; a re-entrancy guard prevents double submission even via keyboard.
- **Failure presentation:** one generic message — "Incorrect email or password. Please try again." — covers invalid credentials; it is deliberately identical for unknown account, wrong password, and deactivated account (FR-AUTH-001, FR-AUTH-006 AC1). Technical failures use a separate generic message. No account existence, activation state, or rate-limit status is exposed.
- **Integration-pending state:** with no approved contract, submission shows an explicit notice that sign-in is not connected, that no request was sent, and that the user cannot sign in. The interface never claims success and never appears authenticated.
- **Credentials:** held only in component state, never persisted (no `localStorage`/`sessionStorage`/cookies), never logged, and not sent anywhere.
- **No public registration, no Company Mentor, no unapproved authentication methods** appear anywhere in the UI.

## 8. API Integration Status and Contract Dependencies

**The login form is not connected to a real backend, because no approved authentication API contract exists.** This is not an oversight: inventing an endpoint path, request body, response format, or authentication semantics would create a speculative contract and a fake authenticated state, both explicitly forbidden.

What exists now:
- `lib/auth/sign-in.ts` — the seam. `attemptSignIn` returns `integration-pending`; its `SignInOutcome` type already models `authenticated`/`rejected`/`failed` per FR-AUTH-001/FRD §7.1 so the form's call site is the real integration shape.
- `lib/api/client.ts` — base-URL resolution and generic request mechanics, unused by the form today, with `credentials` deliberately unset.

Required to connect (for a later substage):
1. An API Specification defining the sign-in endpoint: path, method, request payload, response shape, and mapping to FRD §7.1 failure categories (SRS-IF-04).
2. Backend hosting decision (IF-DEC-10) and cookie policy (IF-DEC-19: HttpOnly, Secure in production, SameSite) before cross-origin `credentials`/CORS behavior can be coded.
3. Role-specific landing surfaces (FR-AUTH-001 AC1) before a successful sign-in can navigate anywhere.

No cross-origin cookie behavior has been assumed. No secrets or tokens appear in `NEXT_PUBLIC_` variables.

## 9. Accessibility and Responsive Design Checks

Implemented: visible labels on all fields; keyboard-operable form with Enter-to-submit; a single visible `:focus-visible` outline; errors associated with their fields via `aria-describedby` and marked with `aria-invalid`; `role="alert"` for error messages; a persistent `role="status"`/`aria-live="polite"` region whose text changes to announce the loading state (a live region must exist before its text changes to be announced reliably); double-submission prevented; `motion-reduce` respected by the spinner; color contrast considered via the token palette; layout uses `max-w` containers and viewport padding with no fixed widths that could overflow at 360px.

**Explicitly not claimed:** no WCAG or any accessibility conformance level is asserted — the conformance target remains an open decision (SRS-UI-07). **No browser or assistive-technology test was run** (no Playwright or other browser infrastructure exists yet, and none was added). Responsiveness and announcements were verified by construction and code review only, not by automated or manual browser testing.

## 10. Files Created, Modified, or Deleted

Created (8): the eight `frontend/src/...` files listed in §6. Modified (4): `frontend/src/app/page.tsx`, `frontend/src/app/globals.css`, `frontend/README.md`, `docs/development/SETUP.md`. Deleted: none. No backend file, requirement baseline, or architecture decision record was touched.

## 11. Dependencies Changed

**None.** No package was added, removed, or upgraded. All functionality uses React, Next.js, and Tailwind CSS already present. `frontend/.env.example` was left unchanged (its `NEXT_PUBLIC_API_BASE_URL` placeholder remains correct).

## 12. Commands Executed and Verification Results

| Command | Result |
|---|---|
| `npm run lint` (in `frontend/`) | ✅ Exit 0, no errors or warnings. |
| `npm run typecheck` (in `frontend/`) | ✅ Exit 0, no TypeScript errors. |
| `npm run build` (in `frontend/`) | ✅ Exit 0. Compiled successfully; routes: `/`, `/_not-found`, `/login` (all static). No errors or warnings. |
| `git diff --check` | ✅ Exit 0, no whitespace errors. (Informational LF→CRLF `autocrlf` warnings on Windows only.) |
| `git status --short` | 4 modified tracked files (`SETUP.md`, `frontend/README.md`, `globals.css`, `page.tsx`) + untracked new paths (`docs/reviews/STAGE-2.2-REVIEW.md` and the new `frontend/src` directories `(unauthenticated)/`, `components/`, `features/`, `lib/`), all expected. |
| `git branch --show-current` | `stage-2-ui-authentication` (unchanged). |
| `git rev-parse --short HEAD` | `c019ae7` (unchanged — nothing committed). |

Scope-boundary greps over `frontend/src`: no `localStorage`/`sessionStorage`/`document.cookie` use (only comments stating they are not used); the only `fetch(` is inside `lib/api/client.ts`, never in components; no `/api/...` endpoint strings (matches are `@/lib/...` import paths); no Mentor, registration, or hardcoded credentials anywhere.

**Tests:** no test infrastructure exists in `frontend/` (no test script, no framework), and none was introduced for this substage per the plan. The behaviors listed for testing in the substage plan (rendering, validation, invalid email, loading/disabled, generic failure, network failure, responsive behavior) are implemented and covered by lint/build/typecheck, but **no unit or browser tests were executed** because no test runner exists yet.

## 13. Requirements Fulfilled and Remaining Gaps

Fulfilled (UI scope): `/login` route with accessible email+password form (FR-AUTH-001 UI portion); generic failure messaging and non-enumeration (FR-AUTH-001, FR-AUTH-006 AC1); responsive layout without horizontal overflow by construction (SRS-UI-05); advisory client-side validation (SRS-IF-03, NFR-SEC-005); no interface-based security reliance (SRS-UI-06 — nothing here enforces authorization, and none pretends to); no post-MVP functionality exposed (SRS-FR-002 — no Company Mentor, no registration).

Remaining gaps (expected — later substages): actual authentication (needs the API Specification), sessions and cookies, CSRF, password hashing and first-login change, rate limiting, role-based routing and dashboards, protected routes, and formal accessibility verification. No accessibility conformance claim is made (SRS-UI-07 open).

## 14. Scope-Boundary Verification

Verified against the substage's explicit out-of-scope list: no backend authentication code was written; no Spring Security configuration was changed; no sessions, cookies, CSRF, or password-hashing implementation exists; no database entities, schema, or migrations were created; no role-based backend authorization or protected business modules were built; no production deployment work was done; and **no fake authentication or hardcoded test credentials exist anywhere** — the login form provably cannot sign anyone in and says so in the interface. No speculative endpoint contracts were introduced. All existing functionality (the `/` page, health endpoint, build) remains intact.

## 15. Risks, Blockers, and Unresolved Decisions

- **Blocker for real login:** no approved authentication API contract (API Specification is a future deliverable, SAD §10). Until it exists, the login form cannot be connected without inventing a contract.
- **Open decisions carried unchanged:** IF-DEC-10 (backend hosting — affects cross-origin cookie/CORS behavior), IF-DEC-14 (API versioning — affects the client's future URL prefix), SRS-UI-05 (browser/device baseline), SRS-UI-07 (accessibility conformance target), plus the Stage 2.1 open items (OD-02/OD-03 blocking the first database migration; DB credentials supplied out-of-band). None was resolved or narrowed by this substage.
- **Risk:** the responsive and accessibility behaviors are unverified by any browser or assistive-technology testing; this should be covered when Playwright infrastructure is added (SAD §16 targets E2E tests for a later stage).
- **Not a blocker:** `git diff --check` reports informational LF→CRLF line-ending notices typical of Windows `autocrlf`; no whitespace errors exist.

## 16. Recommended Next Substage

**Stage 2.3 — Authenticated Application Shell and Role Routing**, or the API Specification work, depending on the owner's priorities. The API Specification (sign-in endpoint contract) is the critical path for connecting the login form; the authenticated shell (role route groups, session-aware navigation, logout) can proceed in parallel against the approved IF-DEC-19 design. Stage 2.3+ must also implement the FR-AUTH-007 AC2 / SRS-IF-04 requirement that the backend distinguishes 401 from 403 (the backend currently returns a blanket 403 for unauthenticated requests).

## 17. Final Git State and Commit Status

| Item | Value |
|---|---|
| Branch | `stage-2-ui-authentication` (unchanged) |
| HEAD | `c019ae7` (unchanged) |
| Working tree | 4 modified tracked files (`SETUP.md`, `frontend/README.md`, `globals.css`, `page.tsx`) + untracked new paths: `docs/reviews/STAGE-2.2-REVIEW.md` and the new `frontend/src` directories `(unauthenticated)/`, `components/`, `features/`, `lib/` (all expected; listed in §10) |
| Commit status | **Nothing was committed, merged, pushed, or stashed. No branch was created or switched.** |

This report covers Stage 2.2 only. Stage 2 as a whole remains in progress.
