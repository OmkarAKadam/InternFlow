/**
 * Sign-in integration boundary (Stage 2.2).
 *
 * The login form calls `attemptSignIn` and never talks to the API client or
 * fetch directly. This module is the single seam where a real authentication
 * request will be wired once an approved API Specification exists.
 *
 * Current behavior (deliberate, not a stub pretending to work):
 * - No endpoint path, request body, response shape, or success/failure
 *   semantics are invented here. No approved authentication contract exists
 *   (SAD §10; SRS-IF-01 defers the endpoint catalogue and payload schemas).
 * - `attemptSignIn` performs NO network request and always reports
 *   `integration-pending`. It cannot authenticate anyone and cannot leave the
 *   UI in a state that looks authenticated.
 * - Credentials are used only for client-side required/format validation and
 *   are never stored, logged, or persisted (no localStorage/sessionStorage).
 *
 * To connect to a real backend later:
 * 1. Define the authentication endpoint in the API Specification substage
 *    (path, payload, response, and FRD §7.1 failure-category mapping).
 * 2. Replace the pending return below with an `apiRequest` call, mapping
 *    outcomes to `authenticated` / `rejected` / `failed` (see the type
 *    contract on {@link SignInOutcome}).
 * 3. On `authenticated`, navigate to the role-specific landing surface
 *    (FR-AUTH-001 AC1) once those routes exist.
 */

export interface SignInInput {
  email: string;
  password: string;
}

export type SignInOutcome =
  /** The request was accepted by the backend and a session exists. */
  | { status: "authenticated" }
  /** The backend refused the credentials — generic message required
   *  (FR-AUTH-001, FR-AUTH-006 AC1: never distinguish unknown account from
   *  wrong password). */
  | { status: "rejected" }
  /** A technical/network failure prevented the result from being known
   *  (FRD §7.1 technical failure). */
  | { status: "failed" }
  /** No approved authentication API contract exists yet, so no request was
   *  sent. This is the only outcome Stage 2.2 can produce. */
  | { status: "integration-pending" };

/**
 * Attempt to sign in. See module docblock: currently performs no request.
 *
 * The `input` parameter is accepted so the form's call site is already the
 * real integration shape, but the credentials are not used, sent, or stored.
 */
export async function attemptSignIn(input: SignInInput): Promise<SignInOutcome> {
  // Reference the input so the contract is explicit and lint-clean, without
  // sending, logging, or persisting it anywhere.
  void input;

  // INTEGRATION PENDING: no approved API Specification defines the sign-in
  // endpoint (path, payload, response). Inventing one here would create a
  // speculative contract and a fake authenticated state. Returning
  // `integration-pending` keeps the UI honest: nothing was attempted.
  return { status: "integration-pending" };
}
