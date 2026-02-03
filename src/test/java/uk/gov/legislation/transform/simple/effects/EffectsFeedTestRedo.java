package uk.gov.legislation.transform.simple.effects;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.transform.simple.UnappliedEffectsTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static uk.gov.legislation.transform.simple.UnappliedEffectsHelper.read;

public class EffectsFeedTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        EffectsSimplifier simplifier = ctx.getBean(EffectsSimplifier.class);
        String atom = read("/effects/effects.feed");

        simplify(simplifier, atom);
        parse(simplifier, atom);
        convert(simplifier, atom);

        SpringApplication.exit(ctx);
    }

    static void simplify(EffectsSimplifier simplifier, String atom) throws Exception {
        String actual = simplifier.simpify(atom);
        String expected = read("/effects/effects-simple.feed");
        if (actual.equals(expected))
            return;
        System.out.println("redoing effects-simple.feed");
        writeResource("/effects/effects-simple.feed", actual);
    }

    static void parse(EffectsSimplifier simplifier, String atom) throws Exception {
        Page feed = simplifier.parse(atom);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(feed);
        String expected = read("/effects/effects.json");
        if (actual.equals(expected))
            return;
        System.out.println("redoing effects.json");
        writeResource("/effects/effects.json", actual);
    }

    static void convert(EffectsSimplifier simplifier, String atom) throws Exception {
        Page feed = simplifier.parse(atom);
        PageOfEffects converted = EffectsFeedConverter.convert(feed);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(converted);
        String expected = read("/effects/effects-converted.json");
        if (actual.equals(expected))
            return;
        System.out.println("redoing effects-converted.json");
        writeResource("/effects/effects-converted.json", actual);
    }

    static void writeResource(String resource, String content) throws Exception {
        Path path = Path.of("src/test/resources", resource.substring(1));
        Files.writeString(path, content);
    }
}
