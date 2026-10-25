package com.internflow.auth;

import java.time.Clock;
import java.time.Duration;

import jakarta.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import com.internflow.shared.health.HealthController;

/**
 * Spring Security configuration implementing the approved authentication
 * package (IF-DEC-19, which closed OD-15) for Stage 2.3.
 *
 * <p>What is implemented here, and why:</p>
 * <ul>
 *   <li><strong>Server-managed sessions</strong> — {@code IF_REQUIRED} (never
 *       {@code STATELESS}); session state lives server-side and the browser
 *       holds only the container session cookie (IF-DEC-19).</li>
 *   <li><strong>Session cookie</strong> — {@code HttpOnly} always, {@code Secure}
 *       and {@code SameSite} configured in {@code application.yml}. The
 *       {@code SameSite} value is <em>provisional</em>: the final value depends
 *       on the deployment topology, which is still open (IF-DEC-10).</li>
 *   <li><strong>CSRF protection enabled</strong> (IF-DEC-19) with a
 *       cookie-based token repository so an API client can read the token
 *       ({@code XSRF-TOKEN}) and echo it in the {@code X-XSRF-TOKEN} header —
 *       the header name Spring Security's {@code CookieCsrfTokenRepository}
 *       uses by default (the same convention as Angular and axios, so common
 *       API clients need no custom header configuration). The token is
 *       materialized on safe requests by {@link CsrfCookieFilter} so the cookie
 *       exists before the first state-changing request.</li>
 *   <li><strong>Session lifetimes</strong> — the 30-minute idle timeout is
 *       container-enforced ({@code server.servlet.session.timeout}); the
 *       8-hour absolute lifetime needs {@link AbsoluteSessionLifetimeFilter},
 *       because servlet containers only enforce inactivity (IF-DEC-19).</li>
 *   <li><strong>Session fixation</strong> — Spring Security's {@code migrateSession}
 *       strategy (the framework default, made explicit because IF-DEC-19 relies
 *       on session integrity).</li>
 *   <li><strong>Deny by default</strong> — only the health endpoint, the
 *       provisional sign-in/sign-out endpoints, and the error dispatch are
 *       public; everything else requires an authenticated session
 *       (FR-AUTH-003, SRS-IF-02, NFR-SEC-001).</li>
 *   <li><strong>401 vs 403</strong> — unauthenticated requests are answered
 *       with 401 via the entry point, while authenticated-but-forbidden and
 *       CSRF failures keep Spring Security's 403 (FR-AUTH-007 AC2; the
 *       Stage 2.2 report recorded that the previous blanket 403 had to be
 *       fixed in Stage 2.3).</li>
 *   <li><strong>Logout</strong> — POST-only, invalidates the server-side
 *       session and deletes the session cookie (FR-AUTH-002).</li>
 *   <li><strong>BCrypt</strong> password encoding (IF-DEC-19); the Spring
 *       default strength is used because no strength was specified by the
 *       approver.</li>
 * </ul>
 *
 * <p>Deliberately <strong>not</strong> implemented here, because the decisions
 * or dependencies are not ready (see {@code docs/reviews/STAGE-2.3-REVIEW.md}):</p>
 * <ul>
 *   <li>No JWT / bearer tokens (rejected by IF-DEC-19).</li>
 *   <li>No CORS configuration — cross-origin cookie behaviour waits for the
 *       deployment topology (IF-DEC-10); adding permissive CORS now would widen
 *       the attack surface without a decision.</li>
 *   <li>No failed-login rate limiting — numeric thresholds were not approved
 *       (IF-DEC-19 boundary) and permanent lockout is rejected. Nothing is
 *       claimed about rate limiting.</li>
 *   <li>No role-based endpoint rules — no role-bearing endpoints exist yet
 *       (Stage 3+); deny-by-default covers the protected surface today.</li>
 *   <li>The endpoint paths below are a <strong>provisional contract</strong>
 *       documented for review; no approved API Specification exists yet
 *       (SRS-IF-01, SRS-IF-04). The frontend is intentionally not connected.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Servlet session cookie name — the container default, not customized. */
    static final String SESSION_COOKIE_NAME = "JSESSIONID";

    /** Default cookie name written by Spring Security's {@link CookieCsrfTokenRepository}. */
    static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

    /**
     * Request header an API client must echo the CSRF token in — the default
     * header name of {@link CookieCsrfTokenRepository} (X-XSRF-TOKEN, the same
     * convention as Angular and axios). Not customized: the repository's own
     * default keeps the provisional contract aligned with the framework.
     */
    static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    /**
     * Security context repository shared by the filter chain and
     * {@link SignInService}, so sign-in saves the context into the same place
     * the chain reads it from (explicit-save mode is Spring Security's default).
     */
    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /** Cookie-based CSRF token repository: readable by the API client (IF-DEC-19). */
    @Bean
    CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    /**
     * BCrypt password hashing (IF-DEC-19). The strength parameter is left at
     * the library default because the approver specified the algorithm only.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the standard Spring Security authentication manager so
     * {@link SignInService} can verify credentials once a
     * {@code UserDetailsService} is backed by persisted accounts. With no
     * credential source present (the current state — persistence is blocked by
     * OD-02/OD-03) the manager has no providers and simply is never invoked:
     * the service answers with an explicit configuration-missing outcome
     * instead of a false "invalid credentials" result (FRD §7.1).
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CsrfTokenRepository csrfTokenRepository,
            @Value("${internflow.security.session.absolute-lifetime:PT8H}") Duration absoluteSessionLifetime,
            Clock clock) throws Exception {
        http
            .securityContext(context -> context.securityContextRepository(securityContextRepository))
            .csrf(csrf -> csrf
                // CSRF stays enabled (IF-DEC-19): every non-safe method requires a token.
                .csrfTokenRepository(csrfTokenRepository)
                // Spring Security's default handler is XorCsrfTokenRequestAttributeHandler,
                // which expects a base64-masked token in the header — a scheme meant for
                // server-rendered forms. The double-submit pattern approved in IF-DEC-19
                // has the client read the raw XSRF-TOKEN cookie and echo it unchanged in
                // X-XSRF-TOKEN, so the plain handler (constant-time raw comparison) is the
                // documented pairing for CookieCsrfTokenRepository. Protection is not
                // weakened: a missing or altered token is still rejected with 403.
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .sessionManagement(session -> session
                // Server-managed sessions (IF-DEC-19). Never STATELESS.
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // Session fixation protection: new session id on authentication.
                .sessionFixation(fixation -> fixation.migrateSession()))
            .authorizeHttpRequests(auth -> auth
                // Error responses (404/500) must render even without a session,
                // otherwise an error dispatch would re-trigger authentication.
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HealthController.HEALTH_PATH).permitAll()
                // Provisional authentication endpoints (contract not yet approved).
                .requestMatchers(SignInController.SIGN_IN_PATH, SignInController.SIGN_OUT_PATH).permitAll()
                .anyRequest().authenticated())
            // 401 for unauthenticated requests, distinct from the 403 used for
            // authenticated-but-forbidden and CSRF failures (FR-AUTH-007 AC2).
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new UnauthenticatedEntryPoint()))
            .logout(logout -> logout
                // Provisional path; logout invalidates server-side state, not just
                // the cookie (IF-DEC-19 consequence; FR-AUTH-002).
                .logoutUrl(SignInController.SIGN_OUT_PATH)
                .invalidateHttpSession(true)
                .deleteCookies(SESSION_COOKIE_NAME)
                .logoutSuccessHandler((request, response, authentication) ->
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT)))
            // Forces the deferred CSRF token to materialize on safe requests so the
            // XSRF-TOKEN cookie is issued before any state-changing request.
            .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
            // Absolute lifetime runs before the security context is loaded from the
            // session, so an expired session cannot authenticate the request.
            .addFilterBefore(
                new AbsoluteSessionLifetimeFilter(absoluteSessionLifetime, clock),
                SecurityContextHolderFilter.class);
        return http.build();
    }
}
