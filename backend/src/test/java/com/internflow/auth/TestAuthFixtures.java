package com.internflow.auth;

import java.time.Clock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * TEST-ONLY authentication fixtures.
 *
 * <p>Production has no credential source at all (persistence is blocked by
 * OD-02/OD-03), so the sign-in flow could not otherwise be exercised. Tests
 * importing this configuration get:</p>
 * <ul>
 *   <li>a fixture account whose identity and password exist only in test
 *       sources — hardcoded credentials are permitted in test fixtures, and
 *       nothing equivalent exists under {@code src/main};</li>
 *   <li>{@link MutableClock} as the {@code @Primary} {@link Clock}, so the
 *       8-hour absolute session lifetime is tested at its exact boundary
 *       without waiting or shortening any production setting.</li>
 * </ul>
 */
@TestConfiguration(proxyBeanMethods = false)
class TestAuthFixtures {

    /** Fixture account identity — test-only, never present in production. */
    static final String FIXTURE_EMAIL = "student@college.example";

    /** Fixture account password — test-only, never present in production. */
    static final String FIXTURE_PASSWORD = "fixture-password-1234";

    @Bean
    @Primary
    MutableClock mutableClock() {
        return new MutableClock();
    }

    @Bean
    UserDetailsService fixtureUserDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(User
                .withUsername(FIXTURE_EMAIL)
                .password(passwordEncoder.encode(FIXTURE_PASSWORD))
                .roles("STUDENT")
                .build());
    }

    @Bean
    TestProtectedController testProtectedController() {
        return new TestProtectedController();
    }
}
