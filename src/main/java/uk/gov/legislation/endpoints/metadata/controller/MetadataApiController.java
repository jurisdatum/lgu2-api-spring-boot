package uk.gov.legislation.endpoints.metadata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.endpoints.metadata.api.MetadataApi;
import uk.gov.legislation.endpoints.metadata.service.MetadataService;

import java.io.IOException;

@RestController
public class MetadataApiController implements MetadataApi {

    private final MetadataService metadataService;

    public MetadataApiController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public ResponseEntity<Item> getMetadata(String type, int year, int number) throws IOException, InterruptedException {
        Item item = metadataService.fetchMetadata(type, year, number);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metadata not found");
        }
        return ResponseEntity.ok(item);
    }
}

