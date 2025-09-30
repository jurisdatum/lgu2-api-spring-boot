package uk.gov.legislation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

/**
 * Validates API key authentication for protected endpoints.
 * Uses constant-time comparison to prevent timing attacks.
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final byte[] validApiKey;

    public ApiKeyAuthenticationFilter(@Value("${api.key.secret:}") String validApiKey) {
        if (StringUtils.hasText(validApiKey)) {
            this.validApiKey = validApiKey.getBytes(StandardCharsets.UTF_8);
        } else {
            logger.warn("API key secret is not configured. Protected endpoints will return 401. Set 'api.key.secret' property to enable authentication.");
            this.validApiKey = new byte[0];
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (StringUtils.hasText(providedKey) && isValidKey(providedKey)) {
            // Valid API key - set authentication
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("api-client", null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidKey(String providedKey) {
        if (this.validApiKey.length == 0) {
            return false; // Not configured
        }
        byte[] providedBytes = providedKey.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(this.validApiKey, providedBytes);
    }

}
