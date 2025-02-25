package uk.gov.legislation.endpoints.types.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.endpoints.types.api.TypesApi;
import uk.gov.legislation.util.Type;
import uk.gov.legislation.util.Types;

import java.util.Arrays;
import java.util.List;

@RestController
public class TypesApiController implements TypesApi {

    /**
     * Fetch all available types.
     * @return ResponseEntity containing a list of TypeWrapper objects.
     */
    @Override
    public ResponseEntity<List<TypeWrapper>> getAllTypes() {
        List<TypeWrapper> types = Arrays.stream(Type.values()).map(TypeWrapper::new).toList();
        return ResponseEntity.ok(types);
    }

    /**
     * Fetch types specific to a country.
     * @return ResponseEntity containing a TypesForCountry object.
     */
    @Override
    public ResponseEntity<TypesForCountry> getTypesForCountry(@PathVariable String country) {
        TypesForCountry response = switch (country) {
            case "uk" -> make("UNITED_KINGDOM", Types.PRIMARILY_UK, Types.POSSIBLY_UK);
            case "wales" -> make("WALES", Types.PRIMARILY_WALES, Types.POSSIBLY_WALES);
            case "scotland" -> make("SCOTLAND", Types.PRIMARILY_SCOTLAND, Types.POSSIBLY_SCOTLAND);
            case "ni" -> make("NORTHERN_IRELAND", Types.PRIMARILY_NORTHERN_IRELAND, Types.POSSIBLY_NORTHERN_IRELAND);
            default -> throw new IllegalArgumentException("unrecognized country: " + country);
        };
        return ResponseEntity.ok(response);
    }

    private static TypesForCountry make(String country, List<Type> primarily, List<Type> possibly) {
        return new TypesForCountry(
            country,
            primarily.stream().map(TypeWrapper::new).toList(),
            possibly.stream().map(TypeWrapper::new).toList()
        );
    }

}
