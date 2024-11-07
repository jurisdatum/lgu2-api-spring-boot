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
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.Optional;

@RestController
@Tag(name = "Documents")
@SuppressWarnings("unused")
public class Contents {

    @Autowired
    private Legislation db;

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "table of contents")
    public String clml(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml;
        try {
            clml = db.getTableOfContents(type, year, number, version);
        } catch (NoDocumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clml;
    }

    @Autowired
    private Clml2Akn clml2akn;

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    public String akn(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        XdmNode node = clml2akn.transform(clml);
        return Clml2Akn.serialize(node);
    }

    @Autowired
    private Simplify simplifier;

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TableOfContents json(@PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam Optional<String> version) throws Exception {
        String clml = clml(type, year, number, version);
        return simplifier.contents(clml);
    }

}
