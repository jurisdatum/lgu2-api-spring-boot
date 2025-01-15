package uk.gov.legislation.endpoints.pdf.service;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.transform.simple.Simplify;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Service
public class PdfService {

    private final Legislation legislationService;
    private final Simplify simplifier;

    public PdfService(Legislation legislationService, Simplify simplifier) {
        this.legislationService = legislationService;
        this.simplifier = simplifier;
    }

    /**
     * Fetches the URL of a PDF or its thumbnail based on the input parameters.
     */
    public Optional<String> fetchPdfUrl(String type, String yearOrRegnal, int number, String version) throws IOException, SaxonApiException {
        String clml = legislationService.getTableOfContents(type,yearOrRegnal, number, Optional.ofNullable(version)).clml();
        return simplifier.contents(clml).meta.pdfFormatUri();
    }

    /**
     * Converts a PDF URL to its corresponding thumbnail URL.
     *
     * @param url The original PDF URL
     * @return The thumbnail URL
     */
    public String convertToThumbnailUrl(String url) {
        return url
                .replaceFirst("/pdfs/", "/images/")
                .replaceFirst("\\.pdf", ".jpg");
    }

    /**
     * Builds a redirect response for a given URL.
     *
     * @param url The URL to redirect to
     * @return ResponseEntity with a redirect status
     */
    public ResponseEntity<Void> buildRedirectResponse(String url) {
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(url))
                .build();
    }
}
