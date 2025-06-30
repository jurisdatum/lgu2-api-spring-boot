package uk.gov.legislation.converters.ld;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.exceptions.UnknownTypeException;

import java.util.Map;

@Component
public class EnumConverter implements Converter<String, Parameters.Sort> {

    private static final Map <String, Parameters.Sort> CASE_INSENSITIVE_VALUES = Map.of(
        "title", Parameters.Sort.TITLE,
        "year", Parameters.Sort.YEAR
    );

    @Override
    public Parameters.Sort convert(String sort) {
        if (sort == null) {
            return null;
        }
        // First check case-insensitive matches (like "title", "year")
        Parameters.Sort mapped = CASE_INSENSITIVE_VALUES.get(sort.toLowerCase());
        if (mapped != null) {
            return mapped;
        }
        try {
            return Parameters.Sort.valueOf(sort);
        } catch (IllegalArgumentException e) {
            throw new UnknownTypeException("Invalid sort type: " + sort);
        }
    }
}
