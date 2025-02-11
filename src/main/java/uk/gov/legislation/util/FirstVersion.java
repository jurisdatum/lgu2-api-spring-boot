package uk.gov.legislation.util;

public class FirstVersion {

    public static String getVersion( Type type) {
        return switch(type) {
            case UKPGA, UKLA, UKPPA, ASP, NIA, AOSP, AEP, AIP, APGB, GBLA, ANAW, ASC, MWA, UKCM, MNIA, APNI -> "enacted";
            case UKSI, WSI, SSI, NISI, UKMD, UKSRO, UKDSI, NISR, NISRO, NIDSR, SDSI -> "made";
            case UKMO, UKCI -> "created";
        };
    }
    public static String getFirstVersion( String type){
        Type x= Types.get(type);
        assert x != null;
        return getVersion(x);
    }
}
