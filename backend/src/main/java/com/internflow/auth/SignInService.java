package com.internflow.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * Executes a sign-in attempt against Spring Security's standard authentication
 * manager and establishes the server-side session (IF-DEC-19).
 *
 * <p><strong>Credential source seam.</strong> Credentials are verified by the
 * framework's {@link AuthenticationManager}, which is backed by a
 * {@link UserDetailsService} (or a custom {@link AuthenticationProvider}) bean.
 * No such bean exists yet: the first database migration — and therefore any
 * persisted account — is blocked by open decisions OD-02 and OD-03 (FRD §9.1).
 * Until then {@link #attemptSignIn} answers {@link SignInOutcome#NOT_CONFIGURED}
 * so the API reports the true FRD §7.1 configuration-missing state instead of a
 * false "incorrect password". The tests supply a fixture credential source to
 * exercise the complete flow; production code contains no hardcoded accounts.</p>
 *
 * <p><strong>Session establishment and fixation.</strong> On success the
 * context is written through the same {@link SecurityContextRepository} the
 * filter chain reads from, which creates the server-side session and its
 * HttpOnly cookie (IF-DEC-19). Before that write, the session-fixation
 * strategy runs: if the request already presented a valid session (for example
 * an id an attacker planted on a victim's browser), its id is migrated — new
 * id, same state — so the pre-authentication id can never carry into the
 * authenticated session. Spring Security only applies its configured
 * {@code migrateSession} strategy from its own login machinery
 * ({@code AbstractAuthenticationProcessingFilter} and
 * {@code SessionManagementFilter}); a controller that authenticates directly
 * must invoke the same strategy itself, which this service does.</p>
 *
 * <p><strong>Failure cleanup.</strong> A failed attempt must not leave a
 * partially authenticated context behind. The thread-bound holder is cleared.
 * A session that already existed before the attempt is preserved — including a
 * user's existing signed-in session — and any security context this attempt
 * wrote into it is undone. Only a session created by the attempt itself is
 * invalidated. The successful path and session-fixation protection are
 * unaffected.</p>
 *
 * <p><strong>Logging.</strong> No password, credential material, session id,
 * or identity value is ever logged (NFR-SEC-008); only the outcome category and,
 * for technical failures, the exception's class name are recorded, at
 * debug/warn level. Exception messages and stack traces are deliberately not
 * logged: a credential-store exception can carry the submitted identity or
 * query details. Capturing authentication events in the audit trail is a
 * separate open decision (OD-06) and is deliberately not invented here.</p>
 */
@Service
public class SignInService {

    private static final Logger logger = LoggerFactory.getLogger(SignInService.class);

    private final AuthenticationManager authenticationManager;
    private final ObjectProvider<UserDetailsService> userDetailsService;
    private final ObjectProvider<AuthenticationProvider> authenticationProviders;
    private final SecurityContextRepository securityContextRepository;

    /**
     * Session-fixation strategy for the controller-based sign-in flow: the same
     * class the {@code sessionFixation(migrateSession())} DSL method installs
     * for framework login filters (see {@link SecurityConfig}), invoked here
     * because those filters — not a controller — are what normally apply it.
     */
    private final SessionAuthenticationStrategy sessionFixationStrategy =
            new ChangeSessionIdAuthenticationStrategy();

    public SignInService(
            AuthenticationManager authenticationManager,
            ObjectProvider<UserDetailsService> userDetailsService,
            ObjectProvider<AuthenticationProvider> authenticationProviders,
            SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationProviders = authenticationProviders;
        this.securityContextRepository = securityContextRepository;
    }

    /**
     * Verifies credentials and, on success, establishes an authenticated
     * session for the current request/response pair.
     *
     * @param email    login identity (email address, IF-DEC-19)
     * @param password candidate password, never stored or logged
     */
    public SignInOutcome attemptSignIn(String email, String password,
            HttpServletRequest request, HttpServletResponse response) {
        if (!credentialSourceConfigured()) {
            // FRD §7.1 configuration-missing: persistence (OD-02/OD-03) is unresolved.
            logger.debug("Sign-in attempt not processed: no credential source is configured");
            return SignInOutcome.NOT_CONFIGURED;
        }

        // Snapshot the request's pre-attempt state so a failure can restore it
        // instead of destroying a session that already existed (L-01). The
        // snapshot is taken before authentication and before the fixation
        // strategy migrates the session id.
        HttpSession sessionBeforeAttempt = null;
        SecurityContext contextBeforeAttempt = null;
        SecurityContext attemptedContext = null;
        try {
            sessionBeforeAttempt = request.getSession(false);
            contextBeforeAttempt = contextOf(sessionBeforeAttempt);

            Authentication authenticated =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            // Session fixation (IF-DEC-19): replace any session id that was
            // presented before authentication. No-op when the request has no
            // session yet — the normal case, since sign-in creates it below.
            sessionFixationStrategy.onAuthentication(authenticated, request, response);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticated);
            SecurityContextHolder.setContext(context);
            attemptedContext = context;
            // Explicit save: creates the server-side session and its cookie (IF-DEC-19).
            securityContextRepository.saveContext(context, request, response);
            logger.debug("Sign-in succeeded; session established");
            return SignInOutcome.AUTHENTICATED;
        } catch (InternalAuthenticationServiceException ex) {
            // Credential lookup failed inside the environment (e.g. data source
            // unavailable) — not a credential mismatch. Detail stays out of the
            // response; only the exception type is logged at debug level because
            // a message or stack trace can carry identity or query details.
            abandonSignInAttempt(request, sessionBeforeAttempt, contextBeforeAttempt, attemptedContext);
            logger.warn("Sign-in could not be completed (internal authentication error)");
            logger.debug("Internal authentication error type: {}", ex.getClass().getName());
            return SignInOutcome.TECHNICAL_FAILURE;
        } catch (AuthenticationException ex) {
            // Bad/unknown credentials, or an account not permitted to sign in.
            // The exception itself is intentionally not logged: its message can
            // carry identity details, and FR-AUTH-006 requires one generic outcome.
            abandonSignInAttempt(request, sessionBeforeAttempt, contextBeforeAttempt, attemptedContext);
            logger.debug("Sign-in attempt rejected: credentials did not match");
            return SignInOutcome.REJECTED;
        } catch (Exception ex) {
            abandonSignInAttempt(request, sessionBeforeAttempt, contextBeforeAttempt, attemptedContext);
            logger.warn("Sign-in could not be completed (unexpected error)");
            logger.debug("Unexpected sign-in error type: {}", ex.getClass().getName());
            return SignInOutcome.TECHNICAL_FAILURE;
        }
    }

    /**
     * Undoes only the state this sign-in attempt created or changed, so an
     * unsuccessful attempt can never end up half-authenticated:
     *
     * <ul>
     *   <li>clears the thread-bound {@link SecurityContextHolder} context — the
     *       fix for review finding L-01: {@link #attemptSignIn} populates the
     *       holder <em>before</em> persisting, so an exception thrown by
     *       session-fixation migration or by
     *       {@link SecurityContextRepository#saveContext} would otherwise leave
     *       an authenticated identity visible to the rest of this request even
     *       though no session was stored;</li>
     *   <li>preserves a session that already existed before the attempt,
     *       including a user's existing signed-in session. If this attempt
     *       wrote its new context into that session before failing, the previous
     *       context is restored (or the attribute removed when there was none),
     *       so the failed attempt cannot displace the existing authentication;</li>
     *   <li>invalidates a session created by the attempt itself (an
     *       {@code IllegalStateException} means it was already invalidated), so
     *       a session identifier minted during the failed attempt cannot
     *       authenticate a later request.</li>
     * </ul>
     *
     * <p>The container may already have sent an attempt-created session id to
     * the client; invalidation makes that id unusable, but this helper does not
     * delete browser cookies — it has no {@link HttpServletResponse} with which
     * to do so. The successful path is untouched, and session-fixation
     * protection is not weakened: this only runs when the attempt did not
     * complete.</p>
     */
    private void abandonSignInAttempt(HttpServletRequest request, HttpSession sessionBeforeAttempt,
            SecurityContext contextBeforeAttempt, SecurityContext attemptedContext) {
        SecurityContextHolder.clearContext();

        HttpSession session;
        try {
            session = request.getSession(false);
        } catch (IllegalStateException ex) {
            // The request/session is already invalid; there is nothing to undo.
            return;
        }
        if (session == null) {
            return;
        }

        if (session != sessionBeforeAttempt) {
            // The attempt created this session; it must not outlive the failure.
            invalidateQuietly(session);
            return;
        }

        if (attemptedContext == null) {
            // Authentication failed before this attempt wrote anything: the
            // existing session and its context are untouched.
            return;
        }

        try {
            Object current = session.getAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            if (current != attemptedContext) {
                // saveContext failed before writing, or a concurrent request
                // changed the attribute; leave that state alone.
                return;
            }
            if (contextBeforeAttempt != null) {
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        contextBeforeAttempt);
            } else {
                session.removeAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            }
        } catch (IllegalStateException ex) {
            // Session became invalid concurrently — nothing further to undo.
        }
    }

    /** Reads the security context already stored in a session, if any. */
    private static SecurityContext contextOf(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            Object attribute = session.getAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            return attribute instanceof SecurityContext context ? context : null;
        } catch (IllegalStateException ex) {
            // Session invalidated concurrently: treat it as absent.
            return null;
        }
    }

    private static void invalidateQuietly(HttpSession session) {
        try {
            session.invalidate();
        } catch (IllegalStateException ex) {
            // Already invalidated — nothing further to undo.
        }
    }

    /**
     * True when the environment can verify credentials at all: a
     * {@link UserDetailsService} or a custom {@link AuthenticationProvider}
     * bean is present. Checked before the manager is invoked so production
     * (which has neither) reports configuration-missing rather than failing
     * inside an empty provider list.
     */
    private boolean credentialSourceConfigured() {
        return userDetailsService.getIfAvailable() != null
                || authenticationProviders.stream().findFirst().isPresent();
    }
}
