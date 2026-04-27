package uk.gov.legislation.endpoints.fragment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.converters.UnappliedEffectsFetcher;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Clml2Pdf;
import uk.gov.legislation.transform.Transforms;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.simple.BilingualFragmentFixtures;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.transform.simple.UnappliedEffectsHelper;

import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FragmentController.class)
@Import({ Transforms.class, Clml2Akn.class, Akn2Html.class, Simplify.class })
class FragmentVersioningTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Clml2Docx clml2Docx;

    @MockitoBean
    private Clml2Pdf clml2Pdf;

    @MockitoBean
    private UnappliedEffectsFetcher unappliedEffectsFetcher;

    @Test
    void currentUnversionedFragment_usesLastFragmentMilestoneAsVersion() throws Exception {
        String clml = UnappliedEffectsHelper.read("/ukpga_2000_8/ukpga-2000-8-section-91.xml");
        Legislation.Response response = new Legislation.Response(clml, Optional.empty());
        when(marklogic.getDocumentSection("ukpga", "2000", 8, "section-91", Optional.empty(), Optional.of("en")))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukpga/2000/8/section-91")
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.meta.version").value("2024-01-30"))
            .andExpect(jsonPath("$.meta.pointInTime").value(nullValue()))
            .andExpect(jsonPath("$.meta.versions[0]").value("enacted"))
            .andExpect(jsonPath("$.meta.versions[9]").value("2024-01-30"));
    }

    @Test
    void welshFragmentVersions_areLanguageAware() throws Exception {
        Legislation.Response response = new Legislation.Response(BilingualFragmentFixtures.welsh(), Optional.empty());
        when(marklogic.getDocumentSection("wsi", "2020", 1609, "part-3-chapter-1", Optional.empty(), Optional.of("cy")))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/wsi/2020/1609/part-3-chapter-1")
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "cy"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.meta.version").value("2021-02-27"))
            .andExpect(jsonPath("$.meta.pointInTime").value(nullValue()))
            .andExpect(jsonPath("$.meta.versions", contains(
                "made", "2020-12-20", "2020-12-24", "2021-01-09",
                "2021-01-15", "2021-01-22", "2021-01-29", "2021-02-27")));
    }

    @Test
    void versionedWelshFragment_pointInTimeDoesNotOverrideWelshFragmentVersion() throws Exception {
        Legislation.Response response = new Legislation.Response(BilingualFragmentFixtures.welshPointInTime(), Optional.empty());
        when(marklogic.getDocumentSection("wsi", "2020", 1609, "part-3-chapter-1", Optional.of("2021-05-17"), Optional.of("cy")))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/wsi/2020/1609/part-3-chapter-1")
                .param("version", "2021-05-17")
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "cy"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.meta.version").value("2021-02-27"))
            .andExpect(jsonPath("$.meta.pointInTime").value("2021-05-17"))
            .andExpect(jsonPath("$.meta.versions", contains(
                "made", "2020-12-20", "2020-12-24", "2021-01-09",
                "2021-01-15", "2021-01-22", "2021-01-29", "2021-02-27")));
    }

    @Test
    void englishFragmentVersions_areLanguageAware() throws Exception {
        Legislation.Response response = new Legislation.Response(BilingualFragmentFixtures.english(), Optional.empty());
        when(marklogic.getDocumentSection("wsi", "2020", 1609, "part-3-chapter-1", Optional.empty(), Optional.of("en")))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/wsi/2020/1609/part-3-chapter-1")
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.meta.version").value("2022-03-28"))
            .andExpect(jsonPath("$.meta.pointInTime").value(nullValue()))
            .andExpect(jsonPath("$.meta.versions", contains(
                "made", "2021-05-17", "2021-08-07", "2022-03-28")))
            .andExpect(jsonPath("$.meta.versions", not(hasItems(
                "2020-12-20", "2020-12-24", "2021-01-09",
                "2021-01-15", "2021-01-22", "2021-01-29", "2021-02-27"))));
    }
}
