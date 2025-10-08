package uk.gov.legislation.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Spring Security configuration.
 * Tests the full authentication/authorization chain including filter and SecurityConfig.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpointReturns401WithoutApiKey() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test\",\"email\":\"test@example.com\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointReturns401WithInvalidApiKey() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .header("X-API-Key", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test\",\"email\":\"test@example.com\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointAllowsRequestWithValidApiKey() throws Exception {
        // Note: This uses the api.key.secret from application-test.properties
        mockMvc.perform(post("/contact/tso")
                .header("X-API-Key", "test-api-key-12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test\",\"email\":\"test@example.com\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isNotImplemented()); // Controller throws NOT_IMPLEMENTED
    }

    @Test
    void publicEndpointAllowsRequestWithoutApiKey() throws Exception {
        // Public endpoints should work without API key
        mockMvc.perform(get("/types"))
            .andExpect(status().isOk());
    }

    @Test
    void publicEndpointAllowsRequestWithApiKey() throws Exception {
        // Public endpoints should also work WITH API key (filter is passive)
        mockMvc.perform(get("/types")
                .header("X-API-Key", "test-api-key-12345"))
            .andExpect(status().isOk());
    }
}
