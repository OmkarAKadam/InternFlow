package com.internflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the InternFlow backend.
 *
 * <p>InternFlow is a modular monolith (IF-DEC-01): a single Spring Boot deployable
 * that will eventually host the 13 MVP modules as packages under {@code com.internflow}.
 * Module packages are intentionally not created until they contain real code; the
 * planned layout is documented in {@code docs/development/SETUP.md}.</p>
 */
@SpringBootApplication
public class InternFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternFlowApplication.class, args);
    }
}
