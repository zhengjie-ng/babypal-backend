package com.babypal.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Custom CSRF token repository that generates stateless tokens
 * suitable for cross-site JWT-based authentication
 */
@Component
public class JwtCsrfTokenRepository implements CsrfTokenRepository {

    private static final String CSRF_PARAMETER_NAME = "_csrf";
    private static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String CSRF_TOKEN_ATTR_NAME = JwtCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, createNewToken());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        // Store in request attribute for stateless operation
        if (token == null) {
            request.removeAttribute(CSRF_TOKEN_ATTR_NAME);
        } else {
            request.setAttribute(CSRF_TOKEN_ATTR_NAME, token);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        // For stateless operation, generate a new token for each request
        return (CsrfToken) request.getAttribute(CSRF_TOKEN_ATTR_NAME);
    }

    private String createNewToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}