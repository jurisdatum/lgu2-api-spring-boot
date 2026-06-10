package uk.gov.legislation.api.parameters;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.legislation.util.Extent;

@Component
public class ExtentConverter implements Converter<String, Extent> {

    @Override
    public Extent convert(String extent) {
        String normalized = extent.trim().toUpperCase(Locale.ROOT);
        if ("N.I.".equals(normalized))
            return Extent.NI;
        return Extent.valueOf(normalized);
    }

}
