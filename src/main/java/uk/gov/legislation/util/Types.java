package uk.gov.legislation.util;


import java.util.*;
import java.util.stream.Collectors;

public class Types {

    private static final Set<Type> POSSIBLY_WALES = Set.of(Type.UKPGA, Type.UKLA, Type.UKPPA,Type.AEP,Type.APGB ,Type.GBLA, Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI);// not found -> GBPPA
    private static final Set<Type> POSSIBLY_SCOTLAND = Set.of(Type.UKPGA, Type.UKLA, Type.UKPPA, Type.APGB, Type.GBLA, Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI);  // not found -> GBPPA
    private static final Set<Type> POSSIBLY_NORTHERN_IRELAND = Set.of(Type.UKPGA, Type.UKLA, Type.UKPPA, Type.GBLA, Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI); // not found -> GBPPA

    private static final Set<String> ShortNames = Arrays.stream(Type.values()).map(Type::shortName).collect(Collectors.toSet());

    public static boolean isValidShortType(String type) {
        return ShortNames.contains(type);
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

    public static List<Type> primarilyAppliesToUK() {
        return Arrays.stream(Type.values()).filter(
                type->type.country().equals(Type.Country.UK)).toList();
    }
    public static List<Type> possiblyAppliesToUK() {
        return Arrays.stream(Type.values()).filter(
                type->!type.country().equals(Type.Country.UK)).toList();
    }

    public static List<Type> primarilyAppliesToWales() {
        return Arrays.stream(Type.values()).filter(
                type -> type.country().equals(Type.Country.WALES)).toList();
    }

    public static List<Type> possiblyAppliesToWales() {
        return new ArrayList <>(POSSIBLY_WALES);
    }

    public static List<Type> primarilyAppliesToScotland() {
        return Arrays.stream(Type.values()).filter(
                type -> type.country().equals(Type.Country.SCOTLAND)).toList();
    }

    public static List<Type> possiblyAppliesToScotland() {
        return new ArrayList<>(POSSIBLY_SCOTLAND);
    }

    public static List<Type> primarilyAppliesToNorthernIreland() {
        return Arrays.stream(Type.values()).filter(
                type -> type.country().equals(Type.Country.NORTHERN_IRELAND)).toList();
    }

    public static List<Type> possiblyAppliesToNorthernIreland() {
        return new ArrayList<>(POSSIBLY_NORTHERN_IRELAND);
    }
}
