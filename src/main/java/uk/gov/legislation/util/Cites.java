package uk.gov.legislation.util;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Cites {

    public static String make(String type, int year, int number, Collection<? extends AltNumber> altNumbers) {
        return make(Types.get(type), year, number, altNumbers);
    }

    public static String make(Type type, int year, int number, Collection<? extends AltNumber> altNumbers) {
        if (type == null)
            return null;
        String base = Cites.make(type, year, number);
        if (altNumbers == null)
            altNumbers = Collections.emptyList();
        String extra = altNumbers.stream().map(a -> " (" + convertCategory(a) + "." + " " + a.value() + ")")
                .collect(Collectors.joining());
        return base + extra;
    }

    private static String convertCategory(AltNumber alt) {
        if (alt.category().equals("NI"))
            return "N.I";
        return alt.category();
    }

    private static String make(Type type, int year, int number) {
        return switch(type) {
            // case UKPPA:
            case UKPGA,
                    NIA,
                    AOSP,
                    AEP,
                    AIP,
                    APGB -> year + " c. " + number;
            case UKLA ->
          //case GBLA:
                    year + " c. " + Roman.toLowerRoman(number);
            case ASP,
                    ANAW ->
          //case ASC:
                    year + " " + type.shortName() + " " + number;
         // case GBPPA:
            case MWA -> year + " nawm " + number;
            case MNIA,
                    APNI -> year + " Chapter " + number;
            case UKSI,
                    UKCM,
                    UKMD,
                    UKMO,
                    UKSRO,
                    WSI,
                    SSI,
                    NISI,
                    NISR,
                    UKCI,
                    NISRO -> year + " No. " + number;
            /*
            case UKDSI:
            return "";
            case NIDSR:
             return "";
            case SDSI:
            return "";
            */

        };
    }

}
