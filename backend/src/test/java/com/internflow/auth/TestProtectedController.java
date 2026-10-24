package com.internflow.auth;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TEST-ONLY endpoint behind authentication, used to observe whether a request
 * carries an authenticated session. It is never part of the production
 * surface: it lives in test sources and is registered only through
 * {@link TestAuthFixtures}.
 */
@RestController
class TestProtectedController {

    static final String PATH = "/api/test/protected";

    @GetMapping(PATH)
    public Map<String, String> protectedResource() {
        return Map.of("result", "ok");
    }
}
