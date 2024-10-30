package uk.gov.legislation.api.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;

import java.io.IOException;
import java.util.Optional;

@RestController
@Tag(name = "Documents", description = "individual documents")
@SuppressWarnings("unused")
public class Document {

    @Autowired
    private Legislation db;

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "the content of a document", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = uk.gov.legislation.api.types.Legislation.class))
        )
    })
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(hidden = true))) // to prevent creation of seprate response based on method return type
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws IOException, InterruptedException {
        String clml;
        try {
            clml = db.getDocument(type, year, number, version);
        } catch (NoDocumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clml;
    }

    final Clml2Akn clml2akn = Transforms.clml2akn();

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/akn+xml")
    public String akn(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode akn1 = clml2akn.transform(clml);
        return Clml2Akn.serialize(akn1);
    }

    final Akn2Html akn2html = Transforms.akn2html();

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.TEXT_HTML_VALUE)
    public String html(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode akn = clml2akn.transform(clml);
        return akn2html.transform(akn, true);
    }

    public record Response(Metadata meta, String html) { }

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode akn = clml2akn.transform(clml);
        String html = akn2html.transform(akn, false);
        Metadata meta = AkN.Meta.extract(akn);
        return new Response(meta, html);
    }

}
