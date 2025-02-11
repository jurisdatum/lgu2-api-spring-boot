package uk.gov.legislation.endpoints.types.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.util.Type;

import java.util.ArrayList;
import java.util.List;

@Service
public class TypesService {

    /**
     * Fetch all available types.
     *
     * @return a list of TypeWrapper objects representing all types.
     */
    public List<TypeWrapper> fetchAllTypes() {
        List<TypeWrapper> list = new ArrayList<>();
        for (Type type : Type.values()) {
            TypeWrapper typeWrapper = new TypeWrapper(type);
            list.add(typeWrapper);
        }
        return list;
    }

    /**
     * Fetch types specific to the UK.
     *
     * @return a TypesForCountry object containing types that primarily and possibly apply to the UK.
     */
    public TypesForCountry fetchUkSpecificTypes() {
        List<TypeWrapper> primarily = uk.gov.legislation.util.Types.primarilyAppliesToUK()
                .stream()
                .map(TypeWrapper::new)
                .toList();

        List<TypeWrapper> possibly = uk.gov.legislation.util.Types.possiblyAppliesToUK()
                .stream()
                .map(TypeWrapper::new)
                .toList();

        return new TypesForCountry("UK", primarily, possibly);
    }
}

