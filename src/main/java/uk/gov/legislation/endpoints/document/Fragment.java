package uk.gov.legislation.endpoints.document;

import io.swagger.v3.oas.annotations.Operation;
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
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;

import java.io.IOException;
import java.util.Optional;

@RestController
@Tag(name = "Documents")
@SuppressWarnings("unused")
public class Fragment {

    @Autowired
    private Legislation db;

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "part of a document")
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws IOException, InterruptedException {
        String clml;
        try {
            clml = db.getDocumentSection(type, year, number, section, version);
        } catch (NoDocumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clml;
    }

    @Autowired
    private Clml2Akn clml2akn;

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = "application/akn+xml")
    public String akn(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn1 = clml2akn.transform(clml);
        return Clml2Akn.serialize(akn1);
    }

    @Autowired
    private Akn2Html akn2html;

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.TEXT_HTML_VALUE)
    public String html(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn = clml2akn.transform(clml);
        return akn2html.transform(akn, true);
    }

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentApi.Response json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn = clml2akn.transform(clml);
        String html = akn2html.transform(akn, false);
        AkN.Meta meta = AkN.Meta.extract(akn);
        return new DocumentApi.Response(meta, html);
    }

}