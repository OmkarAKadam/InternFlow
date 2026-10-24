package com.internflow.auth;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Enforces the <strong>8-hour absolute session lifetime</strong> approved in
 * IF-DEC-19 (closing OD-15).
 *
 * <p>Servlet containers only enforce an <em>idle</em> timeout (the container's
 * {@code session.timeout}); they do not cap total session age, and a session
 * that is used continuously would otherwise live forever. This filter compares
 * the session's creation time against the configured absolute lifetime and
 * invalidates sessions that exceed it, so the request continues unauthenticated
 * and is answered with 401 by the entry point.</p>
 *
 * <p>It runs <em>before</em> the security context is read from the session
 * (registered ahead of {@code SecurityContextHolderFilter}), which guarantees an
 * over-age session cannot authenticate the request and cannot be re-saved.</p>
 *
 * <p>Time is read from the injected {@link Clock} (SAD §5.14 cross-cutting
 * concern) rather than from static factories, so the boundary behaviour is
 * tested deterministically without shortening any production setting.</p>
 *
 * <p>The lifetime value comes from
 * {@code internflow.security.session.absolute-lifetime} (default {@code PT8H}),
 * which is approved configuration, not a test hook.</p>
 */
final class AbsoluteSessionLifetimeFilter extends OncePerRequestFilter {

    private final Duration absoluteLifetime;
    private final Clock clock;

    AbsoluteSessionLifetimeFilter(Duration absoluteLifetime, Clock clock) {
        this.absoluteLifetime = absoluteLifetime;
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && isExpired(session)) {
            try {
                session.invalidate();
            } catch (IllegalStateException ex) {
                // Already invalidated concurrently — treat as absent, not an error.
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isExpired(HttpSession session) {
        try {
            long ageMillis = clock.millis() - session.getCreationTime();
            return ageMillis >= absoluteLifetime.toMillis();
        } catch (IllegalStateException ex) {
            // Session became invalid while being inspected: it authenticates nothing.
            return false;
        }
    }
}
