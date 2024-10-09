package uk.gov.legislation.util;

public class FirstVersion {

    public static String get(String type) {
        switch (type) {
            case "ukpga", "UnitedKingdomPublicGeneralAct":
            case "ukla", "UnitedKingdomLocalAct":
            case "ukppa", "UnitedKingdomPrivateOrPersonalAct":
            case "asp", "ScottishAct":
            case "nia", "NorthernIrelandAct":
            case "aosp", "ScottishOldAct":
            case "aep", "EnglandAct":
            case "aip", "IrelandAct":
            case "apgb", "GreatBritainAct":
            case "gbla", "GreatBritainLocalAct":
            case "anaw", "WelshNationalAssemblyAct":
            case "asc", "WelshParliamentAct":
            case "mwa", "WelshAssemblyMeasure":
            case "ukcm", "UnitedKingdomChurchMeasure":
            case "mnia", "NorthernIrelandAssemblyMeasure":
            case "apni", "NorthernIrelandParliamentAct":
                return "enacted";
            case "uksi", "UnitedKingdomStatutoryInstrument":
            case "wsi", "WelshStatutoryInstrument":
            case "ssi", "ScottishStatutoryInstrument":
            case "nisi", "NorthernIrelandOrderInCouncil":
            case "ukmd", "UnitedKingdomMinisterialDirection":
            case "uksro", "UnitedKingdomStatutoryRuleOrOrder":
            case "ukdsi", "UnitedKingdomDraftStatutoryInstrument":
            case "nisr", "NorthernIrelandStatutoryRule":
            case "nisro", "NorthernIrelandStatutoryRuleOrOrder":
            case "nidsr", "NorthernIrelandDraftStatutoryRule":
            case "sdsi", "ScottishDraftStatutoryInstrument":
                return "made";
            case "ukmo", "UnitedKingdomMinisterialOrder":
            case "ukci", "UnitedKingdomChurchInstrument":
                return "created";
        }
        throw new IllegalArgumentException(type);
    }

}
