package com.internflow.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Placeholder security configuration for the development foundation.
 *
 * <p><strong>Status: this is NOT a production security setup.</strong> Spring Security
 * is part of the confirmed stack (IF-DEC-04), but every authentication and
 * authorization mechanic (credentials, password policy, session vs. token,
 * lifetimes, lockout) is an open decision (OD-15) and is intentionally not
 * implemented here.</p>
 *
 * <p>What this configuration does:</p>
 * <ul>
 *   <li>Denies by default: every path except {@code /api/health} requires authentication.</li>
 *   <li>Disables CSRF and session creation because no session or authentication
 *       mechanism exists yet; this must be revisited when OD-15 is resolved.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
