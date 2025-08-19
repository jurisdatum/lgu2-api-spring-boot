package uk.gov.legislation.converters.ld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.api.responses.ld.ClassResponse;
import uk.gov.legislation.data.virtuoso.jsonld.ClassLD;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

 class ClassConverterTest {

     @ParameterizedTest(name = "{index} => uri={0}, other={1}")
     @MethodSource("provideClassLDInputs")
     @DisplayName("Test ClassConverter.convert with various input combinations")
     void testConvert(ClassLD input, URI expectedUri, Map<String, Object> expectedOther) {
         ClassResponse result = ClassConverter.convert(input);

         assertNotNull(result);
         assertEquals(expectedUri, result.uri);

         if (expectedOther == null) {
             assertNotNull(result.other);
             assertEquals(0, result.other.size());
         } else {
             assertEquals(expectedOther, result.other);
         }
     }

     static Stream <Arguments> provideClassLDInputs() {
         return Stream.of(
             // Valid URI + other properties
             Arguments.of(
                 newClassLD(URI.create("http://www.legislation.gov.uk/ukpga"), mapOf("key1", "value1", "key2", 123)),
                 URI.create("http://www.legislation.gov.uk/ukpga"),
                 mapOf("key1", "value1", "key2", 123)
             ),
             // Valid URI + empty other
             Arguments.of(
                 newClassLD(URI.create("http://www.legislation.gov.uk/ukla"), Collections.emptyMap()),
                 URI.create("http://www.legislation.gov.uk/ukla"),
                 Collections.emptyMap()
             ),
             // Null URI + non-empty other
             Arguments.of(
                 newClassLD(null, mapOf("key1", "value1")),
                 null,
                 mapOf("key1", "value1")
             ),
             // Null URI + null other (should result in empty other map in response)
             Arguments.of(
                 newClassLD(null, null),
                 null,
                 null
             )
         );
     }

     // Helper method to build ClassLD
     private static ClassLD newClassLD(URI uri, Map<String, Object> other) {
         ClassLD instance = new ClassLD();
         instance.id = uri;
         if (other != null) {
             instance.other.putAll(other);
         }
         return instance;
     }

     // Helper method to build maps easily
     private static Map<String, Object> mapOf(Object... keyVals) {
         Map<String, Object> map = new LinkedHashMap<>();
         for (int i = 0; i < keyVals.length; i += 2) {
             map.put((String) keyVals[i], keyVals[i + 1]);
         }
         return map;
     }
 }