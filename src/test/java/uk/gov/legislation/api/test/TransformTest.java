package uk.gov.legislation.api.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.Application;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.endpoints.fragment.service.TransformationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

@SpringBootTest(classes = Application.class)
public class TransformTest {

    private final TransformationService transform;

    @Autowired
    TransformTest(TransformationService transform) {
        this.transform = transform;
    }

    static Stream<String> provide() {
        return Stream.of("ukpga/2000/8/section/91");
    }

    static String read(String resource) throws IOException {
        String content;
        try (var input = TransformTest.class.getResourceAsStream(resource)) {
            Objects.requireNonNull(input);
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        return content;
    }

    static String makeResourceName(String id, String ext) {
        String dir = id.replace('/', '_') + "/";
        String file = id.replace('/', '-') + ext;
        return  "/" +  dir + file;
    }

    static String read(String id, String ext) throws IOException {
        String resource = makeResourceName(id, ext);
        return read(resource);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void akn(String id) throws Exception {
        String clml = read(id, ".xml");
        String actual = transform.transformToAkn(clml)
            .replaceFirst("<FRBRdate date=\".+?\" name=\"transform\"/>", "<FRBRdate date=\"2024-12-19-05:00\" name=\"transform\"/>");
        String expected = read(id, ".akn.xml");
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void html(String id) throws Exception {
        String clml = read(id, ".xml");
        String actual = transform.transformToHtml(clml, true).replaceFirst("""
            <div property="FRBRdate" typeof="FRBRdate">
             {21}<meta property="date" content="[^"]+">
             {21}<meta property="name" content="transform">""", """
            <div property="FRBRdate" typeof="FRBRdate">
                                 <meta property="date" content="2024-12-19-05:00">
                                 <meta property="name" content="transform">""");
        String expected = read(id, ".html");
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void json(String id) throws Exception {
        String clml = read(id, ".xml");
        DocumentApi.Response response = transform.createJsonResponse(clml);
        ObjectMapper mapper = new ObjectMapper()
            .registerModules(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);
        String actual = mapper.writeValueAsString(response);
        String expected = read(id, ".json");
        Assertions.assertEquals(expected, actual);
    }

}
