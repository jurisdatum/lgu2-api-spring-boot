package uk.gov.legislation.util;

public class Cites {

    public static String make(String type, int year, int number) {
        switch (type) {
            case "ukpga", "UnitedKingdomPublicGeneralAct":
                return year + " c. " + number;
        }
        throw new IllegalArgumentException(type);
    }

}
