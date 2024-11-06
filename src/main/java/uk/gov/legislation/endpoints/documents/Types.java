package uk.gov.legislation.endpoints.documents;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.util.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Document types")
public class Types {

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TypeWrapper> types() {
        return Arrays.stream(Type.values()).map(TypeWrapper::new).collect(Collectors.toList());
    }

    @GetMapping(value = "/types/uk", produces = MediaType.APPLICATION_JSON_VALUE)
    public TypesForCountry uk() {
        List<TypeWrapper> primarily = uk.gov.legislation.util.Types.primarilyAppliesToUK()
                .stream().map(TypeWrapper::new).toList();
        List<TypeWrapper> possibly = uk.gov.legislation.util.Types.possiblyAppliesToUK()
                .stream().map(TypeWrapper::new).toList();
        return new TypesForCountry() {
            public String country() { return "UK"; }
            public List<TypeWrapper> primarily() { return primarily; }
            public List<TypeWrapper> possibly() { return possibly; }
        };
    }

}
