package uk.gov.legislation.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.transform.simple.Level;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

 class LabelsTest {


    @ParameterizedTest
    @MethodSource("provideLevels")
    void testMake(Level level, String expected) {
        assertEquals(expected, Labels.make(level));
    }

    static Stream<Arguments> provideLevels() {
        return Stream.of(
            arguments(level("Part", "1", null, null), "1"),
            arguments(level("Chapter", "2", null, null), "2"),
            arguments(level("Pblock", null, "Title for Block", null), "Title for Block"),
            arguments(level("PsubBlock", null, "Title for SubBlock", null), "Title for SubBlock"),
            arguments(level("P1", null, null, "example-id"), capitalize("example id")),
            arguments(level("Schedule", "5", null, null), "5"),
            arguments(level("DefaultName", null, null, null), "DefaultName")
        );
    }

    private static Level level(String name, String number, String title, String id) {
        Level level = new Level();
        level.name = name;
        level.number = number;
        level.title = title;
        level.id = id;
        return level;
    }
    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

}