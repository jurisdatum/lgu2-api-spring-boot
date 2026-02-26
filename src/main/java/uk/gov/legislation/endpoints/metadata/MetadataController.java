package uk.gov.legislation.endpoints.metadata;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.ExtendedMetadata;
import uk.gov.legislation.converters.ExtendedMetadataConverter;
import uk.gov.legislation.converters.UnappliedEffectsFetcher;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.Locale;
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class MetadataController implements MetadataApi {

    private final Legislation marklogic;

    private final Simplify simplifier;

    private final UnappliedEffectsFetcher effectsFetcher;

    public MetadataController(Legislation marklogic, Simplify simplify, UnappliedEffectsFetcher effectsFetcher) {
        this.marklogic = marklogic;
        this.simplifier = simplify;
        this.effectsFetcher = effectsFetcher;
    }

    @Override
    public ResponseEntity<String> xml(String type, String year, int number, Locale locale) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getMetadata(type, year, number, Optional.of(language));
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(leg.clml());
    }

    @Override
    public ResponseEntity<ExtendedMetadata> json(String type, String year, int number, Locale locale) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getMetadata(type, year, number, Optional.of(language));
        Metadata simple = simplifier.extractDocumentMetadata(leg.clml());
        effectsFetcher.fetchIfNeeded(simple);
        ExtendedMetadata metadata = ExtendedMetadataConverter.convert(simple);
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(metadata);

    }

}
