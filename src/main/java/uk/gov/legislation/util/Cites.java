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

    public static String convertNumbersAndMake(String type, int year, int number, List<CommonMetadata.AltNumber> altNumbers) {
        if (altNumbers == null)
            altNumbers = List.of();
        List<AltNumberWrapper> wrapped = altNumbers.stream()
            .map(AltNumberWrapper::new)
            .toList();
        return make(Types.get(type), year, number, wrapped);
    }

    public static String make(Type type, int year, int number, Collection<? extends AltNumber> altNumbers) {
        if (altNumbers == null)
            altNumbers = List.of();
        if (altNumbers.stream().anyMatch(a -> "W".equals(a.category()))) {
            // This is needed only to correct bad data
            // I don't think there should ever be both a W and a Cy alternative number
            altNumbers = altNumbers.stream()
                .filter(a -> !"Cy".equals(a.category()))
                .toList();
        }
        RegnalYear regnal = altNumbers.stream()
            .filter(a -> "Regnal".equals(a.category()))
            .findFirst()
            .map(AltNumber::value)
            .map(RegnalYear::parse)
            .orElse(null);
        String base = Cites.make(type, year, regnal, number);
        String extra = altNumbers.stream()
            .filter(a -> !"Regnal".equals(a.category()))
            .map(a -> " (" + convertCategory(a) + "." + " " + a.value() + ")")
            .collect(Collectors.joining());
        return base + extra;
    }

    private static String convertCategory(AltNumber alt) {
        if (alt.category().equals("NI"))
            return "N.I";
        return alt.category();
    }

    private static final String SI_PREFIX = "S.I. ";
    private static final String NI_DESIGNATION = " (N.I.)";

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static String make(Type type, int year, RegnalYear regnal, int number) {
        String regnalYear = RegnalYear.forCitation(regnal);
        return switch (type) {
            case UKPGA, UKPPA -> regnalYear + year + " c. " + number;
            case APGB -> year + regnalYear + " c. " + number;
            case NIA -> year + " c. " + number + NI_DESIGNATION;
            case AOSP -> year + " c. " + number + " [S]";
            case AEP -> year + regnalYear + " c. " + number;
            case AIP -> year + regnalYear + " c. " + number + " [I]";
            case UKLA, GBLA, GBPPA -> year + regnalYear + " c. " + Roman.toLowerRoman(number);
            case ASP, ANAW, ASC -> year + " " + type.shortName() + " " + number;
            case MWA -> year + " nawm " + number;
            case MNIA -> year + " c. " + number + NI_DESIGNATION;
            case APNI -> year + " c. " + number + NI_DESIGNATION;
            case UKCM, UKMO -> year + " No. " + number;
            case UKMD -> "No. " + year + "/" + number;
            case UKCI -> "Church Instrument  " + year + "/" + number;
            case UKSI, WSI, NISI -> SI_PREFIX + year + "/" + number;
            case UKSRO, NISRO -> "S.R. & O. " + year + "/" + number;
            case NISR -> "S.R. " + year + "/" + number;
            case SSI -> "S.S.I. " + year + "/" + number;
            case UKDSI -> SI_PREFIX + year + " (draft)";
            case SDSI -> "S.S.I. " + year + " (draft)";
            case NIDSR -> SI_PREFIX + year + " (N.I.) (draft)";
        };
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
