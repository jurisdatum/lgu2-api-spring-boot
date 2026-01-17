package uk.gov.legislation.endpoints.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.PageOfDocuments;

import java.io.IOException;

@Tag(name = "Search")
public interface SearchApi {

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<StreamingResponseBody> searchByAtom(@ParameterObject SearchParameters parameters
    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PageOfDocuments> searchByJson(@ParameterObject SearchParameters parameters
    ) throws IOException, InterruptedException;

}
