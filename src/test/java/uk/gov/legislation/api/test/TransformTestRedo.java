package uk.gov.legislation.api.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.transform.Transforms;
import uk.gov.legislation.util.UpToDate;

import java.time.ZonedDateTime;

import static uk.gov.legislation.api.test.TransformHelper.MAPPER;
import static uk.gov.legislation.api.test.TransformTest.CUTOFF;

public class TransformTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        for (String id : TransformTest.provide().toList()) {
            akn(ctx, id);
            html(ctx, id);
            json(ctx, id);
        }
        SpringApplication.exit(ctx);
    }

    @FunctionalInterface
    public interface ITransform {
        String apply(String clml) throws Exception;
    }

    static void akn(ApplicationContext ctx, String id) throws Exception {
        Transforms transforms = ctx.getBean(Transforms.class);
        redo(id, transforms::clml2akn, "akn.xml");
    }

    static void html(ApplicationContext ctx, String id) throws Exception {
        Transforms transforms = ctx.getBean(Transforms.class);
        ITransform clml2html = (String clml) -> transforms.clml2html(clml, true);
        redo(id, clml2html, "html");
    }

    static void json(ApplicationContext ctx, String id) throws Exception {
        Transforms transforms = ctx.getBean(Transforms.class);
        ITransform transform = TransformTest.isFragment(id) ?
            (String clml) -> {
                Fragment fragment = transforms.clml2fragment(clml);
                UpToDate.setUpToDate(fragment.meta, CUTOFF);
                return MAPPER.writeValueAsString(fragment);
            } :
            (String clml) -> {
                Document document = transforms.clml2document(clml);
                UpToDate.setUpToDate(document.meta, CUTOFF);
                return MAPPER.writeValueAsString(document);
            } ;
        redo(id, transform, "json");
    }

    static void redo(String id, ITransform transform, String format) throws Exception {
        String clml = TransformHelper.read(id, "xml");
        String actual = transform.apply(clml);
        String expected;
        try {
            expected = TransformHelper.read(id, format);
        } catch (NullPointerException e) {
            expected = ZonedDateTime.now().toString();
        }
        if (actual.equals(expected)) {
            System.out.println("skipping " + id);
            return;
        }
        System.out.println("redoing " + id);
        TransformHelper.write(id, format, actual);
    }

}
