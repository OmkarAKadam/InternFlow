"use client";

import { useRef, useState, type FormEvent } from "react";

import { Alert } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/ui/form-field";
import { LoadingIndicator } from "@/components/ui/loading-indicator";
import { attemptSignIn } from "@/lib/auth/sign-in";

/**
 * Sign-in form (Stage 2.2).
 *
 * Behavior contract:
 * - Client-side validation is advisory only (NFR-SEC-005, SRS-IF-03): it
 *   gives immediate feedback; the server remains the authority.
 * - Authentication itself is delegated to `attemptSignIn`, which currently
 *   performs no request because no API contract exists. The form therefore
 *   shows an honest "not connected yet" notice instead of pretending success.
 * - A single generic message covers authentication failure (FR-AUTH-001,
 *   FR-AUTH-006 AC1). Nothing here reveals account existence, activation
 *   state, or rate-limit status.
 * - Passwords are held only in component state for the lifetime of the form
 *   and are never persisted (no localStorage/sessionStorage) or logged.
 */

// Generic failure copy — identical for unknown account, wrong password, and
// deactivated account, so responses cannot be used to enumerate accounts
// (FR-AUTH-006 AC1). Do not make this specific per cause.
const GENERIC_SIGN_IN_FAILURE =
  "Incorrect email or password. Please try again.";

const INTEGRATION_PENDING_NOTICE =
  "Sign-in is not connected to the backend yet. The authentication service " +
  "is not available in this environment, so no request was sent and you " +
  "cannot sign in. This is expected until the API is connected.";

interface FieldErrors {
  email?: string;
  password?: string;
}

// Deliberately permissive: one @, a non-empty local part, and a domain with
// a dot (FR-USER-005 requires uniqueness, not a stricter format; the server
// validates authoritatively).
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function validateFieldValues(email: string, password: string): FieldErrors {
  const errors: FieldErrors = {};
  if (!email.trim()) {
    errors.email = "Email is required.";
  } else if (!EMAIL_PATTERN.test(email.trim())) {
    errors.email = "Enter a valid email address.";
  }
  if (!password) {
    errors.password = "Password is required.";
  }
  return errors;
}

export function SignInForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  // Top-level message: generic auth failure (alert) or info notice (status).
  const [formMessage, setFormMessage] = useState<
    { tone: "error" | "info"; text: string } | null
  >(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const emailInputRef = useRef<HTMLInputElement>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    // Guard: never allow a second submission while one is in flight.
    if (isSubmitting) return;

    const errors = validateFieldValues(email, password);
    setFieldErrors(errors);
    if (errors.email || errors.password) {
      setFormMessage(null);
      // Move focus to the first invalid field so keyboard and screen-reader
      // users land on the problem (announced via aria-invalid + describedby).
      if (errors.email) {
        emailInputRef.current?.focus();
      }
      return;
    }

    setFormMessage(null);
    setIsSubmitting(true);
    try {
      const outcome = await attemptSignIn({
        email: email.trim(),
        password,
      });

      switch (outcome.status) {
        case "authenticated":
          // Not reachable in Stage 2.2 (attemptSignIn cannot produce it).
          // When the API is connected, navigate to the role landing surface
          // (FR-AUTH-001 AC1) instead of setting a message.
          setFormMessage({
            tone: "info",
            text: "Signed in. Redirecting is not implemented yet.",
          });
          break;
        case "rejected":
          setFormMessage({ tone: "error", text: GENERIC_SIGN_IN_FAILURE });
          break;
        case "failed":
          setFormMessage({
            tone: "error",
            text: "Something went wrong. Please try again.",
          });
          break;
        case "integration-pending":
          setFormMessage({ tone: "info", text: INTEGRATION_PENDING_NOTICE });
          break;
      }
    } catch {
      // Defensive: attemptSignIn is not expected to throw. A thrown error is
      // treated as a technical failure with a generic message — no stack
      // traces or internal details are ever shown to the user.
      setFormMessage({
        tone: "error",
        text: "Something went wrong. Please try again.",
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-5">
      <FormField
        id="email"
        ref={emailInputRef}
        label="Email"
        type="email"
        name="email"
        autoComplete="email"
        inputMode="email"
        required
        value={email}
        onChange={(event) => {
          setEmail(event.target.value);
          // Clear the stale error as soon as the user edits the field.
          if (fieldErrors.email) {
            setFieldErrors((previous) => ({ ...previous, email: undefined }));
          }
        }}
        error={fieldErrors.email}
      />

      <FormField
        id="password"
        label="Password"
        type="password"
        name="password"
        autoComplete="current-password"
        required
        value={password}
        onChange={(event) => {
          setPassword(event.target.value);
          if (fieldErrors.password) {
            setFieldErrors((previous) => ({
              ...previous,
              password: undefined,
            }));
          }
        }}
        error={fieldErrors.password}
      />

      {formMessage ? (
        <Alert tone={formMessage.tone}>{formMessage.text}</Alert>
      ) : null}

      <Button type="submit" disabled={isSubmitting} className="w-full">
        {isSubmitting ? "Signing in…" : "Sign in"}
      </Button>

      {/* Persistent polite live region so the loading state is announced to
          assistive technology. The region must exist in the DOM before its
          text changes; an empty status region announces nothing until then. */}
      <p role="status" aria-live="polite" className="sr-only">
        {isSubmitting ? "Signing in, please wait…" : ""}
      </p>
      {isSubmitting ? (
        <LoadingIndicator label="Signing in, please wait…" className="justify-center" />
      ) : null}
    </form>
  );
}
