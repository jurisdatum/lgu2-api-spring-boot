package uk.gov.legislation.endpoints.fragment;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@WebMvcTest(FragmentController.class)
@AutoConfigureMockMvc
class FragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Transforms transforms;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void shouldReturnFragmentXml_whenValidRequest() {

        // 1. Mock marklogic response
        String expectedXml = "<fragment><section>1</section></fragment>";
        Legislation.Response mockResponse = new Legislation.Response(expectedXml, Optional.empty());

        when(marklogic.getDocumentSection(
            anyString(), anyString(), anyInt(), anyString(), any(Optional.class), any(Optional.class)))
            .thenReturn(mockResponse);

        given()
            .header("Accept-Language", "en")
            .queryParam("version", "enacted")
            .accept("application/xml")
            .when()
            .get("/fragment/ukla/2020/1/section-1")
            .then()
            .statusCode(200)
            .contentType("application/xml")
            .body(equalTo(expectedXml));
    }

    @Test
    void shouldReturn404_whenPathVariableIsMissing() {

        given()
            .accept("application/xml")
            .queryParam("version", "enacted")
            .header("Accept-Language", "en")
            .when()
            .get("/fragment/ukla/2020/1") // Missing 'section'
            .then()
            .statusCode(404)
            .onFailMessage("404 for missing path variables");
    }

    @Test
    @DisplayName("Invalid Year Requested")
    void shouldReturn400_whenYearIsInvalid() {

        given()
            .accept("application/xml")
            .queryParam("version", "enacted")
            .header("Accept-Language", "en")
            .when()
            .get("/fragment/ukla/not-a-year/1/section-1")
            .then()
            .statusCode(400)
            .onFailMessage("Bad Request due to type invalid year");
    }


    @Test
    @DisplayName("Default Accept language header")
    void shouldUseDefaultLocale_whenNoAcceptLanguageHeaderProvided() {
        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        given()
            .accept("application/xml")
            .queryParam("version", "enacted")
            .when()
            .get("/fragment/ukla/2020/1/section-1")
            .then()
            .statusCode(200)
            .contentType("application/xml")
            .body(equalTo(clmlXml))
            .header("Content-Language", equalTo("en"));

    }

    @ParameterizedTest
    @ValueSource(strings = {
        "en",
        "cy",
    })
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) {
        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        given()
            .accept("application/xml")
            .header("Accept-Language", acceptLanguageHeader)
            .queryParam("version", "enacted")
            .when()
            .get("/fragment/ukla/2020/1/section-1")
            .then()
            .statusCode(200)
            .contentType("application/xml")
            .body(equalTo(clmlXml))
            .header("Content-Language", equalTo(acceptLanguageHeader));

    }


    @ParameterizedTest
    @ValueSource(strings = {
        "application/json",
        "application/akn+xml",
        "text/html",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    })
    void shouldReturn200ForSupportedAcceptHeaders(String acceptHeader) {

        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        given()
            .accept(acceptHeader)
            .queryParam("version", "enacted")
            .header("Accept-Language", "en")
            .when()
            .get("/fragment/ukla/2020/1/section-1")
            .then()
            .statusCode(200);
    }
    @Test
    @DisplayName("Unsupported Accept Headers")
    void shouldReturn400ForUnSupportedAcceptHeaders() {

        given()
            .accept("application/abc")
            .queryParam("version", "enacted")
            .header("Accept-Language", "en")
            .when()
            .get("/fragment/ukla/2020/1/section-1")
            .then()
            .statusCode(406)
            .onFailMessage("406 for unsupported accept headers");
    }
}

