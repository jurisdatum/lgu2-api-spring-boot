package uk.gov.legislation.data.marklogic.search;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.legislation.endpoints.ParameterValidator;
import uk.gov.legislation.exceptions.UnknownTypeException;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

     static Stream<Arguments> types() {
         return Stream.of(
             Arguments.of(List.of("ukpga", "ukla"), "ukpga+ukla"),
             Arguments.of(List.of("asp"), "asp"),
             Arguments.of(List.of("primary", "eu-origin"), "primary+eu-origin")
         );
     }
     static Stream<Arguments> type() {
         return Stream.of(
             Arguments.of(("ukpga"), "ukpga"),
             Arguments.of(("asp"), "asp"),
             Arguments.of(("primary"), "primary")
         );
     }

     @ParameterizedTest
     @MethodSource("types")
     void listTypesTest(List<String> input, String expected) {
         Parameters.Builder builder = new Parameters.Builder();
         Parameters result = builder.type(input).build();
         assertEquals(expected, result.type);
     }

     @ParameterizedTest
     @MethodSource("type")
     void singleTypesTest(String input, String expected) {
         Parameters.Builder builder = new Parameters.Builder();
         Parameters result = builder.type(input).build();
         assertEquals(expected, result.type);
     }

    @ParameterizedTest
    @ValueSource(strings = {"ukpga", "ukla", "uksi"}) // valid types
    @NullSource
    void testSingleTypeValid(String type) {
        assertDoesNotThrow(() -> ParameterValidator.validateType(type));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid", "123", "pppp"}) // invalid types
    void testSingleTypeInvalid(String type) {
        assertThrows(UnknownTypeException.class, () -> ParameterValidator.validateType(type));
    }

    @Test
    void testListOfValidTypes() {
        List<String> types = List.of("primary", "secondary", "eu-origin");
        assertDoesNotThrow(() -> ParameterValidator.validateType(types));
    }

    @ParameterizedTest
    @MethodSource("invalidTypeLists")
    void testListOfInvalidTypes(List<String> types) {
        assertThrows(UnknownTypeException.class, () -> ParameterValidator.validateType(types));
    }

    static Stream<List<String>> invalidTypeLists() {
        return Stream.of(
            List.of("A", "invalid"),
            List.of("unknown", "X")
        );
    }

}
