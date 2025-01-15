package uk.gov.legislation.api.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.endpoints.Application;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;
import uk.gov.legislation.transform.simple.effects.Page;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.legislation.api.test.TransformTest.read;

@SpringBootTest(classes = Application.class)
class EffectsFeedTest {

    private final EffectsSimplifier simplifier;

    @Autowired
    EffectsFeedTest(EffectsSimplifier simplifier) {
        this.simplifier = simplifier;
    }

    @Test
    void simplify() throws IOException, SaxonApiException {
        String atom = read("/effects/effects.feed");
        String actual = simplifier.simpify(atom);
        String expected =  read("/effects/effects-simple.feed");
        assertEquals(expected, actual);
    }

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    void parse() throws IOException, SaxonApiException {
        String atom = read("/effects/effects.feed");
        Page feed = simplifier.parse(atom);
        String actual = mapper.writeValueAsString(feed);
        String expected = read("/effects/effects.json");
        assertEquals(expected, actual);
    }

    @Test
    void convert() throws IOException, SaxonApiException {
        String atom = read("/effects/effects.feed");
        Page feed = simplifier.parse(atom);
        PageOfEffects converted = EffectsFeedConverter.convert(feed);
        String actual = mapper.writeValueAsString(converted);
        String expected = read("/effects/effects-converted.json");
        assertEquals(expected, actual);
    }

}
