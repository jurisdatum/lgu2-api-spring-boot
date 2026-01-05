package uk.gov.legislation.transform.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.converters.FragmentMetadataConverter;

import static uk.gov.legislation.transform.simple.UnappliedEffectsHelper.read;
import static uk.gov.legislation.transform.simple.UnappliedEffectsHelper.write;
import static uk.gov.legislation.transform.simple.UnappliedEffectsTest.mapper;
import static uk.gov.legislation.transform.simple.UpToDateTest.provide;

public class UpToDateTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        Simplify simplifier = ctx.getBean(Simplify.class);
        for (String id : provide().toList()) {
            String resource = "/" + id.replace('/', '_') + "/clml.xml";
            String clml = read(resource);
            Metadata simple = simplifier.extractFragmentMetadata(clml);
            FragmentMetadata meta = FragmentMetadataConverter.convert(simple);
            String actual = mapper.writeValueAsString(meta);
            String expectedResource = "/" + id.replace('/', '_') + "/meta.json";
            String expected = read(expectedResource);
            if (actual.equals(expected))
                continue;
            System.out.println("redoing " + id);
            write(expectedResource, actual);
        }
        SpringApplication.exit(ctx);
    }

}
