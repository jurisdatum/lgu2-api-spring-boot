package uk.gov.legislation.util;

import java.util.Set;

public class ShortTypes {

    private static Set<String> types = Set.of("ukpga");

    public static boolean isValidShortType(String type) {
        return types.contains(type);
    }

}
