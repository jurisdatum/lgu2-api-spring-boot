package uk.gov.legislation.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.saxon.s9api.XdmNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.simple.effects.UnappliedEffect;
import uk.gov.legislation.transform.simple.Simplify;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class UnappliedEffectsTest {

    private final Simplify simplifier;
    private final Clml2Akn clml2Akn;

    @Autowired
    UnappliedEffectsTest(Simplify simplifier, Clml2Akn transform) {
        this.simplifier = simplifier;
        this.clml2Akn = transform;
    }

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    void raw() throws Exception {
        String clml;
        try (var input = getClass().getResourceAsStream("/ukpga-2000-8-section-91.xml")) {
            Objects.requireNonNull(input);
            clml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String json;
        try (var input = getClass().getResourceAsStream("/ukpga-2000-8-section-91-effects-raw.json")) {
            Objects.requireNonNull(input);
            json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        List<UnappliedEffect> effects = simplifier.contents(clml).meta().unfilteredEffects();
        String actual = mapper.writeValueAsString(effects);
        Assertions.assertEquals(json, actual);
    }

    @Test
    void filtered() throws Exception {
        String clml;
        try (var input = getClass().getResourceAsStream("/ukpga-2000-8-section-91.xml")) {
            Objects.requireNonNull(input);
            clml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String json;
        try (var input = getClass().getResourceAsStream("/ukpga-2000-8-section-91-effects-filtered.json")) {
            Objects.requireNonNull(input);
            json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        XdmNode akn = clml2Akn.transform(clml);
        List<Effect> effects = AkN.Meta.extract(akn).unappliedEffects();
        String actual = mapper.writeValueAsString(effects);
        Assertions.assertEquals(json, actual);
    }

}
