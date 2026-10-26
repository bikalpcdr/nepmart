package com.bikalpa.nepmart.core.auth.filter;

import com.bikalpa.nepmart.core.auth.utility.JwtUtility;
import com.bikalpa.nepmart.core.auth.service.AppUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Reads the {@code Authorization: Bearer <token>} header and, when it carries a
 * valid access token, populates the SecurityContext.
 *
 * <p>Deliberately never writes an error response itself: an invalid or missing
 * token simply leaves the request anonymous and the authorization rules decide
 * the outcome. Refresh tokens are rejected here so they cannot be used as
 * access tokens.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtility jwtUtility;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtility.isTokenValid(token)
                && JwtUtility.TYPE_ACCESS.equals(jwtUtility.extractTokenType(token))) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtility.extractEmail(token));

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the raw token, or {@code null} when the header is absent or
     * not a bearer credential.
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_NAME);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        String token = header.substring(TOKEN_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
