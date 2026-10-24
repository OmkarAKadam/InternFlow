package com.internflow.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Sign-in request payload (shape only — <strong>provisional contract</strong>,
 * see {@link SignInController}).
 *
 * <p>Field semantics come from approved requirements, not invention:</p>
 * <ul>
 *   <li>{@code email} — the login identity is the email address (IF-DEC-19,
 *       closing OD-15); format checked at the server boundary (SRS-IF-03,
 *       NFR-SEC-005). The 254-character cap is the practical RFC 5321/5322
 *       limit and is a transport-sanity bound, not a policy decision.</li>
 *   <li>{@code password} — required presence only. The 12-character minimum is
 *       a <em>password-setting</em> policy (IF-DEC-19) and applies to
 *       provisioning and the forced change flow, neither of which exists yet;
 *       on sign-in every non-blank value goes to credential verification so all
 *       failures produce the single generic message (FR-AUTH-001,
 *       FR-AUTH-006 AC1) instead of a length-dependent response.</li>
 * </ul>
 *
 * <p><strong>Length bounds (security fix L-02).</strong> The password is capped
 * at 256 characters and the email at 254, so an oversized body is rejected
 * deterministically at the validation boundary instead of being handed to BCrypt
 * (which does fixed work on arbitrarily long input). The password bound sits far
 * above any real credential: it exists only to stop resource-exhaustion input, so
 * it constrains no legitimate user. It is <em>not</em> a password-policy change —
 * no minimum length and no character-class rules are introduced here, and the cap
 * applies identically whether or not an account exists, so it cannot be used to
 * enumerate accounts (a too-long password yields the same {@code validation-failed}
 * 400 for a known and an unknown email).</p>
 *
 * <p>For the email field the 254 cap is redundant in practice: Hibernate's
 * {@code @Email} already enforces the RFC 5321/5322 total-length limit, so any
 * address over 254 fails format validation as well. The explicit
 * {@code @Size(max = 254)} is kept as defence-in-depth (in case the {@code @Email}
 * implementation is ever relaxed) and to state the bound in the code itself; the
 * enforcing constraint in practice is {@code @Email}. Either way the outcome is
 * identical — a 400 {@code validation-failed} before any credential check.</p>
 *
 * <p>Unrecognised fields are ignored by the JSON mapper; unknown properties
 * never reach this record.</p>
 */
public record SignInRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.")
        @Size(max = 254, message = "Email must be 254 characters or fewer.")
        String email,
        @NotBlank(message = "Password is required.")
        @Size(max = 256, message = "Password must be 256 characters or fewer.")
        String password) {
}
