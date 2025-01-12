package uk.gov.legislation.endpoints.effects;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.params.Number;
import uk.gov.legislation.params.Type;

import java.io.IOException;

@Tag(name = "Effects")
public interface EffectsApi {

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    String atom(
        @RequestParam(required = false) @Type String targetType,
        @RequestParam(required = false) String targetYear,
        @RequestParam(required = false) @Number Integer targetNumber,
        @RequestParam(required = false) String targetTitle,
        @RequestParam(required = false) @Type String sourceType,
        @RequestParam(required = false) String sourceYear,
        @RequestParam(required = false) @Number Integer sourceNumber,
        @RequestParam(required = false) String sourceTitle,
        @RequestParam(required = false, defaultValue = "1") int page
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_JSON_VALUE)
    PageOfEffects json(
        @RequestParam(required = false) @Type String targetType,
        @RequestParam(required = false) String targetYear,
        @RequestParam(required = false) @Number Integer targetNumber,
        @RequestParam(required = false) String targetTitle,
        @RequestParam(required = false) @Type String sourceType,
        @RequestParam(required = false) String sourceYear,
        @RequestParam(required = false) @Number Integer sourceNumber,
        @RequestParam(required = false) String sourceTitle,
        @RequestParam(required = false, defaultValue = "1") int page
    ) throws IOException, InterruptedException, SaxonApiException;

}
