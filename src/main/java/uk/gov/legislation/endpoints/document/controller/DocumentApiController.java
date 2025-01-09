package uk.gov.legislation.endpoints.document.controller;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.endpoints.document.service.DocumentService;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.util.Constants;

import java.util.Optional;
import static uk.gov.legislation.transform.Helper.validateLanguage;


/**
 * Controller for document-related API operations.
 */
@RestController
public class DocumentApiController implements DocumentApi {

    private final DocumentService documentService;

    public DocumentApiController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Fetches CLML content based on document details.
     */
    @Override
    public ResponseEntity<String> getDocumentClml(String type, int year, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);

        return documentService.fetchAndTransform(
                clml -> clml,
                type,
                Integer.toString(year),
                number,
                version,
                validatedLanguage
        );
    }

    @Override
    public ResponseEntity<String> getDocumentClml(String type, String monarch, String years, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return documentService.fetchAndTransform(
                clml -> clml,
                type,
                regnalYear,
                number,
                version,
                validatedLanguage
        );
    }

    @Override
    public ResponseEntity<String> getDocumentAkn(String type, int year, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        return getDocumentAkn(type, Integer.toString(year), number, version, validatedLanguage);
    }

    @Override
    public ResponseEntity<String> getDocumentAkn(String type, String monarch, String years, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentAkn(type, regnalYear, number, version, validatedLanguage);
    }

    private ResponseEntity<String> getDocumentAkn(String type, String year, int number, Optional<String> version, String language) {
        return documentService.fetchAndTransform(
                clml -> {
                    try {
                        return documentService.transformToAkn(clml);
                    } catch (SaxonApiException e) {
                        throw new TransformationException(Constants.TRANSFORMATION_FAIL_AKN.getError(), e);
                    }
                },
                type,
                year,
                number,
                version,
                language
        );
    }

    @Override
    public ResponseEntity<String> getDocumentHtml(String type, int year, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        return getDocumentHtml(type, Integer.toString(year), number, version, validatedLanguage);
    }

    @Override
    public ResponseEntity<String> getDocumentHtml(String type, String monarch, String years, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentHtml(type, regnalYear, number, version, validatedLanguage);
    }

    private ResponseEntity<String> getDocumentHtml(String type, String year, int number, Optional<String> version, String language) {

        return documentService.fetchAndTransform(
                clml -> {
                    try {
                        return documentService.transformToHtml(clml);
                    } catch (SaxonApiException e) {
                        throw new TransformationException(Constants.TRANSFORMATION_FAIL_HTML.getError(), e);
                    }
                },
                type,
                year,
                number,
                version,
                language
        );
    }

    @Override
    public ResponseEntity<Response> getDocumentJson(String type, int year, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        return getDocumentJson(type, Integer.toString(year), number, version, validatedLanguage);
    }

    @Override
    public ResponseEntity<Response> getDocumentJson(String type, String monarch, String years, int number, Optional<String> version, String language) {
        String validatedLanguage = validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentJson(type, regnalYear, number, version, validatedLanguage);
    }

    private ResponseEntity<Response> getDocumentJson(String type, String year, int number, Optional<String> version, String language ) {
        return documentService.fetchAndTransform(
                clml -> {
                    try {
                        return documentService.transformToJsonResponse(clml);
                    } catch (SaxonApiException e) {
                        throw new TransformationException(Constants.TRANSFORMATION_FAIL_JSON.getError(), e);
                    }
                },
                type,
                year,
                number,
                version,
                language
        );
    }
}
