package uk.gov.legislation.api.document;

import net.sf.saxon.s9api.XdmNode;
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
public class Document {

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.APPLICATION_XML_VALUE)
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws IOException, InterruptedException {
        String clml;
        try {
            clml = Legislation.getDocument(type, year, number, version);
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
        String akn = Clml2Akn.serialize(akn1);
        return akn;
    }

    final Akn2Html akn2html = Transforms.akn2html();

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.TEXT_HTML_VALUE)
    public String html(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode akn = clml2akn.transform(clml);
        String html = akn2html.transform(akn);
        return html;
    }

    static record Response(AkN.Meta meta, String html) { }

    @GetMapping(value = "/document/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode akn = clml2akn.transform(clml);
        String html = akn2html.transform(akn);
        AkN.Meta meta = AkN.Meta.extract(akn);
        Response response = new Response(meta, html);
        return response;
    }

}
