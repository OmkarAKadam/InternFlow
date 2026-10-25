package com.internflow.auth;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fails application startup when the production profile is active but the
 * session cookie would be issued without the {@code Secure} flag.
 *
 * <p><strong>Why this exists (security fix M-01).</strong> IF-DEC-19 requires the
 * session cookie to be {@code Secure} in production. {@code application.yml}
 * deliberately defaults {@code INTERNFLOW_COOKIE_SECURE} to {@code false} so local
 * plain-HTTP development keeps working — which means a production deployment that
 * forgets the variable would silently start and transmit the session cookie in
 * clear text. This guard closes that gap by refusing to start instead.</p>
 *
 * <p><strong>What it checks.</strong> Not the environment variable itself, but the
 * <em>effective</em> value Spring Boot will hand to the servlet container
 * ({@code server.servlet.session.cookie.secure}), read from the bound
 * {@link ServerProperties}. Checking the effective configuration means any source
 * of the setting — env var, {@code application-prod.yml}, {@code SPRING_APPLICATION_JSON},
 * or command-line argument — is honoured, and the guard cannot be defeated by a
 * setting that never reaches the container.</p>
 *
 * <p><strong>Deliberately out of scope.</strong> This is a configuration safeguard,
 * not a TLS validator: it cannot confirm that the deployment actually terminates
 * TLS, that a reverse proxy sets {@code X-Forwarded-Proto}, or that the
 * {@code SameSite} topology is correct (still open under IF-DEC-10). Those remain
 * deployment-time responsibilities recorded in the Stage 2.3 review report.</p>
 */
@Component
final class ProductionSessionCookieGuard implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ProductionSessionCookieGuard.class);

    /** Profiles that represent a production deployment (matching IF-DEC-19 wording). */
    private static final String[] PRODUCTION_PROFILES = {"prod", "production"};

    private final Environment environment;
    private final ServerProperties serverProperties;

    ProductionSessionCookieGuard(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override
    public void afterPropertiesSet() {
        if (!isProductionProfile()) {
            return;
        }
        if (!isSessionCookieSecure()) {
            throw new IllegalStateException(
                    "Refusing to start: the '"
                            + String.join("', '", environment.getActiveProfiles())
                            + "' profile is active but the session cookie is not marked Secure. "
                            + "IF-DEC-19 requires a Secure session cookie in production. Set "
                            + "INTERNFLOW_COOKIE_SECURE=true (or server.servlet.session.cookie.secure=true) "
                            + "so the session cookie is never sent over clear text.");
        }
        logger.info("Production profile active: session cookie is marked Secure");
    }

    /** True when any active profile denotes a production deployment. */
    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> Arrays.stream(PRODUCTION_PROFILES)
                        .anyMatch(profile::equalsIgnoreCase));
    }

    /**
     * The effective {@code Secure} flag of the session cookie, as bound into
     * {@link ServerProperties} and handed to the servlet container. Treated as
     * not-secure when unset, so an absent value fails safe.
     */
    private boolean isSessionCookieSecure() {
        Session.Cookie cookie = serverProperties.getServlet().getSession().getCookie();
        Boolean secure = cookie.getSecure();
        return Boolean.TRUE.equals(secure);
    }
}
