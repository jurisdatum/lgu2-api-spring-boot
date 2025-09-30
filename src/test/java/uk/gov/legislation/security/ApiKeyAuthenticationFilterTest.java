package uk.gov.legislation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for ApiKeyAuthenticationFilter.
 * Tests that the filter correctly sets authentication in the SecurityContext
 * when a valid API key is provided.
 */
class ApiKeyAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationWhenValidKeyProvided() throws ServletException, IOException {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("test-secret-key");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/contact/tso");
        request.addHeader("X-API-Key", "test-secret-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("api-client");
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_API_CLIENT");
        verify(chain).doFilter(request, response);
    }

    @Test
    void doesNotSetAuthenticationWhenKeyMissing() throws ServletException, IOException {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("test-secret-key");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/contact/tso");
        // No X-API-Key header
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doesNotSetAuthenticationWhenKeyIncorrect() throws ServletException, IOException {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("test-secret-key");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/contact/tso");
        request.addHeader("X-API-Key", "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void doesNotSetAuthenticationWhenKeyNotConfigured() throws ServletException, IOException {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(""); // Empty config
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/contact/tso");
        request.addHeader("X-API-Key", "some-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void alwaysInvokesFilterChainRegardlessOfKey() throws ServletException, IOException {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("test-secret-key");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/documents/ukpga");
        // No API key for public endpoint
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        // Filter is passive - always continues the chain
        verify(chain).doFilter(request, response);
    }
}
