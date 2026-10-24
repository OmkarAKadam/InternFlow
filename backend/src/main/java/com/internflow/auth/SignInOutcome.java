package com.internflow.auth;

/**
 * Outcomes of a sign-in attempt, kept separate from HTTP concerns so the
 * controller maps them and the service stays transport-agnostic.
 *
 * <p>The categories follow the FRD §7.1 failure categories rather than an HTTP
 * guess: {@link #REJECTED} is an authentication failure with one generic
 * message (FR-AUTH-001), {@link #NOT_CONFIGURED} is the explicit
 * "configuration-missing" state (never a false compliant/negative result), and
 * {@link #TECHNICAL_FAILURE} is an internal error that must not be reported as
 * "wrong credentials".</p>
 */
enum SignInOutcome {

    /** Valid credentials; an authenticated session was established (FR-AUTH-001). */
    AUTHENTICATED,

    /**
     * Credentials did not match, or the account would not be permitted to sign
     * in. One generic message covers every cause so responses are
     * indistinguishable (FR-AUTH-001 AC2/AC3/AC4, FR-AUTH-006 AC1).
     */
    REJECTED,

    /**
     * No credential source is configured in this environment — account
     * persistence does not exist yet (blocked by OD-02/OD-03). Reported
     * explicitly as configuration-missing (FRD §7.1) rather than pretending
     * the credentials were wrong.
     */
    NOT_CONFIGURED,

    /**
     * An internal error prevented verification (e.g. the credential lookup
     * failed). Distinct from {@link #REJECTED} so a broken environment is never
     * reported as bad credentials (FRD §7.1 technical failure).
     */
    TECHNICAL_FAILURE
}
