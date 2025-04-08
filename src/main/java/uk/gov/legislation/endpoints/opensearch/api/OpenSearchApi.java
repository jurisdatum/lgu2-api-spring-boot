package uk.gov.legislation.endpoints.opensearch.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.legislation.data.virtuoso.model.OpenSearchRequest;


import java.io.IOException;

@Tag(name = "OpenSearch")
public interface OpenSearchApi {

    @GetMapping("/open-search")
    public ResponseEntity<String> getOpenSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws IOException;


    @GetMapping("/all")
    public ResponseEntity<String> getAllDocuments() throws IOException;


    @GetMapping("/get/{id}")
    public ResponseEntity<String> getDocument(@PathVariable String id) throws IOException;


    @PostMapping("/store")
    public ResponseEntity<String> storeDocument(@RequestBody OpenSearchRequest document) throws IOException;


    @DeleteMapping("/delete/{id}")
     ResponseEntity<String> deleteDocument(@PathVariable String id) throws IOException;
}

