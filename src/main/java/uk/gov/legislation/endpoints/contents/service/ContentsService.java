package uk.gov.legislation.endpoints.contents.service;

import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.converters.TableOfContentsConverter;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.Optional;
import java.util.function.Function;

@Service
public class ContentsService {

    private final Legislation legislationRepository;
    private final Clml2Akn clmlToAknTransformer;
    private final Simplify simplifier;

    public ContentsService(Legislation legislationRepository, Clml2Akn clmlToAknTransformer, Simplify simplifier) {
        this.legislationRepository = legislationRepository;
        this.clmlToAknTransformer = clmlToAknTransformer;
        this.simplifier = simplifier;
    }

    public String transformToAkn(String clmlContent) {
        try {
            XdmNode transformedNode = clmlToAknTransformer.transform(clmlContent);
            return Clml2Akn.serialize(transformedNode);
        } catch (Exception e) {
            throw new TransformationException("Transformation to AKN format failed",e);
        }
    }

    public TableOfContents simplifyToTableOfContents(String clmlContent) {
        try {
            Contents simple = simplifier.contents(clmlContent);
            return TableOfContentsConverter.convert(simple);
        } catch (Exception e) {
            throw new TransformationException("Simplification to JSON format failed",e);
        }
    }

    /* helper */

    public <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version, Function<String, T> transform) {
        Legislation.Response leg = legislationRepository.getTableOfContents(type, year, number, version);
        T body = transform.apply(leg.clml());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
