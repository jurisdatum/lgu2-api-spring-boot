package uk.gov.legislation.api.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import uk.gov.legislation.transform.Clml2Akn;

import java.util.Optional;

@RestController
@Tag(name = "Documents")
public class Contents {

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "table of contents")
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml;
        try {
            clml = Legislation.getTableOfContents(type, year, number, version);
        } catch (NoDocumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clml;
    }

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    public String akn(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode node = Transforms.clml2akn().transform(clml);
        String akn = Clml2Akn.serialize(node);
        return akn;
    }

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        Object simple = Transforms.simplifier().contents(clml);
        return simple;
    }

}
