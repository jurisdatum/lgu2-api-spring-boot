package uk.gov.legislation.endpoints.contents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.endpoints.contents.api.ContentsApi;
import uk.gov.legislation.endpoints.contents.service.ContentsService;

import java.util.Optional;
import java.util.function.Function;

import static uk.gov.legislation.endpoints.ParameterValidator.validateLanguage;


/**
 * REST Controller for managing API endpoints related to document contents.
 * This controller provides methods to retrieve document contents in various formats: CLML, AKN, and JSON.
 */
@RestController
public class ContentsApiController implements ContentsApi {

    private final ContentsService contentsService;
    /**
     * Constructor that initializes the ContentsApiController with the required contents service.
     *
     * @param contentsService  Service that provides methods to fetch and transform document contents
     */
    public ContentsApiController(ContentsService contentsService) {
        this.contentsService = contentsService;
    }

    /**
     * Retrieves the document contents in CLML (Custom Markup Language) XML format.
     *
     * @param type     The document type identifier
     * @param year     The publication year of the document
     * @param number   The document number
     * @param version  Optional version of the document to retrieve
     * @return ResponseEntity containing CLML XML if found, or throws NoDocumentException if the document is not found
     */
    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, int year, int number, Optional<String> version, String language) {
        validateLanguage(language);
        return getDocumentContentsClml(type, Integer.toString(year), number, version, language);
    }
    /**
     * @param monarch   An abbreviation of the monarch, relative to which the year is given, e.g., 'Vict'
     * @param years     A year or range of years, relative to the monarch, e.g., '1' or '1-2'
     */
    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, String monarch, String years, int number, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentContentsClml(type, regnalYear, number, version, language);
    }
    private ResponseEntity<String> getDocumentContentsClml(String type, String year, int number, Optional<String> version, String validatedLanguage) {
        return contentsService.fetchAndTransform(type, year, number, version, Function.identity(), validatedLanguage);
    }

    /**
     * Retrieves the document contents in AKN (Akoma Ntoso) XML format.
     * Converts the CLML format to AKN XML format using the contents service.
     */
    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, int year, int number, Optional<String> version, String language) {
        validateLanguage(language);
        return getDocumentContentsAkn(type, Integer.toString(year), number, version, language);
    }
    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, String monarch, String years, int number, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentContentsAkn(type, regnalYear, number, version, language);
    }
    private ResponseEntity<String> getDocumentContentsAkn(String type, String year, int number, Optional<String> version, String validatedLanguage) {
        return contentsService.fetchAndTransform(type, year, number, version, contentsService::transformToAkn, validatedLanguage);
    }

    /**
     * Retrieves the document contents in a simplified JSON format.
     * Converts the CLML format to a TableOfContents object using the contents service.
     */
    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, int year, int number, Optional<String> version, String language) {
        validateLanguage(language);
        return getDocumentContentsJson(type, Integer.toString(year), number, version, language);
    }
    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, String monarch, String years, int number, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getDocumentContentsJson(type, regnalYear, number, version, language);
    }
    private ResponseEntity<TableOfContents> getDocumentContentsJson(String type, String year, int number, Optional<String> version, String validatedLanguage) {
        return contentsService.fetchAndTransform(type, year, number, version, contentsService::simplifyToTableOfContents, validatedLanguage);
    }

}
