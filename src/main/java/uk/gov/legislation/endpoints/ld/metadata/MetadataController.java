package uk.gov.legislation.endpoints.ld.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.model.Item;

import java.util.List;

@RestController
public class MetadataController implements MetadataApi {

    private final uk.gov.legislation.data.virtuoso.queries.Item itemQuery;
    private final uk.gov.legislation.data.virtuoso.queries.Items itemsQuery;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public MetadataController(uk.gov.legislation.data.virtuoso.queries.Item item, uk.gov.legislation.data.virtuoso.queries.Items items, ObjectMapper mapper) {
        this.itemQuery = item;
        this.itemsQuery = items;
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
    public ResponseEntity<String> getMetadataList(String type, int year, int page, int pageSize, String accept) throws Exception {

        final int offset = (page - 1) * pageSize;

        if (Virtuoso.Formats.contains(accept)) {
            String data = itemsQuery.get(type, year, pageSize, offset, accept);
            return ResponseEntity.ok().header(CONTENT_TYPE, accept).body(data);
        }

        List<Item> items = itemsQuery.get(type, year, pageSize, offset);

        if ("application/xml".equals(accept)) {
            String xml = xmlMapper.writeValueAsString(items);
            return ResponseEntity.ok().header(CONTENT_TYPE, accept).body(xml);
        }

        String json = jsonMapper.writeValueAsString(items);
        return ResponseEntity.ok().header(CONTENT_TYPE, "application/json").body(json);
    }

}
