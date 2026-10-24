package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;

/**
 * FRD §7.1 technical failure: when the credential source itself fails (the way
 * an unavailable account store does), sign-in must answer
 * <strong>500 technical-failure</strong> — never the 401 generic credential
 * message, so a broken environment is never misreported as a wrong password
 * (FR-AUTH-006 AC1 in spirit, and FRD §7.1's failure taxonomy).
 *
 * <p>The fixture below makes the credential lookup fail deterministically; no
 * timing, network, or environment dependency is involved, and the only beans
 * added are test-only. Its exception message deliberately contains the
 * submitted email, which lets the test prove that technical-failure logging
 * records only the exception <em>type</em> — never the message, stack trace, or
 * identity (debug-logging fix in the security-fix pass).</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "logging.level.com.internflow.auth.SignInService=DEBUG")
@ExtendWith(OutputCaptureExtension.class)
@Import(SignInTechnicalFailureTest.BrokenCredentialSource.class)
class SignInTechnicalFailureTest extends MockMvcSecurityTestSupport {

    @Test
    void credentialSourceFailureIsReportedAsTechnicalFailureNotCredentialMismatch(CapturedOutput output)
            throws Exception {
        Cookie csrf = fetchCsrfCookie();
        mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(csrf)
                        .header(SecurityConfig.CSRF_HEADER_NAME, csrf.getValue())
                        .content(signInJson("applicant@college.example", "some-password-1234")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("technical-failure"))
                // The message must not resemble the credential-failure wording.
                .andExpect(jsonPath("$.message").value(
                        "Sign-in could not be completed right now."));

        // Proves the DEBUG line was actually captured: without this, the
        // absence assertions below could pass vacuously if log capture or the
        // configured level were broken.
        assertThat(output.getAll())
                .as("the technical-failure debug log must be captured")
                .contains("Internal authentication error type:");

        assertThat(output.getAll())
                .as("technical-failure logging must not include the submitted identity")
                .doesNotContain("applicant@college.example");
        assertThat(output.getAll())
                .as("technical-failure logging must not include the exception message or stack trace")
                .doesNotContain("credential store unavailable");
    }

    /** TEST-ONLY credential source that fails the way an unavailable store does. */
    @TestConfiguration(proxyBeanMethods = false)
    static class BrokenCredentialSource {

        @Bean
        @Primary
        UserDetailsService brokenUserDetailsService() {
            return username -> {
                // Deliberately identity-bearing: the service must not log this
                // message or its stack trace on the technical-failure path.
                throw new IllegalStateException(
                        "credential store unavailable for " + username + " (test fixture)");
            };
        }
    }
}
