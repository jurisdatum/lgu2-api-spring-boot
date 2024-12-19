package uk.gov.legislation.endpoints.contents.service;

import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.endpoints.document.TableOfContents;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.ClMl2Akn;
import uk.gov.legislation.transform.simple.Simplify;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ContentsService {

    private final Legislation legislationRepository;
    private final ClMl2Akn clMlToAknTransformer;
    private final Simplify simplifier;

    public ContentsService(Legislation legislationRepository, ClMl2Akn clMlToAknTransformer, Simplify simplifier) {
        this.legislationRepository = legislationRepository;
        this.clMlToAknTransformer = clMlToAknTransformer;
        this.simplifier = simplifier;
    }

    public String transformToAkn(String clMlContent) {
        try {
            XdmNode transformedNode = clMlToAknTransformer.transform(clMlContent);
            return ClMl2Akn.serialize(transformedNode);
        } catch (Exception e) {
            throw new TransformationException("Transformation to AKN format failed",e);
        }
    }

    public TableOfContents simplifyToTableOfContents(String clMlContent) {
        try {
            return simplifier.contents(clMlContent);
        } catch (Exception e) {
            throw new TransformationException("Simplification to JSON format failed",e);
        }
    }

    /* helper */

    public <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version, Function<String, T> transform) {
        Legislation.Response leg = legislationRepository.getTableOfContents(type, year, number, version);
        T body = transform.apply(leg.clMl());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        // ok for headers to be null
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
