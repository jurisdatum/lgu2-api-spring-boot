package uk.gov.legislation.util;

import uk.gov.legislation.api.responses.CommonMetadata;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Cites {

    public static String make(String type, int year, int number) {
        return make(Types.get(type), year, number, Collections.emptyList());
    }
    public static String make(String type, int year, int number, Collection<? extends AltNumber> altNumbers) {
        return make(Types.get(type), year, number, altNumbers);
    }

    public static String make2(String type, int year, int number, List<CommonMetadata.AltNumber> altNumbers) {
        if (altNumbers == null)
            altNumbers = Collections.emptyList();
        List<AltNumberWrapper> wrapped = altNumbers.stream().map(AltNumberWrapper::new).toList();
        return make(Types.get(type), year, number, wrapped);
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
        switch (type) {
            case UKPGA:
            // case UKPPA:
            case NIA:
            case AOSP:
            case AEP:
            case AIP:
            case APGB:
                return year + " c. " + number;
            case UKLA:
//            case GBLA:
                return year + " c. " + Roman.toLowerRoman(number);
            case ASP:
            case ANAW:
//            case ASC:
                return year + " " + type.shortName() + " " + number;
            // case GBPPA:
            case MWA:
                return year + " nawm " + number;
            case MNIA:
            case APNI:
                return year + " Chapter " + number;
            case UKSI:
            case UKCM:
            case UKMD:
            case UKMO:
            case UKSRO:
            case WSI:
            case SSI:
            case NISI:
            case NISR:
            case UKCI:
            case NISRO:
                return year + " No. " + number;
//            case UKDSI:
//                return "";
//            case NIDSR:
//                return "";
//            case SDSI:
//                return "";
            default:
                throw new IllegalArgumentException(type.shortName());
        }
    }

    /* */

    public static class AltNumberWrapper implements AltNumber {

        private final CommonMetadata.AltNumber resp;

        public AltNumberWrapper(CommonMetadata.AltNumber resp) {
            this.resp = resp;
        }

        @Override
        public String category() {
            return resp.category;
        }

        @Override
        public String value() {
            return resp.value;
        }

    }

}
