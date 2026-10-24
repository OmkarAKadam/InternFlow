package com.internflow.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * TEST-ONLY clock whose time moves only when a test moves it. Registered as the
 * {@code @Primary} {@link Clock} bean by {@link TestAuthFixtures}, so
 * {@code AbsoluteSessionLifetimeFilter} reads test-controlled time. Production
 * always receives {@code Clock.systemUTC()} from {@code ClockConfig} and
 * contains no fast-forward hook of any kind.
 */
final class MutableClock extends Clock {

    private volatile Instant instant;

    MutableClock() {
        this.instant = Instant.now();
    }

    /** Moves the clock forward by the given duration. */
    void advance(Duration duration) {
        this.instant = this.instant.plus(duration);
    }

    /** Restores the clock to real current time. */
    void reset() {
        this.instant = Instant.now();
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return this.instant;
    }
}
