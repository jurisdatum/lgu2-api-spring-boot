package uk.gov.legislation.endpoints.contact;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests that validation annotations on the ContactRequest record work correctly.
 * Verifies that Spring Boot's Bean Validation properly handles record components.
 */
@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactRequestValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rejectsRequestWhenFullNameMissing() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("Request validation failed for 1 field(s)"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.fullName").value("Full name is required"));
    }

    @Test
    void rejectsRequestWhenEmailMissing() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test User\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("Request validation failed for 1 field(s)"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.email").value("Email address is required"));
    }

    @Test
    void rejectsRequestWhenEmailInvalid() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test\",\"email\":\"not-an-email\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("Request validation failed for 1 field(s)"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.email").value("Email address must be valid"));
    }

    @Test
    void rejectsRequestWhenAddressMissing() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test\",\"email\":\"test@example.com\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.message").value("Request validation failed for 1 field(s)"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.address").value("Address is required"));
    }

    @Test
    void acceptsValidRequest() throws Exception {
        mockMvc.perform(post("/contact/tso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"address\":\"123 Main St\"}"))
            .andExpect(status().isNotImplemented()); // Controller returns 501
    }

}
