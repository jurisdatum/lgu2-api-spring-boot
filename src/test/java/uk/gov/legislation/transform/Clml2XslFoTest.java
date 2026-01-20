package uk.gov.legislation.transform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.Application;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

@SpringBootTest(classes = Application.class)
class Clml2XslFoTest {

    static Stream<String> provide() {
        return Stream.of(
            "ukpga/2023/29/2024-11-01"
        );
    }

    private final Clml2Pdf clml2Pdf;

    @Autowired
    Clml2XslFoTest(Clml2Pdf clml2Pdf) {
        this.clml2Pdf = clml2Pdf;
    }

    @ParameterizedTest
    @MethodSource("provide")
    void x(String id) throws Exception {
        String fo;
        try (InputStream clml = TransformHelper.open(id, "xml")) {
            fo = clml2Pdf.clml2xslFo(clml);
        }
        String expected = TransformHelper.read(id, "fo");
        fo = replaceFoGeneratedDate(fo);
        expected = replaceFoGeneratedDate(expected);
        Assertions.assertEquals(expected, fo);
        final String finalFo = fo;
        Assertions.assertDoesNotThrow(() -> {
            clml2Pdf.xslFo2pdf(finalFo, OutputStream.nullOutputStream());
        });
    }

    private static String replaceFoGeneratedDate(String fo) {
        return fo.replaceAll(
            "Document Generated:\\s*\\d{4}-\\d{2}-\\d{2}",
            "Document Generated: 1001-01-01");
    }

}
