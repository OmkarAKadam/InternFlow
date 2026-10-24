package com.internflow.auth;

import java.io.IOException;
import java.util.function.Supplier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Materializes the deferred CSRF token on safe (GET/HEAD/OPTIONS) requests so
 * that the {@code XSRF-TOKEN} cookie is written before an API client attempts a
 * state-changing request.
 *
 * <p>Spring Security defers CSRF token creation until something actually reads
 * the token. A JSON API has no template that reads it, so without this filter
 * the first {@code POST} would arrive with no cookie to echo and would be
 * rejected — not because of a real attack, but because the client never had a
 * chance to obtain the token. Materializing it here keeps CSRF protection fully
 * enabled (IF-DEC-19) while making the provisional contract usable.</p>
 *
 * <p>The token value is never logged; only the repository's normal cookie
 * write happens as a side effect of reading it.</p>
 */
final class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Object attribute = request.getAttribute(CsrfToken.class.getName());
        // Depending on the configured request handler the attribute is either the
        // token itself, a deferred token, or a plain supplier; reading it in every
        // shape forces the same load-or-create-and-save behaviour.
        if (attribute instanceof CsrfToken csrfToken) {
            csrfToken.getToken();
        } else if (attribute instanceof Supplier<?> supplier) {
            supplier.get();
        }
        filterChain.doFilter(request, response);
    }
}
