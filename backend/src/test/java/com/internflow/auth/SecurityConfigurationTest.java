package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Approved configuration values (IF-DEC-19, closing OD-15) asserted against
 * the running application context — the values that will be handed to the
 * servlet container, not just text in a YAML file.
 *
 * <p>The 30-minute idle timeout is enforced by the servlet container itself,
 * so this test asserts the binding chain up to the container-facing
 * configuration; {@link SessionCookieIntegrationTest} additionally asserts the
 * value that actually reached Tomcat. A real-time wait through any idle window
 * is deliberately not part of the suite: Boot converts the timeout to whole
 * minutes for Tomcat (verified from the Boot bytecode), so the shortest
 * behaviourally testable window would be 60 seconds of wall time. The lifetime
 * this project implements itself — the 8-hour absolute cap — is
 * behaviour-tested at its boundary in
 * {@link AbsoluteSessionLifetimeEnforcementTest}.</p>
 */
@SpringBootTest
class SecurityConfigurationTest {

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void idleTimeoutIsConfiguredToThirtyMinutes() {
        Session session = serverProperties.getServlet().getSession();
        assertThat(session.getTimeout()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void sessionCookieIsHttpOnlyWithProvisionalSameSiteAndLocalSecureDefault() {
        Cookie cookie = serverProperties.getServlet().getSession().getCookie();
        // IF-DEC-19: HttpOnly always — the cookie must be unreadable from JS.
        assertThat(cookie.getHttpOnly()).isTrue();
        // PROVISIONAL (documented in docs/reviews/STAGE-2.3-REVIEW.md):
        // - Secure defaults to false for local plain-HTTP development only; a
        //   TLS deployment MUST set INTERNFLOW_COOKIE_SECURE=true.
        // - SameSite=Lax is a provisional value pending the IF-DEC-10
        //   topology decision; it is not an approved final value.
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.getSameSite()).isEqualTo(Cookie.SameSite.LAX);
    }

    @Test
    void passwordEncoderBeanIsBcrypt() {
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
