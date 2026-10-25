package com.internflow.shared.health;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal liveness-style health endpoint (proposed decision IF-DEC-18).
 *
 * <p>Returns a constant, non-sensitive response. It intentionally exposes no
 * configuration, dependency, or data details (NFR-SEC-003, NFR-SEC-008).
 * Database health checks are deliberately not included while the database
 * design remains a draft and no schema exists.</p>
 */
@RestController
public class HealthController {

    /** Referenced by the security rules in {@code com.internflow.auth.SecurityConfig}. */
    public static final String HEALTH_PATH = "/api/health";

    @GetMapping(HEALTH_PATH)
    public Map<String, String> health() {
        return Map.of("status", "UP", "application", "internflow-backend");
    }
}
