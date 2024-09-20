package uk.gov.legislation.api.metadata;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.model.Item;

@RestController
@Tag(name = "Linked data")
public class Metadata {

    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Item get(@PathVariable String type, @PathVariable int year, @PathVariable int number) throws Exception {
        Item item = uk.gov.legislation.data.virtuoso.Metadata.get(type, year, number);
        if (item == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return item;
    }

}
