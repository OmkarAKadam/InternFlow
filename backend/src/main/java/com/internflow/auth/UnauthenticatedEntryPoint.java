package com.internflow.auth;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Answers unauthenticated requests to protected resources with <strong>401</strong>.
 *
 * <p>Spring Security's fallback entry point answers 403, which would make an
 * unauthenticated request indistinguishable from an authorized-but-forbidden
 * one. FR-AUTH-007 AC2 (as recorded in the Stage 2.2 review) requires the two
 * to be observably different: 401 when no valid session exists, 403 when the
 * requester is authenticated but not permitted (or a CSRF token is
 * missing/invalid, which Spring Security handles with its own 403 handler).</p>
 *
 * <p>The error body is produced by the container error dispatch
 * ({@code DispatcherType.ERROR} is permitted in {@link SecurityConfig}), which
 * renders the standard JSON error payload — no authentication detail, session
 * identifier, or stack trace is written here (NFR-SEC-003, NFR-SEC-008).</p>
 */
final class UnauthenticatedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        // The exception itself is deliberately not echoed to the client.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
