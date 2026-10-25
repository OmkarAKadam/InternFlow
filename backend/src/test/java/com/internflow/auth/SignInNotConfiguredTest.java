package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Production behaviour with NO credential source (this class deliberately does
 * not import {@link TestAuthFixtures}): account persistence is blocked by
 * OD-02/OD-03, so sign-in must report the explicit configuration-missing state
 * (FRD §7.1) — never a false "incorrect password", never a fake success, and
 * never a session. This is exactly the behaviour the deployed backend has
 * until the first database migration lands.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SignInNotConfiguredTest extends MockMvcSecurityTestSupport {

    @Test
    void signInProgressReportsConfigurationMissingAndCreatesNoSession() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MvcResult result = mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson("applicant@college.example", "some-password-1234")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("sign-in-not-configured"))
                .andReturn();

        assertThat(result.getRequest().getSession(false))
                .as("an unverifiable sign-in must not create a session")
                .isNull();
    }

    @Test
    void protectedPathsStillRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/does-not-exist")).andExpect(status().isUnauthorized());
    }
}
