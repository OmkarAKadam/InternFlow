package com.internflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that the application context loads with the Stage 2.3 foundation:
 * the real security configuration (deny-by-default filter chain, BCrypt
 * password encoder, no default in-memory user) and JPA auto-configuration
 * without a live database. Production has no credential source at all yet
 * (persistence is blocked by OD-02/OD-03), which is the state this context
 * reflects; SignInNotConfiguredTest covers the resulting sign-in behaviour.
 */
@SpringBootTest
class InternFlowApplicationTests {

    @Test
    void contextLoads() {
    }
}
