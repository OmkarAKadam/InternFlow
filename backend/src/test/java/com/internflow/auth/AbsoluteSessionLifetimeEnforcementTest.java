package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

/**
 * The 8-hour absolute session lifetime (IF-DEC-19, closing OD-15) at its exact
 * boundary, driven through the injected {@code Clock}: no real hours elapse and
 * no production setting is shortened. The explicit property below matches the
 * production default but gives this class its own application context, so the
 * advanced test clock can never leak into other tests sharing a cached context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestAuthFixtures.class)
@TestPropertySource(properties = "internflow.security.session.absolute-lifetime=PT8H")
class AbsoluteSessionLifetimeEnforcementTest extends MockMvcSecurityTestSupport {

    @Autowired
    private MutableClock clock;

    @BeforeEach
    void resetClock() {
        // Each method starts from real time; session creation times come from
        // the container, so age is measured against the reset clock.
        clock.reset();
    }

    @Test
    void sessionsStopAuthenticatingOnceTheyAreEightHoursOld() throws Exception {
        Cookie csrf = fetchCsrfCookie();
        MvcResult signIn = signIn(TestAuthFixtures.FIXTURE_EMAIL, TestAuthFixtures.FIXTURE_PASSWORD, csrf)
                .andExpect(status().isNoContent())
                .andReturn();
        MockHttpSession session = (MockHttpSession) signIn.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // Just inside the limit: still authenticated.
        clock.advance(Duration.ofHours(7).plusMinutes(59));
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isOk());

        // Just past the limit: the session is invalidated and no longer
        // authenticates — the request is answered by the 401 entry point.
        clock.advance(Duration.ofMinutes(2));
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isUnauthorized());

        // And it stays gone: the over-age session is not resurrected.
        mockMvc.perform(get(TestProtectedController.PATH).session(session))
                .andExpect(status().isUnauthorized());
    }
}
