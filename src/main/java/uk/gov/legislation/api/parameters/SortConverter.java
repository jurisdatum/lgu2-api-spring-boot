package uk.gov.legislation.api.parameters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.search.Parameters.Sort;

@Component
public class SortConverter implements Converter<String, Sort> {

    @Override
    public Sort convert(String sort) {
        return Sort.valueOf(sort.trim().toUpperCase());
    }

}
