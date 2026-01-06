package uk.gov.legislation.util;

import java.util.*;
import java.util.stream.Collectors;

public class Types {

    private static final Map<String, String> ShortToLong = Arrays.stream(Type.values())
            .collect(Collectors.toMap(Type::shortName, Type::longName));

    private static final Map<String, String> LongToShort = ShortToLong.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static boolean isValidShortType(String type) {
        return ShortToLong.containsKey(type);
    }

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

    /* by country */

    public static final List<Type> PRIMARILY_UK = List.of(
        Type.UKPGA, Type.UKLA, Type.UKPPA, Type.UKSI,
        Type.UKMD, Type.UKMO, Type.UKSRO, Type.UKDSI, Type.UKIA
    );
    public static final List<Type> POSSIBLY_UK = List.of(
        Type.ASP, Type.NIA, Type.AOSP, Type.AEP, Type.AIP, Type.APGB, Type.GBLA, Type.GBPPA,
        Type.NISR, Type.ANAW, Type.ASC, Type.MWA, Type.UKCM,
        Type.WSI, Type.SSI, Type.NISI, Type.UKCI, Type.MNIA, Type.APNI, Type.NISRO,
        Type.NIDSR, Type.SDSI
    );

    public static final List<Type> PRIMARILY_WALES = List.of(
        Type.ASC, Type.WSI, Type.ANAW, Type.MWA
    );
    public static final List<Type> POSSIBLY_WALES = List.of(
        Type.UKPGA, Type.UKLA, Type.UKPPA, Type.AEP, Type.APGB, Type.GBLA, Type.GBPPA,
        Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI
    );

    public static final List<Type> PRIMARILY_SCOTLAND = List.of(
        Type.ASP, Type.AOSP, Type.SSI, Type.SDSI
    );
    public static final List<Type> POSSIBLY_SCOTLAND = List.of(
        Type.UKPGA, Type.UKLA, Type.UKPPA, Type.APGB, Type.GBLA, Type.GBPPA,
        Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI
    );

    public static final List<Type> PRIMARILY_NORTHERN_IRELAND = List.of(
        Type.NIA, Type.AIP, Type.NISR, Type.NISI,
        Type.MNIA, Type.APNI, Type.NISRO, Type.NIDSR
    );
    public static final List<Type> POSSIBLY_NORTHERN_IRELAND = List.of(
        Type.UKPGA, Type.UKLA, Type.UKPPA, Type.GBLA, Type.GBPPA,
        Type.UKSI, Type.UKMD, Type.UKSRO, Type.UKDSI
    );

}
