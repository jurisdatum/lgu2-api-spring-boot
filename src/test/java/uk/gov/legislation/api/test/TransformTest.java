package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.transform.Transforms;
import uk.gov.legislation.util.Links;
import uk.gov.legislation.util.UpToDate;

import java.time.LocalDate;
import java.util.stream.Stream;

import static uk.gov.legislation.api.test.TransformHelper.MAPPER;

@SpringBootTest(classes = Application.class)
@ExtendWith(LoggingTestWatcher.class)
class TransformTest {

    private final Transforms transforms;

    @Autowired
    TransformTest(Transforms transforms) {
        this.transforms = transforms;
    }

    static Stream<String> provide() {
        return Stream.of(
            "ukpga/2000/8/section/91",
            "ukpga/2023/29/2024-11-01",
            "ukla/2017/1/enacted", "ukla/2017/1/part/3/enacted",
            "uksi/2025/3/made", "uksi/2025/3/regulation/2/made",
            "ukpga/Geo6/6-7/48/1991-02-01", "ukpga/Geo6/6-7/48/part/1/1991-02-01",
            "asp/2025/11/2025-08-07", "asp/2025/11/part/1/crossheading/reviews/2025-08-07",
            "nia/2022/21/enacted", "nia/2022/21/introduction/enacted",
            "aosp/1707/8/2007-01-01", "aosp/1707/8/paragraph/p3/2007-01-01",
            "aep/Ann/6/11/1991-02-01", "aep/Ann/6/11/part/1/1991-02-01",
            "aip/Geo3/40/38/1991-02-01", "aip/Geo3/40/38/part/1/1991-02-01",
            "apgb/Geo3/39-40/14", "apgb/Geo3/39-40/14/section/2",
            "asc/2025/1/2025-03-25", "asc/2025/1/part/1/chapter/2/2025-03-25",
            "mwa/2011/7/section/1", "mwa/2011/7/section/1/2012-11-16",
            "mnia/1974/4/section/5/2006-01-01","ukcm/2025/2/section/1",
            "ssi/2025/281/regulation/1/made", "anaw/2020/3/section/2/welsh",
            "eudr/2020/2089/article/1/2020-12-11"

        );
    }

    static String replaceAknDate(String akn) {
        return akn.replaceFirst("<FRBRdate date=\".+?\" name=\"transform\"/>", "<FRBRdate date=\"1001-01-01-00:00\" name=\"transform\"/>");
    }

    static boolean isFragment(String id) {
//        String uri = "http://www.legislation.gov.uk/" + id;
        Links.Components comps = Links.parse(id);
        return comps.fragment().isPresent();
    }

    @ParameterizedTest
    @MethodSource("provide")
    void akn(String id) throws Exception {
        String clml = TransformHelper.read(id, "xml");
        String actual = transforms.clml2akn(clml);
        String expected = TransformHelper.read(id, "akn.xml");
        actual = replaceAknDate(actual);
        expected = replaceAknDate(expected);
        Assertions.assertEquals(expected, actual);
    }

    static String replaceHtmlDate(String html) {
        return html.replaceFirst("""
            <div property="FRBRdate" typeof="FRBRdate">
             {21}<meta property="date" content="[^"]+">
             {21}<meta property="name" content="transform">""", """
            <div property="FRBRdate" typeof="FRBRdate">
                                 <meta property="date" content="1001-01-01-00:00">
                                 <meta property="name" content="transform">""");
    }

    @ParameterizedTest
    @MethodSource("provide")
    void html(String id) throws Exception {
        String clml = TransformHelper.read(id, "xml");
        String actual = transforms.clml2html(clml, true);
        String expected = TransformHelper.read(id, "html");
        actual = replaceHtmlDate(actual);
        expected = replaceHtmlDate(expected);
        Assertions.assertEquals(expected, actual);
    }

    static final LocalDate CUTOFF = LocalDate.of(2025, 3, 30);

    @ParameterizedTest
    @MethodSource("provide")
    void json(String id) throws Exception {
        String clml = TransformHelper.read(id, "xml");
        String actual;
        if (isFragment(id)) {
            Fragment fragment = transforms.clml2fragment(clml);
            UpToDate.setUpToDate(fragment.meta, CUTOFF);
            actual = MAPPER.writeValueAsString(fragment);
        } else {
            Document document = transforms.clml2document(clml);
            UpToDate.setUpToDate(document.meta, CUTOFF);
            actual = MAPPER.writeValueAsString(document);
        }
        String expected = TransformHelper.read(id, "json");
        Assertions.assertEquals(expected, actual);
    }

}
