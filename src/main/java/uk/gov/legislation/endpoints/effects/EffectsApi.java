package uk.gov.legislation.endpoints.effects;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.saxon.s9api.SaxonApiException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.parameters.*;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.data.marklogic.changes.Parameters;

import java.io.IOException;

@Tag(name = "Effects")
public interface EffectsApi {

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    String atom(@ParameterObject EffectsParameters param
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_JSON_VALUE)
    PageOfEffects json(@ParameterObject EffectsParameters param
    ) throws IOException, InterruptedException, SaxonApiException;

}
