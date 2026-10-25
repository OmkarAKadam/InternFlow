package com.internflow.auth;

import java.util.List;

/** TEST-ONLY helpers for reading {@code Set-Cookie} response headers. */
final class TestCookies {

    private TestCookies() {
    }

    /** Value of the named cookie in the given {@code Set-Cookie} headers, or {@code null}. */
    static String value(List<String> setCookieHeaders, String cookieName) {
        String prefix = cookieName + "=";
        for (String header : setCookieHeaders) {
            if (header.startsWith(prefix)) {
                String rest = header.substring(prefix.length());
                int end = rest.indexOf(';');
                return end >= 0 ? rest.substring(0, end) : rest;
            }
        }
        return null;
    }

    /** Whether the named cookie's {@code Set-Cookie} header contains the given text. */
    static boolean headerContains(List<String> setCookieHeaders, String cookieName, String text) {
        String prefix = cookieName + "=";
        for (String header : setCookieHeaders) {
            if (header.startsWith(prefix) && header.contains(text)) {
                return true;
            }
        }
        return false;
    }
}
