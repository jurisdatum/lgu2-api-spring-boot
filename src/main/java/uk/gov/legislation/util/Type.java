package uk.gov.legislation.util;

public enum Type {

    // United Kingdom Acts
    UKPGA("UnitedKingdomPublicGeneralAct", Category.Primary, Country.UK),
    UKLA("UnitedKingdomLocalAct", Category.Primary, Country.UK),
    UKPPA("UnitedKingdomPrivateOrPersonalAct", Category.Primary, Country.UK),
    UKSI("UnitedKingdomStatutoryInstrument", Category.Secondary, Country.UK),
    UKMD("UnitedKingdomMinisterialDirection", Category.Secondary, Country.UK),
    UKMO("UnitedKingdomMinisterialOrder", Category.Secondary, Country.UK),
    UKSRO("UnitedKingdomStatutoryRuleOrOrder", Category.Secondary, Country.UK),
    UKDSI("UnitedKingdomDraftStatutoryInstrument", Category.Secondary, Country.UK),

    // Wales Acts
    ASC("WelshParliamentAct", Category.Primary, Country.WALES),
    WSI("WelshStatutoryInstrument", Category.Secondary, Country.WALES),
    ANAW("WelshNationalAssemblyAct", Category.Primary, Country.WALES),
    MWA("WelshAssemblyMeasure", Category.Primary, Country.WALES),

    // Scotland Acts
    ASP("ScottishAct", Category.Primary, Country.SCOTLAND),
    AOSP("ScottishOldAct", Category.Primary, Country.SCOTLAND),
    SSI("ScottishStatutoryInstrument", Category.Secondary, Country.SCOTLAND),
    SDSI("ScottishDraftStatutoryInstrument", Category.Secondary, Country.SCOTLAND),

    // Northern Ireland Acts
    NIA("NorthernIrelandAct", Category.Primary, Country.NORTHERN_IRELAND),
    AIP("IrelandAct", Category.Primary, Country.NORTHERN_IRELAND),
    NISR("NorthernIrelandStatutoryRule", Category.Secondary, Country.NORTHERN_IRELAND),
    NISI("NorthernIrelandOrderInCouncil", Category.Secondary, Country.NORTHERN_IRELAND),
    MNIA("NorthernIrelandAssemblyMeasure", Category.Primary, Country.NORTHERN_IRELAND),
    APNI("NorthernIrelandParliamentAct", Category.Primary, Country.NORTHERN_IRELAND),
    NISRO("NorthernIrelandStatutoryRuleOrOrder", Category.Secondary, Country.NORTHERN_IRELAND),
    NIDSR("NorthernIrelandDraftStatutoryRule", Category.Secondary, Country.NORTHERN_IRELAND),

    // Great Britain Acts
    APGB("GreatBritainAct", Category.Primary, Country.GREAT_BRITAIN),
    GBLA("GreatBritainLocalAct", Category.Primary, Country.GREAT_BRITAIN),

    // England Acts
    UKCM("UnitedKingdomChurchMeasure", Category.Primary, Country.ENGLAND),
    UKCI("UnitedKingdomChurchInstrument", Category.Secondary, Country.ENGLAND),
    AEP("EnglandAct", Category.Primary, Country.ENGLAND);

    public enum Category { Primary, Secondary }

    public enum Country {
        UK, SCOTLAND, WALES, NORTHERN_IRELAND, ENGLAND, GREAT_BRITAIN
    }

    private final String longName;
    private final Category category;
    private final Country country;

    // Constructor
    Type(String longName, Category category, Country country) {
        this.longName = longName;
        this.category = category;
        this.country = country;
    }

    public String shortName() {
        return name().toLowerCase();
    }

    public String longName() {
        return longName;
    }

    public Type.Category category() {
        return category;
    }

    public Type.Country country() {
        return country;
    }
}

