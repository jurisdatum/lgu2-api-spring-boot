package uk.gov.legislation.api.test;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.Application;
import uk.gov.legislation.transform.simple.Simplify;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.legislation.api.test.ExtendedMetadataTestRedo.*;

@SpringBootTest(classes = Application.class)
class ExtendedMetadataTest {

    static Stream<String> provide() {
        return Stream.of("asp/2025/11");
    }

    private final Simplify simplifier;

    @Autowired
    ExtendedMetadataTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    @ParameterizedTest
    @MethodSource("provide")
    void one(String id) throws IOException, SaxonApiException, TransformerException {
        String xml = readClml(id);
        String simple = simplify(simplifier, xml);
        String expected = readSimpleXml(id);
        assertEquals(expected, simple);
        String json = toJson(simple);
        expected = readJson(id);
        assertEquals(expected, json);
    }

}
