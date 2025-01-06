package uk.gov.legislation.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.endpoints.fragment.service.TransformationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@SpringBootTest
public class TransformTest {

    private final TransformationService transform;

    @Autowired
    TransformTest(TransformationService transform) {
        this.transform = transform;
    }


    static String read(String resource) throws IOException {
        String content;
        try (var input = TransformTest.class.getResourceAsStream(resource)) {
            Objects.requireNonNull(input);
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        return content;
    }

    private String getClml() throws IOException {
        return read("/ukpga-2000-8-section-91.xml");
    }

    @Test
    void akn() throws Exception {
        String clml = getClml();
        String actual = transform.transformToAkn(clml)
            .replaceFirst("<FRBRdate date=\".+?\" name=\"transform\"/>", "<FRBRdate date=\"2024-12-19-05:00\" name=\"transform\"/>");
        String expected = read("/ukpga-2000-8-section-91.akn.xml");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void html() throws Exception {
        String clml = getClml();
        String actual = transform.transformToHtml(clml, true).replaceFirst("""
            <div property="FRBRdate" typeof="FRBRdate">
             {21}<meta property="date" content="[^"]+">
             {21}<meta property="name" content="transform">""", """
            <div property="FRBRdate" typeof="FRBRdate">
                                 <meta property="date" content="2024-12-19-05:00">
                                 <meta property="name" content="transform">""");
        String expected = read("/ukpga-2000-8-section-91.html");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void json() throws Exception {
        String clml = getClml();
        DocumentApi.Response response = transform.createJsonResponse(clml);
        ObjectMapper mapper = new ObjectMapper()
            .registerModules(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);
        String actual = mapper.writeValueAsString(response);
        String expected = read("/ukpga-2000-8-section-91.json");
        Assertions.assertEquals(expected, actual);
    }

}
