package uk.gov.legislation.endpoints.effects;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import net.sf.saxon.s9api.SaxonApiException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.legislation.api.responses.PageOfEffects;

@Tag(name = "Effects")
public interface EffectsApi {

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    String atom(@ParameterObject EffectsParameters param) throws IOException, InterruptedException;

    @GetMapping(value = "/effects", produces = MediaType.APPLICATION_JSON_VALUE)
    PageOfEffects json(@ParameterObject EffectsParameters param)
            throws IOException, InterruptedException, SaxonApiException;
}
