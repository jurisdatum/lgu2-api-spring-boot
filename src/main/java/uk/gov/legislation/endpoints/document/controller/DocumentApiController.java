package uk.gov.legislation.endpoints.document.controller;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.Metadata;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.util.Constants;

import java.util.Optional;
import java.util.function.Function;


/**
 * Controller for document-related API operations.
 */
@RestController
public class DocumentApiController implements DocumentApi {

    private final Legislation legislationService;
    private final Clml2Akn clmlToAknTransformer;
    private final Akn2Html aknToHtmlTransformer;

    /**
     * Constructs a new DocumentApiController with dependencies.
     *
     * @param legislationService the legislation service
     * @param clmlToAknTransformer the CLML to AKN transformer
     * @param aknToHtmlTransformer the AKN to HTML transformer
     */

    public DocumentApiController(Legislation legislationService, Clml2Akn clmlToAknTransformer, Akn2Html aknToHtmlTransformer) {
        this.legislationService = legislationService;
        this.clmlToAknTransformer = clmlToAknTransformer;
        this.aknToHtmlTransformer = aknToHtmlTransformer;
    }

    /**
     * Fetches CLML content based on document details.
     */
    private Optional<String> fetchClmlContent(String type, String year, int number, Optional<String> version) {
        return Optional.ofNullable(legislationService.getDocument(type, year, number, version));
    }

    private <T> ResponseEntity<T> handleTransformation(Function <String, T> transformationFunction, String type, String year, int number, Optional<String> version, String errorMessage) {
        return fetchClmlContent(type, year, number, version)
                .map(transformationFunction)
                .map(result -> ResponseEntity.ok().body(result))
                .orElseThrow(() -> new NoDocumentException(String.format(errorMessage, type, year, number)));
    }

    @Override
    public ResponseEntity<String> getDocumentClml(String type, int year, int number, Optional<String> version) {
        return handleTransformation(clml -> clml, type, Integer.toString(year), number, version, Constants.DOCUMENT_NOT_FOUND.getError());
    }
    @Override
    public ResponseEntity<String> getDocumentClml(String type, String monarch, String years, int number, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return handleTransformation(clml -> clml, type, regnalYear, number, version, Constants.DOCUMENT_NOT_FOUND.getError());
    }


    @Override
    public ResponseEntity<String> getDocumentAkn(String type, int year, int number, Optional<String> version) {
        return getDocumentAkn(type, Integer.toString(year), number, version);
    }
    @Override
    public ResponseEntity<String> getDocumentAkn(String type, String monarch, String years, int number, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getDocumentAkn(type, regnalYear, number, version);
    }
    private ResponseEntity<String> getDocumentAkn(String type, String year, int number, Optional<String> version) {
        return handleTransformation(clml -> {
            try {
                XdmNode aknNode = clmlToAknTransformer.transform(clml);
                return Clml2Akn.serialize(aknNode);
            } catch (SaxonApiException e) {
                throw new TransformationException(Constants.TRANSFORMATION_FAIL_AKN.getError(), e);
            }
        }, type, year, number, version, Constants.DOCUMENT_NOT_FOUND.getError());
    }


    @Override
    public ResponseEntity<String> getDocumentHtml(String type, int year, int number, Optional<String> version) {
        return getDocumentHtml(type, Integer.toString(year), number, version);
    }
    @Override
    public ResponseEntity<String> getDocumentHtml(String type, String monarch, String years, int number, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getDocumentHtml(type, regnalYear, number, version);
    }
    private ResponseEntity<String> getDocumentHtml(String type, String year, int number, Optional<String> version) {
        return handleTransformation(clml -> {
            try {
                XdmNode aknNode = clmlToAknTransformer.transform(clml);
                return aknToHtmlTransformer.transform(aknNode, true);
            } catch (SaxonApiException e) {
                throw new TransformationException(Constants.TRANSFORMATION_FAIL_HTML.getError(), e);
            }
        }, type, year, number, version, Constants.DOCUMENT_NOT_FOUND.getError());
    }


    @Override
    public ResponseEntity<Response> getDocumentJson(String type, int year, int number, Optional<String> version) {
        return getDocumentJson(type, Integer.toString(year), number, version);
    }
    @Override
    public ResponseEntity<Response> getDocumentJson(String type, String monarch, String years, int number, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getDocumentJson(type, regnalYear, number, version);
    }
    private ResponseEntity<Response> getDocumentJson(String type, String year, int number, Optional<String> version) {
        return handleTransformation(clml -> {
            try {
                XdmNode aknNode = clmlToAknTransformer.transform(clml);
                String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
                Metadata metadata = AkN.Meta.extract(aknNode);
                return new Response(metadata, htmlContent);
            } catch (SaxonApiException e) {
                throw new TransformationException(Constants.TRANSFORMATION_FAIL_JSON.getError(), e);
            }
        }, type, year, number, version, Constants.DOCUMENT_NOT_FOUND.getError());
    }

}
