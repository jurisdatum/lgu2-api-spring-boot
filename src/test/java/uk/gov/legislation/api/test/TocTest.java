package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.converters.TableOfContentsConverter;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.util.UpToDate;

import java.util.stream.Stream;

import static uk.gov.legislation.api.test.TransformHelper.read;
import static uk.gov.legislation.api.test.TransformTest.CUTOFF;

@SpringBootTest(classes = Application.class)
class TocTest {

    private final Simplify simplifier;

    @Autowired
    TocTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    static Stream<String> provide() {
        return Stream.of(
            "ukpga/2000/8/contents",
            "ukpga/2023/29/contents/2024-11-01",
            "ukla/2017/1/contents/enacted",
            "uksi/2025/3/contents/made",
            "ukpga/Geo6/6-7/48/contents/1991-02-01",
            "asp/2025/11/contents/2025-08-07",
            "nia/2022/21/contents/enacted",
            "aosp/1707/8/contents/2007-01-01",
            "aep/Ann/6/11/contents/1991-02-01",
            "aip/Geo3/40/38/contents/1991-02-01",
            "apgb/Geo3/39-40/14/contents"
        );
    }

    @ParameterizedTest
    @MethodSource("provide")
    void toc(String id) throws Exception {
        String clml = read(id, "xml");
        Contents simple = simplifier.contents(clml);
        TableOfContents toc = TableOfContentsConverter.convert(simple);
        UpToDate.setUpToDate(toc.meta, CUTOFF);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(toc);
        String expected = read(id, "json");
        Assertions.assertEquals(expected, actual);
    }

}
