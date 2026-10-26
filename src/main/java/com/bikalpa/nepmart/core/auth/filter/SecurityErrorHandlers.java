package com.bikalpa.nepmart.core.auth.filter;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.generics.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Renders 401/403 failures as {@link ApiResponse} JSON so error bodies keep the
 * same envelope as everything {@code GlobalExceptionHandler} produces.
 *
 * <p>Security failures happen inside the filter chain, before the DispatcherServlet,
 * so {@code GlobalExceptionHandler} never sees them - without this the client would
 * get Spring's default empty 401.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /** No or invalid credentials on a protected endpoint. */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(response, HttpServletResponse.SC_UNAUTHORIZED,
                MessageConstant.AUTHENTICATION_REQUIRED_MESSAGE, request);
    }

    /** Authenticated, but not allowed to perform this action. */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(response, HttpServletResponse.SC_FORBIDDEN,
                MessageConstant.ACCESS_DENIED_MESSAGE, request);
    }

    private void write(HttpServletResponse response, int status, String message,
                       HttpServletRequest request) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(),
                ApiResponse.error(message, request.getRequestURI()));
    }
}
