package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import com.internflow.shared.health.HealthController;

/**
 * Sign-in / sign-out behaviour of the provisional endpoints (contract
 * documented in {@code docs/reviews/STAGE-2.3-REVIEW.md} §8) with a fixture
 * credential source from {@link TestAuthFixtures}. The frontend is not
 * connected to these endpoints in this substage.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestAuthFixtures.class)
class SignInEndpointTest extends MockMvcSecurityTestSupport {

    @Test
    void healthEndpointIsPublicAndIssuesTheCsrfCookie() throws Exception {
        MvcResult result = mockMvc.perform(get(HealthController.HEALTH_PATH))
                .andExpect(status().isOk())
                .andReturn();
        List<String> setCookies = result.getResponse().getHeaders("Set-Cookie");
        assertThat(TestCookies.value(setCookies, SecurityConfig.CSRF_COOKIE_NAME))
                .as("safe requests must materialize the XSRF-TOKEN cookie")
                .isNotBlank();
    }

    @Test
    void unknownPathsRequireAuthentication() throws Exception {
        // Deny by default: paths that do not even exist still answer 401 for
        // anonymous callers, not 404 (FR-AUTH-003, NFR-SEC-001).
        mockMvc.perform(get("/api/does-not-exist")).andExpect(status().isUnauthorized());
    }

    @Test
    void missingEmailIsAValidationFailureNotAnAuthenticationOutcome() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content("{\"password\":\"some-password-1234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email is required."));
    }

    @Test
    void malformedEmailIsRejectedAtTheBoundary() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson("not-an-email", "some-password-1234")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Enter a valid email address."));
    }

    @Test
    void missingPasswordIsAValidationFailure() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content("{\"email\":\"student@college.example\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").value("Password is required."));
    }

    @Test
    void stateChangingRequestsWithoutCsrfTokenAreRejectedBeforeAuthentication() throws Exception {
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL,
                                TestAuthFixtures.FIXTURE_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void signOutAlsoRequiresTheCsrfToken() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MockHttpSession session = signInExpectingSession(csrf);

        mockMvc.perform(post(SignInController.SIGN_OUT_PATH).session(session))
                .andExpect(status().isForbidden());

        // The logout did not happen: the session still authenticates.
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isOk());
    }

    @Test
    void unknownAccountAndPasswordMismatchAreIndistinguishable() throws Exception {
        Cookie csrf = fetchCsrfCookie();

        String unknownAccount = mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson("nobody@college.example", "some-password-1234")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("sign-in-failed"))
                .andExpect(jsonPath("$.message")
                        .value(SignInController.GENERIC_SIGN_IN_FAILURE_MESSAGE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String wrongPassword = mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL, "wrong-password-1234")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("sign-in-failed"))
                .andExpect(jsonPath("$.message")
                        .value(SignInController.GENERIC_SIGN_IN_FAILURE_MESSAGE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // FR-AUTH-006 AC1: one generic message; the response cannot be used to
        // distinguish "no such account" from "wrong password".
        assertThat(wrongPassword).isEqualTo(unknownAccount);
    }

    @Test
    void failedSigninCreatesNoSession() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MvcResult result = mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL, "wrong-password-1234")))
                .andExpect(status().isUnauthorized())
                .andReturn();
        assertThat(result.getRequest().getSession(false))
                .as("a failed sign-in must not create a session")
                .isNull();
    }

    @Test
    void failedSignInDoesNotDestroyAnExistingAuthenticatedSession() throws Exception {
        // L-01 regression: a signed-in user who submits a bad-password sign-in
        // must not be logged out by the failure cleanup.
        Cookie csrf = fetchCsrfCookie();
        MockHttpSession session = signInExpectingSession(csrf);

        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL, "wrong-password-1234")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("sign-in-failed"));

        // The pre-existing signed-in session must survive the failed attempt.
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isOk());
    }

    @Test
    void validCredentialsEstablishASessionThatLaterRequestsReuse() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MockHttpSession session = signInExpectingSession(csrf);

        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"));
    }

    @Test
    void signOutInvalidatesTheServerSideSession() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MockHttpSession session = signInExpectingSession(csrf);

        mockMvc.perform(post(SignInController.SIGN_OUT_PATH)
                        .session(session)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue()))
                .andExpect(status().isNoContent());

        // FR-AUTH-002: server-side state is gone — the same id must not
        // authenticate anything any more.
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isUnauthorized());
    }

    // ---- Password / email length bounds (security fix L-02) ----

    @Test
    void oversizedPasswordIsAValidationFailureNotAAuthenticationAttempt() throws Exception {
        // 257 characters is one past the documented bound, so it is rejected at
        // the validation boundary and never reaches BCrypt (which does fixed work
        // on arbitrarily long input).
        Cookie csrf = fetchCsrfCookie();
        String oversized = "x".repeat(257);
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(TestAuthFixtures.FIXTURE_EMAIL, oversized)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andExpect(jsonPath("$.fieldErrors.password")
                        .value("Password must be 256 characters or fewer."));
    }

    @Test
    void maximumLengthPasswordStillReachesCredentialVerification() throws Exception {
        // The 256-character bound is inclusive: a password of exactly this length
        // is a well-formed request and proceeds to credential verification (which
        // fails, generically, because the fixture account's password is shorter).
        Cookie csrf = fetchCsrfCookie();
        String atBound = "x".repeat(256);
        signIn(TestAuthFixtures.FIXTURE_EMAIL, atBound, csrf)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("sign-in-failed"))
                .andExpect(jsonPath("$.message")
                        .value(SignInController.GENERIC_SIGN_IN_FAILURE_MESSAGE));
    }

    @Test
    void oversizedPasswordFailsIdenticallyForKnownAndUnknownAccounts() throws Exception {
        // The length bound must not become an enumeration oracle: it is applied
        // before any account lookup, so a too-long password yields the same
        // validation-failed body whether or not the account exists.
        Cookie csrf = fetchCsrfCookie();
        String oversized = "y".repeat(300);

        String known = signIn(TestAuthFixtures.FIXTURE_EMAIL, oversized, csrf)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andReturn().getResponse().getContentAsString();

        String unknown = signIn("nobody@college.example", oversized, csrf)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andReturn().getResponse().getContentAsString();

        assertThat(unknown)
                .as("an oversized password must produce identical errors for existing and unknown accounts")
                .isEqualTo(known);
    }

    @Test
    void oversizedEmailIsAValidationFailure() throws Exception {
        // A 255-character address is rejected at the validation boundary. Note the
        // email bound is unreachable in isolation: Hibernate's @Email also enforces
        // the RFC 5321 total-length cap, so any address over 254 fails @Email too.
        // Whichever constraint is reported, the outcome is the same — a 400
        // validation-failed, never a credential-verification pass-through.
        Cookie csrf = fetchCsrfCookie();
        String oversized = "a".repeat(142) + "." + "b".repeat(96) + "@college.example";
        assertThat(oversized.length()).as("test fixture must be exactly 255 characters").isEqualTo(255);
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson(oversized, "some-password-1234")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation-failed"))
                .andExpect(jsonPath("$.fieldErrors.email").isNotEmpty());
    }

    @Test
    void shortWellFormedEmailIsNotRejectedForLength() throws Exception {
        // A normal-length, well-formed address is not reported as a length or
        // format failure; it proceeds to credential verification and fails as an
        // unknown account with the single generic 401 message. This pins the
        // negative case: the length bound above does not over-trigger on
        // legitimate input.
        Cookie csrf = fetchCsrfCookie();
        signIn("someone@college.example", "some-password-1234", csrf)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("sign-in-failed"))
                .andExpect(jsonPath("$.message")
                        .value(SignInController.GENERIC_SIGN_IN_FAILURE_MESSAGE));
    }

    private MockHttpSession signInExpectingSession(Cookie csrf) throws Exception {
        MvcResult result = signIn(TestAuthFixtures.FIXTURE_EMAIL, TestAuthFixtures.FIXTURE_PASSWORD, csrf)
                .andExpect(status().isNoContent())
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).as("successful sign-in must establish a server-side session").isNotNull();
        return session;
    }
}
