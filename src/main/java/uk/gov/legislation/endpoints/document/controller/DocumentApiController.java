package uk.gov.legislation.endpoints.document.controller;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.Metadata;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for document-related API operations.
 */
@RestController
public class DocumentApiController implements DocumentApi {
    public static final String DOCUMENT_NOT_FOUND_MESSAGE = "Document not found for type: %s, year: %d, number: %d";


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
    private Optional<String> fetchClmlContent(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(legislationService.getDocument(type, year, number, version));
    }

    @Override
    public ResponseEntity<String> getDocumentClml(String type, int year, int number, Optional<String> version)
            throws NoDocumentException, IOException, InterruptedException {
        return fetchClmlContent(type, year, number, version)
                .map(clml -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(clml))
                .orElseThrow(() -> new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }

    @Override
    public ResponseEntity<String> getDocumentAkn(String type, int year, int number, Optional<String> version)
            throws NoDocumentException, SaxonApiException, IOException, InterruptedException {
        String clml = fetchClmlContent(type, year, number, version)
                .orElseThrow(() -> new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
        XdmNode aknNode = clmlToAknTransformer.transform(clml);
        String serializedAkn = Clml2Akn.serialize(aknNode);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/akn+xml"))
                .body(serializedAkn);
    }

    @Override
    public ResponseEntity<String> getDocumentHtml(String type, int year, int number, Optional<String> version)
            throws NoDocumentException, SaxonApiException, IOException, InterruptedException {
        String clml = fetchClmlContent(type, year, number, version)
                .orElseThrow(() -> new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
        XdmNode aknNode = clmlToAknTransformer.transform(clml);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, true);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }

    @Override
    public ResponseEntity<Response> getDocumentJson(String type, int year, int number, Optional<String> version)
            throws SaxonApiException, NoDocumentException, IOException, InterruptedException {
        String clml = fetchClmlContent(type, year, number, version)
                .orElseThrow(() -> new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
        XdmNode aknNode = clmlToAknTransformer.transform(clml);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
        Metadata metadata = AkN.Meta.extract(aknNode);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Response(metadata, htmlContent));
    }
}
