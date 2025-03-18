package uk.gov.legislation.endpoints.metadata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Metadata;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.data.virtuoso.model.MetadataItem;
import uk.gov.legislation.endpoints.metadata.api.MetadataApi;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
public class MetadataApiController implements MetadataApi {

    private final Metadata metadata;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public MetadataApiController(Metadata metadata, ObjectMapper mapper) {
        this.metadata = metadata;
        this.jsonMapper = mapper;
        this.xmlMapper = new XmlMapper();
    }

    static final Set<String> NativeFormats = Set.of(
            "application/rdf+xml",
            "application/sparql-results+json",
            "application/sparql-results+xml",
            "text/csv",
            "text/plain",
            "text/turtle"
    );

    @Override
    public ResponseEntity<String> getMetadata(String type, int year, int number, String accept) throws IOException, InterruptedException {

        if (NativeFormats.contains(accept)) {
            String data = metadata.get(type, year, number, accept);
            return ResponseEntity.ok(data);
        }

        Item item = metadata.get(type, year, number);
        if (item == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        if ("application/xml".equals(accept)) {
            String xml = xmlMapper.writeValueAsString(item);
            return ResponseEntity.ok(xml);
        }

        String json = jsonMapper.writeValueAsString(item);
        return ResponseEntity.ok(json);
    }

    @Override
    public ResponseEntity <List <MetadataItem>> getMetadataList(String type, int year, int page, int pageSize) throws IOException, InterruptedException {
        int offset = (page - 1) * pageSize;
        List <MetadataItem> items = metadata.getListOfMetadata(type, year, pageSize, offset);
        if(items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No metadata found");
        }
        return ResponseEntity.ok(items);
    }

}
