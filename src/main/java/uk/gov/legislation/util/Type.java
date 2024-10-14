package uk.gov.legislation.util;

public enum Type {

    UKPGA("UnitedKingdomPublicGeneralAct", Category.Primary),
    UKLA("UnitedKingdomLocalAct", Category.Primary),
//    CULA?
//    UKPPA("UnitedKingdomPrivateOrPersonalAct", Category.Primary),

    ASP("ScottishAct", Category.Primary),
    NIA("NorthernIrelandAct", Category.Primary),
    AOSP("ScottishOldAct", Category.Primary),
    AEP("EnglandAct", Category.Primary),
    AIP("IrelandAct", Category.Primary),
    APGB("GreatBritainAct", Category.Primary),
//    GBLA("GreatBritainLocalAct", Category.Primary),
//    GBPPA
    ANAW("WelshNationalAssemblyAct", Category.Primary),
//    ASC("WelshParliamentAct", Category.Primary),
    MWA("WelshAssemblyMeasure", Category.Primary),
    UKCM("UnitedKingdomChurchMeasure", Category.Primary),
    MNIA("NorthernIrelandAssemblyMeasure", Category.Primary),
    APNI("NorthernIrelandParliamentAct", Category.Primary),

    UKSI("UnitedKingdomStatutoryInstrument", Category.Secondary),
    UKMD("UnitedKingdomMinisterialDirection", Category.Secondary),
    UKMO("UnitedKingdomMinisterialOrder", Category.Secondary),
    UKSRO("UnitedKingdomStatutoryRuleOrOrder", Category.Secondary),
//    UKDSI("UnitedKingdomDraftStatutoryInstrument", Category.Secondary),

    WSI("WelshStatutoryInstrument", Category.Secondary),
    SSI("ScottishStatutoryInstrument", Category.Secondary),
    NISI("NorthernIrelandOrderInCouncil", Category.Secondary),
    NISR("NorthernIrelandStatutoryRule", Category.Secondary),

    UKCI("UnitedKingdomChurchInstrument", Category.Secondary),
    NISRO("NorthernIrelandStatutoryRuleOrOrder", Category.Secondary);
//    NIDSR("NorthernIrelandDraftStatutoryRule", Category.Secondary),
//    SDSI("ScottishDraftStatutoryInstrument", Category.Secondary);
//    WDSI ?
//    NIDSI ?
    // "eur", "eudn", "eudr", "eut"
    // "ukia" sia wia niia

    public enum Category { Primary, Secondary }

    private final String longName;

    private final Category category;

    Type(String longName, Category classification) {
        this.longName = longName;
        this.category = classification;
    }

    public String shortName() { return name().toLowerCase(); }

    public String longName() { return longName; }

    public Category category() { return category; }

}
