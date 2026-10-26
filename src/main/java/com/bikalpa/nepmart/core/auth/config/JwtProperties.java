package com.bikalpa.nepmart.core.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secretKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
