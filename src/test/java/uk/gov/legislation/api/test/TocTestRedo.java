package uk.gov.legislation.api.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.converters.TableOfContentsConverter;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.util.UpToDate;

import java.time.ZonedDateTime;

import static uk.gov.legislation.api.test.TocTest.provide;
import static uk.gov.legislation.api.test.TransformHelper.*;
import static uk.gov.legislation.api.test.TransformTest.CUTOFF;

public class TocTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        for (String id : provide().toList())
            redo(ctx, id);
        SpringApplication.exit(ctx);
    }

    static void redo(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, "xml");
        Contents simple = simplifier.contents(clml);
        TableOfContents toc = TableOfContentsConverter.convert(simple);
        UpToDate.setUpToDate(toc.meta, CUTOFF);
        String actual = MAPPER.writeValueAsString(toc);
        String expected;
        try {
            expected = read(id, "json");
        } catch (NullPointerException e) {
            expected = ZonedDateTime.now().toString();
        }
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, "json", actual);
    }

}
