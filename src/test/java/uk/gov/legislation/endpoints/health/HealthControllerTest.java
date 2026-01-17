package uk.gov.legislation.endpoints.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.Ping;
import uk.gov.legislation.data.virtuoso.queries.AskNothing;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Ping markLogic;

    @MockitoBean
    private AskNothing virtuoso;

    @Test
    void healthReturnsOk() throws Exception {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void dependenciesBothUp() throws Exception {
        when(markLogic.getAsBoolean()).thenReturn(true);
        when(virtuoso.getAsBoolean()).thenReturn(true);
        mockMvc.perform(get("/health/dependencies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"))
            .andExpect(jsonPath("$.dependencies.marklogic").value("ok"))
            .andExpect(jsonPath("$.dependencies.virtuoso").value("ok"));
    }

    @Test
    void dependenciesBothDown() throws Exception {
        when(markLogic.getAsBoolean()).thenReturn(false);
        when(virtuoso.getAsBoolean()).thenReturn(false);
        mockMvc.perform(get("/health/dependencies"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value("fail"))
            .andExpect(jsonPath("$.dependencies.marklogic").value("fail"))
            .andExpect(jsonPath("$.dependencies.virtuoso").value("fail"));
    }

    @Test
    void dependenciesOneDown() throws Exception {
        when(markLogic.getAsBoolean()).thenReturn(true);
        when(virtuoso.getAsBoolean()).thenReturn(false);
        mockMvc.perform(get("/health/dependencies"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.status").value("fail"))
            .andExpect(jsonPath("$.dependencies.marklogic").value("ok"))
            .andExpect(jsonPath("$.dependencies.virtuoso").value("fail"));
    }

}
