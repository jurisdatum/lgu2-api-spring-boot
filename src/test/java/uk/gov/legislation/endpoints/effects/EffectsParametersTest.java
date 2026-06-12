package uk.gov.legislation.endpoints.effects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EffectsParametersTest {

    @Test
    void sortIsWritableForParameterBinding() throws Exception {
        PropertyDescriptor sort =
                Arrays.stream(
                                Introspector.getBeanInfo(EffectsParameters.class)
                                        .getPropertyDescriptors())
                        .filter(pd -> pd.getName().equals("sort"))
                        .findFirst()
                        .orElse(null);

        assertNotNull(sort, "EffectsParameters should expose a bean property named 'sort'");
        assertNotNull(sort.getWriteMethod(), "sort must be writable for @ParameterObject binding");
    }
}
