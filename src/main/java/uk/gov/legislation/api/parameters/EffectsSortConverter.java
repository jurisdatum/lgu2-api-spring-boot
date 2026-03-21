package uk.gov.legislation.api.parameters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.changes.Parameters;

@Component
public class EffectsSortConverter implements Converter<String, Parameters.EffectsSort> {

        @Override
        public Parameters.EffectsSort convert(String source) {
            return Parameters.EffectsSort.fromValue(source);
        }

}
