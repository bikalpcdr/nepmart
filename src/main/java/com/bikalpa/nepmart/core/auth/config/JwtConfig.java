package com.bikalpa.nepmart.core.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Activates {@link JwtProperties} binding. Kept as its own class so it can be
 * composed into any test slice via {@code @Import}.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
}
