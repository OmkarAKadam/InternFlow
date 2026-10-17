package com.internflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that the application context loads with the current foundation
 * (web, security placeholder, JPA auto-configuration without a live database).
 */
@SpringBootTest
class InternFlowApplicationTests {

    @Test
    void contextLoads() {
    }
}
