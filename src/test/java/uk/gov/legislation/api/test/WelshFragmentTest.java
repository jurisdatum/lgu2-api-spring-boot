package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.api.test.response.ExpectedWelshResponseDataCy;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.fragment.FragmentController;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Transforms;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FragmentController.class)
@Import({ Transforms.class, Clml2Akn.class, Akn2Html.class, Simplify.class, Clml2Docx.class})
class WelshFragmentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation mock;

    @Test
    void test() throws Exception {

        String clml = UnappliedEffectsHelper.read("/fragment_wsi_2024_1002_regulation_2_cy.xml");
        Legislation.Response response = new Legislation.Response(clml, Optional.empty());
        when(mock.getDocumentSection("wsi", "2024", 1002, "regulation-2", Optional.empty(), Optional.of("cy")))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/wsi/2024/1002/regulation-2")
                .header("Accept-Language", "cy"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(ExpectedWelshResponseDataCy.FRAGMENT_WELSH_RESPONSE_CY_JSON));
    }

}
