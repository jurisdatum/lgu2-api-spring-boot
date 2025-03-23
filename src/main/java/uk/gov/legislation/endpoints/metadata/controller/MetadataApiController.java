package uk.gov.legislation.endpoints.metadata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Metadata;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.data.virtuoso.model.MetadataItem;
import uk.gov.legislation.endpoints.metadata.api.MetadataApi;

import java.io.IOException;
import java.util.List;

@RestController
public class MetadataApiController implements MetadataApi {

    private final uk.gov.legislation.data.virtuoso.queries.Item itemQuery;
    private final Metadata metadata;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public MetadataApiController(uk.gov.legislation.data.virtuoso.queries.Item item, Metadata metadata, ObjectMapper mapper) {
        this.itemQuery = item;
        this.metadata = metadata;
        this.jsonMapper = mapper;
        this.xmlMapper = new XmlMapper();
    }

    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public ResponseEntity<String> getMetadata(String type, int year, int number, String accept) throws Exception {

        if (Virtuoso.Formats.contains(accept)) {
            String data = itemQuery.get(type, year, number, accept);
            return ResponseEntity.ok().header(CONTENT_TYPE, accept).body(data);
        }

        Item item = itemQuery.get(type, year, number);
        if(item == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metadata not found");

        if ("application/xml".equals(accept)) {
            String xml = xmlMapper.writeValueAsString(item);
            return ResponseEntity.ok().header(CONTENT_TYPE, accept).body(xml);
        }

        String json = jsonMapper.writeValueAsString(item);
        return ResponseEntity.ok().header(CONTENT_TYPE, "application/json").body(json);
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
