package uk.gov.legislation.api.parameters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.changes.Parameters;
import uk.gov.legislation.data.marklogic.search.Parameters.Sort;

@Component
public class SortConverter implements Converter<String, Sort> {

    @Override
    public Sort convert(String sort) {
        return Sort.fromValue(sort);
    }
     public static class EffectsSortConverter implements Converter<String, Parameters.EffectsSort> {

         @Override
         public Parameters.EffectsSort convert(String source) {
             return Parameters.EffectsSort.fromValue(source);
         }
     }
}
