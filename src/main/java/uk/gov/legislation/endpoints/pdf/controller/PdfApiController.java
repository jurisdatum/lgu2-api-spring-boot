package uk.gov.legislation.endpoints.pdf.controller;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.pdf.api.PdfApi;
import uk.gov.legislation.endpoints.pdf.service.PdfService;
import uk.gov.legislation.exceptions.DocumentFetchException;
import java.io.IOException;

@RestController
public class PdfApiController implements PdfApi {

    private final PdfService pdfService;

    public PdfApiController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Override
    public ResponseEntity<Void> getPdf(String type, int year, int number, String version) {
        return handlePdfRequest(type, Integer.toString(year), number, version, false);
    }

    @Override
    public ResponseEntity<Void> getPdfWithRegnalYear(String type, String monarch, String years, int number, String version) {
        String regnalYear = String.join("/", monarch, years);
        return handlePdfRequest(type, regnalYear, number, version, false);
    }

    @Override
    public ResponseEntity<Void> getPdfThumbnail(String type, int year, int number, String version) {
        return handlePdfRequest(type, Integer.toString(year), number, version, true);
    }

    @Override
    public ResponseEntity<Void> getPdfThumbnailWithRegnalYear(String type, String monarch, String years, int number, String version) {
        String regnalYear = String.join("/", monarch, years);
        return handlePdfRequest(type, regnalYear, number, version, true);
    }

    private ResponseEntity<Void> handlePdfRequest(String type, String yearOrRegnal, int number, String version, boolean isThumbnail) {
        try {
            return pdfService.fetchPdfUrl(type, yearOrRegnal, number, version)
                    .map(url -> isThumbnail ? pdfService.convertToThumbnailUrl(url) : url)
                    .map(pdfService::buildRedirectResponse)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IOException | SaxonApiException e) {
            throw new DocumentFetchException("Failed to process the document.", e);
        }
    }
}

