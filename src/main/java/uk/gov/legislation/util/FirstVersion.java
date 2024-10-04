package uk.gov.legislation.util;

public class FirstVersion {

    public static String get(String type) {
        switch (type) {
            case "ukpga", "UnitedKingdomPublicGeneralAct":
                return "enacted";
            case "uksi", "UnitedKingdomStatutoryInstrument":
            case "wsi", "WelshStatutoryInstrument":
            case "nisi", "NorthernIrelandOrderInCouncil":
                return "made";
        }
        throw new IllegalArgumentException(type);
    }

}
