package uk.gov.legislation.util;

public class FirstVersion {

    public static String get(String type) {
        switch (type) {
            case "ukpga", "UnitedKingdomPublicGeneralAct":
                return "enacted";
        }
        throw new IllegalArgumentException(type);
    }

}
