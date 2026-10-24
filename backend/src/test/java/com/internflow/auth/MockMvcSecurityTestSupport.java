package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.internflow.shared.health.HealthController;

/**
 * Shared helpers for the MockMvc-based security tests. CSRF tokens are obtained
 * the way a real API client must obtain them — read the {@code XSRF-TOKEN}
 * cookie from a safe request and echo it in the {@code X-XSRF-TOKEN} header —
 * with no {@code spring-security-test} shortcut, so the tests exercise the
 * same flow the frontend will eventually use.
 */
abstract class MockMvcSecurityTestSupport {

    @Autowired
    MockMvc mockMvc;

    /** Fetches the CSRF cookie issued on a safe request, as a client would. */
    Cookie fetchCsrfCookie() throws Exception {
        MvcResult result = mockMvc.perform(get(HealthController.HEALTH_PATH)).andReturn();
        List<String> setCookies = result.getResponse().getHeaders("Set-Cookie");
        String token = TestCookies.value(setCookies, SecurityConfig.CSRF_COOKIE_NAME);
        assertThat(token).as("XSRF-TOKEN cookie must be issued on safe requests").isNotBlank();
        return new Cookie(SecurityConfig.CSRF_COOKIE_NAME, token);
    }

    /** Signs in with the given credentials, sending the CSRF cookie and header. */
    ResultActions signIn(String email, String password, Cookie csrfCookie) throws Exception {
        return mockMvc.perform(post(SignInController.SIGN_IN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(csrfCookie)
                .header(SecurityConfig.CSRF_HEADER_NAME, csrfCookie.getValue())
                .content(signInJson(email, password)));
    }

    static String signInJson(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }
}
