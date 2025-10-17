package uk.gov.legislation.api.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.Effects;
import uk.gov.legislation.util.EffectsComparator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.legislation.api.test.TransformTest.isFragment;
import static uk.gov.legislation.api.test.UnappliedEffectsHelper.read;
import static uk.gov.legislation.api.test.UnappliedEffectsHelper.write;
import static uk.gov.legislation.api.test.UnappliedEffectsTest.indent;
import static uk.gov.legislation.api.test.UnappliedEffectsTest.mapper;

public class UnappliedEffectsTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        for (String id : UnappliedEffectsTest.provide().toList()) {
            simplify(ctx, id);
            raw(ctx, id);
            sorted(ctx, id);
            filtered(ctx, id);
            converted(ctx, id);
        }
        SpringApplication.exit(ctx);
    }

    static void simplify(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, ".xml");
        Simplify.Parameters parameters = new Simplify.Parameters(isFragment(id), false);
        String actual = indent(simplifier.transform(clml, parameters));
        String ext = "-simplified.xml";
        String expected = read(id, ext);
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, ext, actual);
    }

    static void raw(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, ".xml");
        Metadata meta = isFragment(id) ? simplifier.extractFragmentMetadata(clml) : simplifier.extractDocumentMetadata(clml);
        String actual = mapper.writeValueAsString(meta.rawEffects);
        String expected = read(id, "-effects-raw.json");
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, "-effects-raw.json", actual);
    }

    static void sorted(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, ".xml");
        Metadata meta = isFragment(id) ? simplifier.extractFragmentMetadata(clml) : simplifier.extractDocumentMetadata(clml);
        List<Effect> effects = meta.rawEffects.stream().sorted(EffectsComparator.INSTANCE).toList();
        String actual = mapper.writeValueAsString(effects);
        String ext = "-effects-sorted.json";
        String expected = read(id, ext);
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, ext, actual);
    }

    static void filtered(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, ".xml");
        Metadata meta;
        List<uk.gov.legislation.transform.simple.effects.Effect> effects;
        if (isFragment(id)) {
            meta = simplifier.extractFragmentMetadata(clml);
            Set<String> ids = meta.ancestors().stream().map(l -> l.id).collect(Collectors.toSet());
            meta.descendants().stream().map(l -> l.id).forEach(ids::add);
            effects = Effects.removeThoseWithNoRelevantSection(meta.rawEffects, ids, true);
        } else {
            meta = simplifier.extractDocumentMetadata(clml);
            effects = meta.rawEffects;
        }
        String actual = mapper.writeValueAsString(effects);
        String ext = "-effects-filtered.json";
        String expected = read(id, ext);
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, ext, actual);
    }

    static void converted(ApplicationContext ctx, String id) throws Exception {
        Simplify simplifier = ctx.getBean(Simplify.class);
        String clml = read(id, ".xml");
        Metadata meta = isFragment(id) ? simplifier.extractFragmentMetadata(clml) : simplifier.extractDocumentMetadata(clml);
        List<uk.gov.legislation.api.responses.Effect> effects = EffectsFeedConverter.convertEffects(meta.rawEffects);
        String actual = mapper.writeValueAsString(effects);
        String ext = "-effects-converted.json";
        String expected = read(id, ext);
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        write(id, ext, actual);
    }

}
