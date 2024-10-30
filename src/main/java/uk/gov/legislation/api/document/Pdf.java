package uk.gov.legislation.api.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Simplify;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@RestController
@Tag(name = "Documents")
@SuppressWarnings("unused")
public class Pdf {

    @Autowired
    private Legislation db;

    @GetMapping(value = "/pdf/{type}/{year}/{number}")
    @Operation(summary = "an original PDF version")
    public ResponseEntity<Void> pdf(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam(required = false) String version) throws Exception {
        Optional<String> pdf = getPdfUrl(type, year, number, version);
        if (pdf.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        URI uri = URI.create(pdf.get());
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(uri).build();
    }

    @GetMapping(value = "/thumbnail/{type}/{year}/{number}")
    @Operation(summary = "thumbnail of PDF")
    public ResponseEntity<Void> thumbnail(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam(required = false) String version) throws Exception {
        Optional<String> pdf = getPdfUrl(type, year, number, version);
        if (pdf.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        String thumbnail = convertToThumbnailUrl(pdf.get());
        URI uri = URI.create(thumbnail);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(uri).build();
    }

    @Autowired
    private Simplify simplifier;

    private Optional<String> getPdfUrl(String type, int year, int number, String version) throws IOException, InterruptedException, SaxonApiException {
        String clml;
        try {
            clml = db.getTableOfContents(type, year, number, version == null ? Optional.empty() : Optional.of(version));
        } catch (NoDocumentException e) {
            return Optional.empty();
        }
        Contents toc = simplifier.contents(clml);
        return toc.meta().pdfFormatUri();
    }

    private String convertToThumbnailUrl(String url) {
        return url.replaceFirst("/pdfs/", "/images/")
                .replaceFirst("\\.pdf", ".jpg");
    }

}
