package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.catalina.core.StandardContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.internflow.shared.health.HealthController;

/**
 * Session and CSRF behaviour over a real embedded Tomcat (random port) with
 * manual cookie handling — what a browser-facing API actually sees: session
 * cookie flags, CSRF enforcement over HTTP, logout cookie deletion, the
 * container-enforced idle timeout, and session-fixation protection.
 *
 * <p>Cookies are managed manually and responses inspected as a browser would
 * see them. {@link TestRestTemplate} is used because it sends real HTTP and
 * returns 4xx/5xx responses instead of throwing, so exact status codes can be
 * asserted.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestAuthFixtures.class)
class SessionCookieIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ServletWebServerApplicationContext webServerContext;

    @Test
    void sessionCookieFlagsAndIdleTimeoutReachTheContainer() {
        String csrf = fetchCsrfToken();
        ResponseEntity<String> response = signIn(csrf, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        List<String> setCookies = setCookies(response);
        assertThat(TestCookies.value(setCookies, SecurityConfig.SESSION_COOKIE_NAME)).isNotBlank();
        // IF-DEC-19: HttpOnly always. PROVISIONAL: SameSite=Lax pending the
        // IF-DEC-10 topology decision; Secure is off only for local plain-HTTP
        // development and must be true on TLS deployments
        // (INTERNFLOW_COOKIE_SECURE=true).
        assertThat(TestCookies.headerContains(setCookies, SecurityConfig.SESSION_COOKIE_NAME, "HttpOnly"))
                .as("session cookie must be HttpOnly")
                .isTrue();
        assertThat(TestCookies.headerContains(setCookies, SecurityConfig.SESSION_COOKIE_NAME, "SameSite=Lax"))
                .as("provisional SameSite value must reach the container")
                .isTrue();
        assertThat(TestCookies.headerContains(setCookies, SecurityConfig.SESSION_COOKIE_NAME, "Secure"))
                .as("local development default must not mark the cookie Secure")
                .isFalse();

        // The 30-minute idle timeout from application.yml reached Tomcat
        // (Boot hands it over in whole minutes, Tomcat's unit).
        TomcatWebServer tomcat = (TomcatWebServer) webServerContext.getWebServer();
        StandardContext containerContext = (StandardContext) tomcat.getTomcat().getHost().findChildren()[0];
        assertThat(containerContext.getSessionTimeout())
                .as("the container must be configured with the 30-minute idle timeout")
                .isEqualTo(30);
    }

    @Test
    void loginSessionIsReusedAcrossRequestsAndLogoutRemovesIt() {
        String csrf = fetchCsrfToken();
        ResponseEntity<String> signIn = signIn(csrf, null);
        assertThat(signIn.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        String sessionId = sessionIdOf(signIn);
        assertThat(sessionId).isNotBlank();
        String sessionCookie = SecurityConfig.SESSION_COOKIE_NAME + "=" + sessionId;

        // The session lives server-side: a later request reuses the cookie.
        ResponseEntity<String> protectedResource =
                get(TestProtectedController.PATH, sessionCookie);
        assertThat(protectedResource.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Logout with CSRF: 204 and the container deletes the session cookie.
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.add(HttpHeaders.COOKIE,
                sessionCookie + "; " + SecurityConfig.CSRF_COOKIE_NAME + "=" + csrf);
        logoutHeaders.add(SecurityConfig.CSRF_HEADER_NAME, csrf);
        ResponseEntity<String> logout = rest.exchange(SignInController.SIGN_OUT_PATH, HttpMethod.POST,
                new HttpEntity<>(logoutHeaders), String.class);
        assertThat(logout.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(TestCookies.value(setCookies(logout), SecurityConfig.SESSION_COOKIE_NAME))
                .as("logout must delete the session cookie")
                .isEmpty();

        // The old session id no longer authenticates anything (FR-AUTH-002).
        assertThat(get(TestProtectedController.PATH, sessionCookie).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void csrfIsRequiredForStateChangingRequestsOverRealHttp() {
        String csrf = fetchCsrfToken();

        // The browser would auto-send the XSRF-TOKEN cookie; without the header
        // echo the request must be rejected before any authentication happens.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, SecurityConfig.CSRF_COOKIE_NAME + "=" + csrf);
        ResponseEntity<String> rejected = rest.exchange(SignInController.SIGN_IN_PATH, HttpMethod.POST,
                new HttpEntity<>(signInJson(), headers), String.class);
        assertThat(rejected.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void anonymousRequestsGet401JsonAndAuthenticatedUnknownPathsGet404() {
        // Anonymous: 401 with the container's JSON error body — the error
        // dispatch must not be blocked by (or re-answer through) the security
        // chain's authentication rules.
        ResponseEntity<String> anonymous = get(TestProtectedController.PATH, null);
        assertThat(anonymous.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(anonymous.getHeaders().getContentType()).isNotNull();
        assertThat(anonymous.getHeaders().getContentType()
                .isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(anonymous.getBody()).contains("\"status\":401");

        // Authenticated: an unknown path is 404, not another 401/403 — proof
        // that the session was accepted and the error dispatch renders.
        String csrf = fetchCsrfToken();
        String sessionId = sessionIdOf(signIn(csrf, null));
        assertThat(get("/api/does-not-exist", SecurityConfig.SESSION_COOKIE_NAME + "=" + sessionId)
                .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void authenticatingMigratesAnyPreExistingSessionIdToDefeatFixation() {
        String csrf = fetchCsrfToken();
        ResponseEntity<String> first = signIn(csrf, null);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        String presentedId = sessionIdOf(first);
        assertThat(presentedId).isNotBlank();

        // Authenticating again with that id presented must migrate the session
        // id (migrateSession semantics): an id known before authentication —
        // the fixation setup — can never carry into the authenticated session.
        // (Production creates no unauthenticated sessions at all, which is why
        // the test uses a session obtained moments earlier.)
        ResponseEntity<String> second =
                signIn(csrf, SecurityConfig.SESSION_COOKIE_NAME + "=" + presentedId);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        String migratedId = sessionIdOf(second);
        assertThat(migratedId).isNotBlank();
        assertThat(migratedId).isNotEqualTo(presentedId);

        // The migrated id works; the presented id is dead server-side.
        assertThat(get(TestProtectedController.PATH,
                SecurityConfig.SESSION_COOKIE_NAME + "=" + migratedId).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(get(TestProtectedController.PATH,
                SecurityConfig.SESSION_COOKIE_NAME + "=" + presentedId).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> get(String path, String cookieHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (cookieHeader != null) {
            headers.add(HttpHeaders.COOKIE, cookieHeader);
        }
        return rest.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private String fetchCsrfToken() {
        ResponseEntity<String> health = get(HealthController.HEALTH_PATH, null);
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = TestCookies.value(setCookies(health), SecurityConfig.CSRF_COOKIE_NAME);
        assertThat(token).as("XSRF-TOKEN cookie must be issued on safe requests").isNotBlank();
        return token;
    }

    private ResponseEntity<String> signIn(String csrfToken, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(SecurityConfig.CSRF_HEADER_NAME, csrfToken);
        String cookie = SecurityConfig.CSRF_COOKIE_NAME + "=" + csrfToken;
        if (sessionCookie != null) {
            cookie += "; " + sessionCookie;
        }
        headers.add(HttpHeaders.COOKIE, cookie);
        return rest.exchange(SignInController.SIGN_IN_PATH, HttpMethod.POST,
                new HttpEntity<>(signInJson(), headers), String.class);
    }

    private String signInJson() {
        return "{\"email\":\"" + TestAuthFixtures.FIXTURE_EMAIL
                + "\",\"password\":\"" + TestAuthFixtures.FIXTURE_PASSWORD + "\"}";
    }

    private String sessionIdOf(ResponseEntity<String> response) {
        return TestCookies.value(setCookies(response), SecurityConfig.SESSION_COOKIE_NAME);
    }

    private static List<String> setCookies(ResponseEntity<String> response) {
        return response.getHeaders().getOrDefault(HttpHeaders.SET_COOKIE, List.of());
    }
}
