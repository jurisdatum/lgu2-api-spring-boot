package uk.gov.legislation.endpoints.types.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.endpoints.types.api.TypesApi;
import uk.gov.legislation.endpoints.types.service.TypesService;

import java.util.List;

@RestController
public class TypesApiController implements TypesApi {

    private final TypesService typesService;

    public TypesApiController(TypesService typesService) {
        this.typesService = typesService;
    }

    /**
     * Fetch all available types.
     *
     * @return ResponseEntity containing a list of TypeWrapper objects.
     */
    @Override
    public ResponseEntity<List<TypeWrapper>> getAllTypes() {
        return ResponseEntity.ok(typesService.fetchAllTypes());
    }

    /**
     * Fetch types specific to the UK.
     *
     * @return ResponseEntity containing a TypesForCountry object.
     */
    @Override
    public ResponseEntity<TypesForCountry> getUkSpecificTypes() {
        return ResponseEntity.ok(typesService.fetchUkSpecificTypes());
    }
}


