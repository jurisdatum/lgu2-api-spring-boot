package uk.gov.legislation.transform.simple;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.converters.FragmentMetadataConverter;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
class UpToDateTest {

    private final Simplify simplifier;

    @Autowired
    UpToDateTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    static Stream<String> provide() {
        return Stream.of("ukpga/2000/8/schedule/6/paragraph/4A");
    }

    @ParameterizedTest
    @MethodSource("provide")
    void meta(String id) throws IOException, SaxonApiException {
        String resource = "/" + id.replace('/', '_') + "/clml.xml";
        String clml = UnappliedEffectsHelper.read(resource);
        Metadata simple = simplifier.extractFragmentMetadata(clml);
        FragmentMetadata meta = FragmentMetadataConverter.convert(simple);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(meta);
        String expected = UnappliedEffectsHelper.read("/" + id.replace('/', '_') + "/meta.json");
        assertEquals(expected, actual);
    }

}
