package uk.gov.legislation.util;

public class FirstVersion {

    public static String getFirstVersion(Type type) {
        return switch (type) {
            case UKPGA, UKLA, UKPPA, ASP, NIA, AOSP, AEP, AIP, APGB, GBLA, GBPPA, ANAW, ASC, MWA, UKCM, MNIA, APNI -> "enacted";
            case UKSI, WSI, SSI, NISI, UKMD, UKSRO, UKDSI, NISR, NISRO, NIDSR, SDSI -> "made";
            case UKMO, UKCI -> "created";
            case EUR, EUDN, EUDR, EUT -> "adopted";
        };
    }

    public static String getFirstVersion(String type) {
        Type x = Types.get(type);
        if (x == null)
            throw new IllegalArgumentException(type);
        return getFirstVersion(x);
    }

}
