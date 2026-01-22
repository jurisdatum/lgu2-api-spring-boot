package uk.gov.legislation.transform;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;

import java.io.IOException;
import java.io.InputStream;

public class Clml2XslFoTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        try {
            for (String id : Clml2XslFoTest.provide().toList()) {
                redo(ctx, id);
            }
        } finally {
            SpringApplication.exit(ctx);
        }
    }

    private static void redo(ApplicationContext ctx, String id) throws IOException, SaxonApiException {
        final String format = "fo";
        Clml2Pdf transform = ctx.getBean(Clml2Pdf.class);
        String actual;
        try (InputStream clml = TransformHelper.open(id, "xml")) {
            actual = transform.clml2xslFo(clml);
        }
        String expected;
        try {
            expected = TransformHelper.read(id, format);
        } catch (NullPointerException e) {
            expected = null;
        }
        if (actual.equals(expected)) {
            System.out.println("skipping " + id);
            return;
        }
        System.out.println("redoing " + id);
        TransformHelper.write(id, format, actual);
    }

}
