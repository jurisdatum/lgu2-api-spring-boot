package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Types {

    private static final Set<String> shortNames = Arrays.stream(Type.values()).map(Type::shortName).collect(Collectors.toSet());

    public static boolean isValidShortType(String type) {
        return shortNames.contains(type);
    }

    private static final Map<String, String> ShortToLong = Arrays.stream(Type.values())
            .collect(Collectors.toMap(Type::shortName, Type::longName));

    private static final Map<String, String> LongToShort = ShortToLong.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static String shortToLong(String shortType) {
        return ShortToLong.get(shortType);
    }

    public static String longToShort(String longType) {
        return LongToShort.get(longType);
    }

    public static Type get(String type) {
        if (LongToShort.containsKey(type))
            type = LongToShort.get(type);
        if (!ShortToLong.containsKey(type))
            return null;
        return Type.valueOf(type.toUpperCase());
    }

}
