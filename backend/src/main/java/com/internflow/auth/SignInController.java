package com.internflow.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provisional sign-in / sign-out endpoints for Stage 2.3.
 *
 * <p><strong>Contract status: PROPOSED — NOT APPROVED.</strong> No API
 * Specification exists yet (SRS-IF-01 defers the endpoint catalogue and payload
 * schemas; SRS-IF-04 defers concrete status codes and error shapes; SAD §10
 * lists the API Specification as a future deliverable). The paths, payload
 * names, and status codes below are documented for review in
 * {@code docs/reviews/STAGE-2.3-REVIEW.md} §8 and must not be treated as an
 * approved contract. The frontend sign-in form is deliberately left
 * unconnected: {@code frontend/src/lib/auth/sign-in.ts} still performs no
 * network call, and this substage does not change any frontend file.</p>
 *
 * <p>What is <em>requirement-backed</em> rather than invented:</p>
 * <ul>
 *   <li>The login identity is the email address (IF-DEC-19); input shape is
 *       validated at the server boundary (SRS-IF-03).</li>
 *   <li>Every credential failure returns <strong>one generic message</strong>
 *       that cannot reveal whether an account exists or why it failed
 *       (FR-AUTH-001 AC2/AC3/AC4, FR-AUTH-006 AC1). The wording matches the
 *       frontend's Stage 2.2 {@code GENERIC_SIGN_IN_FAILURE} so both layers
 *       present the same text.</li>
 *   <li>Malformed requests are rejected before any authentication attempt
 *       (business validation failure, FRD §7.1).</li>
 *   <li>Sign-out invalidates the server-side session (FR-AUTH-002) — handled by
 *       the logout configuration in {@link SecurityConfig}.</li>
 * </ul>
 *
 * <p>Until a credential source exists (persistence blocked by OD-02/OD-03),
 * sign-in answers an explicit configuration-missing outcome — see
 * {@link SignInService} and the Stage 2.3 review report.</p>
 */
@RestController
public class SignInController {

    /** PROPOSED endpoint: authenticate and start a server-managed session. */
    static final String SIGN_IN_PATH = "/api/auth/sign-in";

    /** PROPOSED endpoint: invalidate the server-managed session. */
    static final String SIGN_OUT_PATH = "/api/auth/sign-out";

    /**
     * The single generic sign-in failure message (FR-AUTH-001). Identical to
     * the frontend constant {@code GENERIC_SIGN_IN_FAILURE} so the visible copy
     * does not differ between client and server.
     */
    static final String GENERIC_SIGN_IN_FAILURE_MESSAGE =
            "Incorrect email or password. Please try again.";

    private final SignInService signInService;

    public SignInController(SignInService signInService) {
        this.signInService = signInService;
    }

    @PostMapping(SIGN_IN_PATH)
    public ResponseEntity<Object> signIn(@Valid @RequestBody SignInRequest signInRequest,
            HttpServletRequest request, HttpServletResponse response) {
        SignInOutcome outcome =
                signInService.attemptSignIn(signInRequest.email(), signInRequest.password(), request, response);
        return switch (outcome) {
            // Session cookie carries the state; no body is invented for success.
            case AUTHENTICATED -> ResponseEntity.noContent().build();
            // Authentication failure with the one generic message (FR-AUTH-001 AC2,
            // FR-AUTH-006 AC1): same status and body for every credential cause.
            case REJECTED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorBody("sign-in-failed", GENERIC_SIGN_IN_FAILURE_MESSAGE));
            // Configuration-missing state (FRD §7.1): explicit, never a false
            // "wrong credentials" while persistence is blocked (OD-02/OD-03).
            case NOT_CONFIGURED -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorBody("sign-in-not-configured",
                            "Sign-in is not configured in this environment."));
            // Technical failure (FRD §7.1): never reported as a credential failure.
            case TECHNICAL_FAILURE -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody("technical-failure",
                            "Sign-in could not be completed right now."));
        };
    }

    /** Business validation failure (FRD §7.1): field-level, explanatory (NFR-UX-003). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "validation-failed");
        body.put("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /** Malformed JSON body — same business validation category as bean validation. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(errorBody("validation-failed", "The request body could not be read as JSON."));
    }

    private static Map<String, Object> errorBody(String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
