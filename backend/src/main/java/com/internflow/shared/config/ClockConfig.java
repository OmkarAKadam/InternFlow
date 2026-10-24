package com.internflow.shared.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application-wide {@link Clock} (recommended cross-cutting concern at
 * {@code SAD.md} §5.14, which lists "clock" under {@code shared/}).
 *
 * <p>Time is injected rather than read from static factories so that
 * time-dependent behaviour — currently the 8-hour absolute session lifetime
 * approved in IF-DEC-19 — can be tested deterministically by substituting a
 * controllable clock in tests, without weakening any production setting.</p>
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
