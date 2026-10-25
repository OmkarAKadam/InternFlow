package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Security fix M-01: {@link ProductionSessionCookieGuard} must refuse to start
 * the application in a production profile when the session cookie's effective
 * {@code Secure} flag is false or absent, and must accept a secure setting.
 *
 * <p>{@link ApplicationContextRunner} is used rather than a full
 * {@code @SpringBootTest} because the behaviour under test <em>is</em> a
 * context-creation failure: a failing {@code @SpringBootTest} context surfaces
 * as a container error rather than a readable assertion. The runner gives a
 * deterministic pass/fail per configuration. Plain-HTTP local development (no
 * production profile, secure flag false) must keep starting — covered by
 * {@code devStartupIsUnaffectedByTheGuard}.</p>
 *
 * <p>These tests validate the effective {@code server.servlet.session.cookie}
 * configuration, which is what the guard reads — not the environment variable
 * directly. They do not prove anything about TLS termination or proxy headers,
 * which remain deployment responsibilities (see the Stage 2.3 review report).</p>
 */
class ProductionSessionCookieGuardTest {

    @Test
    void productionProfileWithSecureCookieDisabledFailsStartup() {
        runner().withPropertyValues("server.servlet.session.cookie.secure=false")
                .withInitializer(context -> ((ConfigurableEnvironment) context.getEnvironment())
                        .addActiveProfile("prod"))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasRootCauseInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("Secure");
                });
    }

    @Test
    void productionProfileWithSecureCookieUnsetFailsStartup() {
        // No secure value at all: the guard must fail safe, not treat absence
        // as though the flag were set.
        runner().withInitializer(context -> ((ConfigurableEnvironment) context.getEnvironment())
                        .addActiveProfile("prod"))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasRootCauseInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("Secure");
                });
    }

    @Test
    void productionProfileWithSecureCookieEnabledStarts() {
        runner().withPropertyValues("server.servlet.session.cookie.secure=true")
                .withInitializer(context -> ((ConfigurableEnvironment) context.getEnvironment())
                        .addActiveProfile("prod"))
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void devStartupIsUnaffectedByTheGuard() {
        // Local development keeps working: no production profile, so the
        // secure=false default (plain HTTP) must not abort startup.
        runner().withPropertyValues("server.servlet.session.cookie.secure=false")
                .withInitializer(context -> ((ConfigurableEnvironment) context.getEnvironment())
                        .addActiveProfile("dev"))
                .run(context -> assertThat(context).hasNotFailed());
    }

    /**
     * Binds the real {@link ServerProperties} (so the effective cookie flag is
     * read exactly as the container would) and registers the guard under test.
     * Active profiles are applied per-test via {@code withInitializer} above.
     */
    private static ApplicationContextRunner runner() {
        return new ApplicationContextRunner()
                .withUserConfiguration(GuardConfiguration.class);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ServerProperties.class)
    static class GuardConfiguration {

        @Bean
        ProductionSessionCookieGuard productionSessionCookieGuard(
                org.springframework.core.env.Environment environment,
                ServerProperties serverProperties) {
            return new ProductionSessionCookieGuard(environment, serverProperties);
        }
    }
}
