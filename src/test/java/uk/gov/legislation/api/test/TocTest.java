package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.converters.TableOfContentsConverter;
import uk.gov.legislation.Application;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.util.UpToDate;

import java.time.LocalDate;
import java.util.stream.Stream;

import static uk.gov.legislation.api.test.TransformTest.read;

@SpringBootTest(classes = Application.class)
class TocTest {

    private final Simplify simplifier;

    @Autowired
    TocTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    static Stream<String> provide() {
        return Stream.of("ukpga/2000/8", "ukpga/2023/29/2024-11-01");
    }

    @ParameterizedTest
    @MethodSource("provide")
    void toc(String id) throws Exception {
        String clml = read(id, "-contents.xml");
        Contents simple = simplifier.contents(clml);
        TableOfContents toc = TableOfContentsConverter.convert(simple);
        LocalDate cutoff = LocalDate.of(2025, 3, 30);
        UpToDate.setUpToDate(toc.meta, cutoff);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(toc);
        String expected = read(id, "-contents.json");
        Assertions.assertEquals(expected, actual);
    }

}
