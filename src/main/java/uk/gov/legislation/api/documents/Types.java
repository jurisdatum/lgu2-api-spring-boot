package uk.gov.legislation.api.documents;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.util.Type;
import uk.gov.legislation.api.types.Wrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Document types")
public class Types {

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Wrapper> types() {
        return Arrays.stream(Type.values()).map(Wrapper::new).collect(Collectors.toList());
    }

}
