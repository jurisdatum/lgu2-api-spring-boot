package uk.gov.legislation.util;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ShortTypes {

//    private static Set<String> Primary = Set.of("ukpga", "ukla", "ukppa", "asp", "asc", "anaw", "mwa", "ukcm", "nia", "aosp", "aep", "aip", "apgb", "gbla", "gbppa", "nisi", "mnia", "apni");
//
//    private static Set<String> Secondary = Set.of("uksi", "wsi", "ssi", "nisr", "ukci", "ukmd", "ukmo", "uksro", "nisro");
//
//    private static Set<String> EU = Set.of("eur", "eudn", "eudr", "eut");
//
//    private static Set<String> Draft = Set.of("ukdsi", "sdsi", "nidsr");
//
//    private static Set<String> Other = Set.of("ukia");

    private static Set<String> types = Set.of("ukpga");

    public static boolean isValidShortType(String type) {
        return types.contains(type);
    }

    private static Map<String, String> ShortToLong = Map.of(
        "ukpga", "UnitedKingdomPublicGeneralAct"
    );

    private static Map<String, String> LongToShort = ShortToLong.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static String shortToLong(String shortType) {
        return ShortToLong.get(shortType);
    }

    public static String longToShort(String longType) {
        return LongToShort.get(longType);
    }

}
