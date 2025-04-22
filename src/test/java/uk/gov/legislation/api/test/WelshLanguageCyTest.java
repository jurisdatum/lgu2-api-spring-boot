package uk.gov.legislation.api.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import uk.gov.legislation.api.test.response.ExpectedWelshResponseDataCy;
import uk.gov.legislation.Application;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WelshLanguageCyTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setupRequestSpecification() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

    @Test
    @Description("Perform the GET request with Accept-Language header 'cy'")
    void testGetDocumentJsonWithAcceptCyLanguageHeader() throws JsonProcessingException {
        Response response = RestAssured.given()
                .param("version", "made")
                .header("Accept-Language", "cy")
                .get("/document/wsi/2024/1002");

        ObjectMapper mapper = new ObjectMapper();
        String actualResponse = response.getBody().asString();
        String expectedResponse = ExpectedWelshResponseDataCy.DOCUMENT_WELSH_RESPONSE_CY_JSON;

        // Assert status code and content type
        response.then().statusCode(200).contentType("application/json");

        // Compare JSON objects
        assertEquals(mapper.readTree(expectedResponse), mapper.readTree(actualResponse));
    }

    @Test
    @Description("Perform the GET request for (/content) with Accept-Language header 'cy'")
    void testGetContentsJsonWithAcceptCyLanguageHeader() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Accept-Language", "cy")
                .get("/contents/wsi/2024/1002");

        ObjectMapper mapper = new ObjectMapper();
        String actualResponse = response.getBody().asString();
        String expectedResponse = ExpectedWelshResponseDataCy.CONTENT_WELSH_RESPONSE_CY_JSON;

        // Assert status code and content type
        response.then().statusCode(200).contentType("application/json");

        // Compare JSON objects
        assertEquals(mapper.readTree(expectedResponse), mapper.readTree(actualResponse));
    }

    @Test
    @Description("Perform the GET request for (/fragment) with Accept-Language header 'cy'")
    void testGetFragmentsJsonWithAcceptCyLanguageHeader() throws JsonProcessingException {
        Response response = RestAssured.given()
                .header("Accept-Language", "cy")
                .get("/fragment/wsi/2024/1002/regulation-2");

        ObjectMapper mapper = new ObjectMapper();
        String actualResponse = response.getBody().asString();
        String expectedResponse = ExpectedWelshResponseDataCy.FRAGMENT_WELSH_RESPONSE_CY_JSON;

        // Assert status code and content type
        response.then().statusCode(200).contentType("application/json");

        // Compare JSON objects
        assertEquals(mapper.readTree(expectedResponse), mapper.readTree(actualResponse));
    }
}