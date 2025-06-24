package uk.gov.legislation.api.test;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.data.marklogic.search.Parameters;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
