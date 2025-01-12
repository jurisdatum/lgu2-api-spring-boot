package uk.gov.legislation.endpoints.document.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.endpoints.document.Metadata;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Helper;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.Optional;
import java.util.function.Function;

@Service
public class DocumentService {

    private final Legislation legislationService;
    private final Clml2Akn clmlToAknTransformer;
    private final Akn2Html aknToHtmlTransformer;
    private final Simplify simplifier;

    public DocumentService(Legislation legislationService, Clml2Akn clmlToAknTransformer, Akn2Html aknToHtmlTransformer, Simplify simplifier) {
        this.legislationService = legislationService;
        this.clmlToAknTransformer = clmlToAknTransformer;
        this.aknToHtmlTransformer = aknToHtmlTransformer;
        this.simplifier = simplifier;
    }

    private final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public String transformToAkn(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return Clml2Akn.serialize(aknNode);
    }

    public String transformToHtml(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return aknToHtmlTransformer.transform(aknNode, true);
    }

    public DocumentApi.Response transformToJsonResponse(String clmlContent) throws SaxonApiException {
        long start = System.currentTimeMillis();
        XdmNode clmlDoc = Helper.parse(clmlContent);
        XdmNode aknNode = clmlToAknTransformer.transform(clmlDoc);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
        Metadata metadata;
        try {
            metadata = simplifier.metadata(clmlDoc);
        } catch (JsonProcessingException e) {
            throw new TransformationException("Simplification to JSON format failed",e);
        }
        long end = System.currentTimeMillis();
        logger.debug("It took {} miliseconds to convert CLML to JSON", end - start);
        return new DocumentApi.Response(metadata, htmlContent);
    }

    /* helper */

    public <T> ResponseEntity <T> fetchAndTransform(
            Function <String, T> transformationFunction,
            String type,
            String year,
            int number,
            Optional<String> version,
            String language) {
        Legislation.Response leg = legislationService.getDocument(type, year, number, version, Optional.of(language));
        T body = transformationFunction.apply(leg.clml());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
