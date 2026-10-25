package com.internflow.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password hashing behaviour required by IF-DEC-19 (BCrypt, closing OD-15):
 * hashes are salted and one-way, and verification accepts the original
 * password and rejects others. A plain unit test of the library contract the
 * sign-in flow relies on; the encoder's bean wiring is asserted in
 * {@link SecurityConfigurationTest}.
 */
class PasswordEncoderTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void bcryptProducesSaltedOneWayHashesThatVerify() {
        String first = encoder.encode("fixture-password-1234");
        String second = encoder.encode("fixture-password-1234");

        assertThat(first).startsWith("$2");
        assertThat(first).doesNotContain("fixture-password-1234");
        // Salting: the same password never hashes to the same string.
        assertThat(second).isNotEqualTo(first);

        assertThat(encoder.matches("fixture-password-1234", first)).isTrue();
        assertThat(encoder.matches("wrong-password-1234", first)).isFalse();
    }
}
