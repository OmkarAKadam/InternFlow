package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Security fix L-01: sign-in populates the thread-bound
 * {@link SecurityContextHolder} <em>before</em> persisting the context, so a
 * failure in session-fixation migration or in
 * {@link SecurityContextRepository#saveContext} would — before the fix — leave
 * an authenticated identity visible to the rest of the request even though no
 * session was ever stored. The cleanup must also distinguish resources created
 * by the attempt from a session that existed beforehand:
 *
 * <ul>
 *   <li>a session created by the failed attempt is invalidated;</li>
 *   <li>a pre-existing session — including a user's existing signed-in
 *       session — is preserved, and any context the failed attempt wrote into
 *       it is replaced by the previous context.</li>
 * </ul>
 *
 * <p>The fixture below makes persistence fail deterministically after
 * credentials have been successfully authenticated and after the new context
 * has been written into the session — the partial-persistence window the
 * finding describes. Reads still delegate to the real
 * {@link HttpSessionSecurityContextRepository}, so the rest of the chain
 * behaves normally.</p>
 *
 * <p>Honest scope note: the MockMvc test proves the <em>endpoint contract</em>
 * (500 technical-failure, no authenticated session, follow-up requests
 * unauthorized). The thread-bound holder is cleared by the framework's
 * {@code SecurityContextHolderFilter} when the request ends, so the
 * discriminating regression tests are the direct service calls, which observe
 * the holder and the session inside the service itself — they fail before the
 * fix and pass after it.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import({TestAuthFixtures.class, SignInPersistenceFailureTest.FailingContextPersistence.class})
class SignInPersistenceFailureTest extends MockMvcSecurityTestSupport {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ObjectProvider<UserDetailsService> userDetailsService;

    @Autowired
    ObjectProvider<AuthenticationProvider> authenticationProviders;

    /** The {@code @Primary} fixture: reads delegate, saves write then fail. */
    @Autowired
    SecurityContextRepository securityContextRepository;

    @Test
    void persistenceFailureAfterAuthenticationAnswersTechnicalFailureAndDiscardsTheSessionItCreated() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        // No session is presented: the failing saveContext creates one before
        // throwing, so this exercises the attempt-created-session cleanup path.
        MvcResult result = mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL,
                                TestAuthFixtures.FIXTURE_PASSWORD)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("technical-failure"))
                .andReturn();

        // No authenticated context survives on the request's thread...
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .as("a failed sign-in must not leave an authenticated context on the thread")
                .isNull();

        // ...and the session minted by the failed attempt is discarded.
        assertThat(result.getRequest().getSession(false))
                .as("the session created by the failed attempt must be invalidated")
                .isNull();
        mockMvc.perform(get(TestProtectedController.PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void failedCompletionClearsTheHolderAndDiscardsTheAttemptCreatedSession() {
        // Direct service call: this observes the holder and request session
        // immediately after the failed attempt, before framework cleanup can
        // mask the outcome.
        SignInService service = new SignInService(
                authenticationManager, userDetailsService, authenticationProviders,
                securityContextRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();

        SignInOutcome outcome = service.attemptSignIn(
                TestAuthFixtures.FIXTURE_EMAIL, TestAuthFixtures.FIXTURE_PASSWORD,
                request, response);

        assertThat(outcome).isEqualTo(SignInOutcome.TECHNICAL_FAILURE);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .as("L-01 regression: the holder must be cleared when persistence fails")
                .isNull();
        assertThat(request.getSession(false))
                .as("a failed attempt must not leave its own session behind")
                .isNull();
    }

    @Test
    void persistenceFailurePreservesAPreExistingSessionAndRestoresItsPreviousContext() {
        // A pre-existing authenticated session must survive a failed attempt,
        // and the new context written by the failed save must not displace it.
        SignInService service = new SignInService(
                authenticationManager, userDetailsService, authenticationProviders,
                securityContextRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();

        SecurityContext existingContext = SecurityContextHolder.createEmptyContext();
        existingContext.setAuthentication(
                new UsernamePasswordAuthenticationToken("existing-user", null, List.of()));
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, existingContext);
        request.setSession(session);
        SecurityContextHolder.clearContext();

        SignInOutcome outcome = service.attemptSignIn(
                TestAuthFixtures.FIXTURE_EMAIL, TestAuthFixtures.FIXTURE_PASSWORD,
                request, response);

        assertThat(outcome).isEqualTo(SignInOutcome.TECHNICAL_FAILURE);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .as("the thread-bound holder must still be cleared on failure")
                .isNull();
        assertThat(request.getSession(false))
                .as("a pre-existing session must not be destroyed by a failed attempt")
                .isSameAs(session);
        assertThat(session.isInvalid()).isFalse();
        assertThat(session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
                .as("the failed attempt's context must not survive in a pre-existing session")
                .isSameAs(existingContext);
    }

    /**
     * TEST-ONLY persistence failure: credentials would authenticate, the new
     * context is written to the session, and then saving reports failure — the
     * way a session store could fail after accepting a write.
     */
    @TestConfiguration(proxyBeanMethods = false)
    static class FailingContextPersistence {

        @Bean
        @Primary
        SecurityContextRepository failingSecurityContextRepository() {
            return new SecurityContextRepository() {

                private final SecurityContextRepository delegate =
                        new HttpSessionSecurityContextRepository();

                @Override
                @SuppressWarnings("deprecation") // required: still the interface's abstract read method
                public SecurityContext loadContext(HttpRequestResponseHolder holder) {
                    return delegate.loadContext(holder);
                }

                @Override
                public void saveContext(SecurityContext context, HttpServletRequest request,
                        HttpServletResponse response) {
                    // Partial persistence: create/reuse the session and write the
                    // attempted context, then fail.
                    request.getSession(true).setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            context);
                    throw new IllegalStateException(
                            "simulated security-context persistence failure (test fixture)");
                }

                @Override
                public boolean containsContext(HttpServletRequest request) {
                    return delegate.containsContext(request);
                }
            };
        }
    }
}
