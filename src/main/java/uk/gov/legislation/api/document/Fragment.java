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
import uk.gov.legislation.transform.Clml2Akn;

import java.io.IOException;
import java.util.Optional;

@RestController
public class Fragment {

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.APPLICATION_XML_VALUE)
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws IOException, InterruptedException {
        String clml;
        try {
            clml = Legislation.getDocumentSection(type, year, number, section, version);
        } catch (NoDocumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clml;
    }

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = "application/akn+xml")
    public String akn(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn1 = Transforms.clml2akn().transform(clml);
        String akn = Clml2Akn.serialize(akn1);
        return akn;
    }

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.TEXT_HTML_VALUE)
    public String html(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn = Transforms.clml2akn().transform(clml);
        String html = Transforms.akn2html().transform(akn, true);
        return html;
    }

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Document.Response json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @PathVariable String section, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, section, version);
        XdmNode akn = Transforms.clml2akn().transform(clml);
        String html = Transforms.akn2html().transform(akn, false);
        AkN.Meta meta = AkN.Meta.extract(akn);
        Document.Response response = new Document.Response(meta, html);
        return response;
    }

}
