package uk.gov.legislation.util;

import java.util.Collection;
import java.util.Optional;

public class Cites {

    public static String make(String type, int year, int number, Collection<? extends AltNumber> altNumbers) {
        if (Type.WSI.longName().equals(type) || Type.WSI.shortName().equals(type)) {
            Optional<String> alt = altNumbers.stream().filter(a -> "W".equals(a.category())).map(a -> a.value()).findFirst();
            return Cites.make(type, year, number, alt);
        }
        if (Type.NISI.longName().equals(type) || Type.NISI.longName().equals(type)) {
            Optional<String> alt = altNumbers.stream().filter(a -> "NI".equals(a.category())).map(a -> a.value()).findFirst();
            return Cites.make(type, year, number, alt);
        }
        return Cites.make(type, year, number, Optional.empty());

    }

    private static String make(String type, int year, int number, Optional<String> alt) {
        switch (type) {
            case "ukpga", "UnitedKingdomPublicGeneralAct":
                return year + " c. " + number;
            case "uksi", "UnitedKingdomStatutoryInstrument":
                return year + " No. " + number;
            case "wsi", "WelshStatutoryInstrument":
                if (alt.isPresent())
                    return year + " No. " + number + " (W. " + alt.get() + ")";
                else
                    return year + " No. " + number;
            case "nisi", "NorthernIrelandOrderInCouncil":
                if (alt.isPresent())
                    return year + " No. " + number + " (N.I. " + alt.get() + ")";
                else
                    return year + " No. " + number;
        }
        throw new IllegalArgumentException(type);
    }

}
