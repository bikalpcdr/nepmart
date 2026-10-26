package com.bikalpa.nepmart.core.auth.utility;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.auth.config.JwtProperties;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Central JWT helper shared by the auth module and any other module that needs
 * to read the current principal.
 *
 * <p>Two token types are issued, distinguished by a {@code type} claim so a
 * refresh token can never be replayed as an access token:
 * <ul>
 *   <li>{@link #TYPE_ACCESS} - short lived, sent on every request</li>
 *   <li>{@link #TYPE_REFRESH} - long lived, only accepted by {@code /refresh}</li>
 * </ul>
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtility {

    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_UUID = "uuid";

    public static final String TYPE_ACCESS = "ACCESS";
    public static final String TYPE_REFRESH = "REFRESH";

    private final JwtProperties jwtProperties;
    private final UserRepo userRepo;

    /**
     * Lazily derived from {@code application.security.jwt.secret-key}. The key must
     * encode to at least 256 bits for HS256, otherwise JJWT throws WeakKeyException.
     */
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    /* ------------------------------------------------------------------ */
    /* Token creation                                                      */
    /* ------------------------------------------------------------------ */

    public String generateAccessToken(User user) {
        return buildToken(user, TYPE_ACCESS, jwtProperties.accessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, TYPE_REFRESH, jwtProperties.refreshTokenExpiration());
    }

    private String buildToken(User user, String tokenType, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim(CLAIM_TYPE, tokenType)
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_UUID, user.getUuid().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    /* ------------------------------------------------------------------ */
    /* Token reading and validation                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Parses the token, mapping JJWT failures onto the project's
     * {@link GenericUncheckedException} so the global handler renders them uniformly.
     */
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            throw new GenericUncheckedException(MessageConstant.TOKEN_EXPIRED_MESSAGE,
                    MessageConstant.TOKEN_EXPIRED, UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new GenericUncheckedException(MessageConstant.INVALID_TOKEN_MESSAGE,
                    MessageConstant.INVALID_TOKEN, UNAUTHORIZED);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get(CLAIM_EMAIL, String.class);
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get(CLAIM_TYPE, String.class);
    }

    public UUID extractUserUuid(String token) {
        String uuid = extractAllClaims(token).get(CLAIM_UUID, String.class);
        return uuid == null ? null : UUID.fromString(uuid);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Access token lifetime in seconds, for the {@code expiresIn} field of the
     * auth response. Config is in milliseconds so clients can plan refreshes.
     */
    public long accessTokenExpirationSeconds() {
        return jwtProperties.accessTokenExpiration() / 1000;
    }

    /**
     * True when the signature, claims and expiry are all valid. Used by the
     * security filter, which must not propagate exceptions for a bad token.
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && isTokenValid(token);
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(extractTokenType(token));
    }

    /* ------------------------------------------------------------------ */
    /* Current principal helpers                                           */
    /* ------------------------------------------------------------------ */

    /**
     * The authenticated user's email, or {@code null} when the request is anonymous.
     * Safe to call from any bean - it never throws.
     */
    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal instanceof String principalName && !"anonymousUser".equals(principalName)) {
            return principalName;
        }
        return null;
    }

    /**
     * The current {@link User} entity, or {@link Optional#empty()} when unauthenticated.
     */
    public Optional<User> getCurrentUser() {
        String email = getCurrentEmail();
        return email == null ? Optional.empty() : userRepo.findByEmailIgnoreCase(email);
    }

    /**
     * Convenience for service code that already knows authentication is present.
     *
     * @throws GenericUncheckedException 401 when there is no authenticated principal
     */
    public User getCurrentUserOrThrow() {
        return getCurrentUser().orElseThrow(() -> new GenericUncheckedException(
                MessageConstant.AUTHENTICATION_REQUIRED_MESSAGE,
                MessageConstant.INVALID_TOKEN,
                UNAUTHORIZED));
    }

    /**
     * The current user's public uuid, for endpoints that address entities by uuid.
     */
    public UUID getCurrentUserUuid() {
        return getCurrentUser().map(User::getUuid).orElse(null);
    }
}
