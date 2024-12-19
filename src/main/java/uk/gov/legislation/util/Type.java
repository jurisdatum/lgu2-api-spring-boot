package uk.gov.legislation.util;

public enum Type {

    /**
     * Primary Categories
     */
    UKPGA(Constants.TypeConstants.UNITED_KINGDOM_PUBLIC_GENERAL_ACT.getType(), Category.PRIMARY),
    UKLA(Constants.TypeConstants.UNITED_KINGDOM_LOCAL_ACT.getType(), Category.PRIMARY),
    //UKPPA(Constants.TypeConstants.UNITED_KINGDOM_PRIVATE_OR_PERSONAL_ACT.getType(), Category.PRIMARY),
    ASP(Constants.TypeConstants.SCOTTISH_ACT.getType(), Category.PRIMARY),
    NIA(Constants.TypeConstants.NORTHERN_IRELAND_ACT.getType(), Category.PRIMARY),
    AOSP(Constants.TypeConstants.SCOTTISH_OLD_ACT.getType(), Category.PRIMARY),
    AEP(Constants.TypeConstants.ENGLAND_ACT.getType(), Category.PRIMARY),
    AIP(Constants.TypeConstants.IRELAND_ACT.getType(), Category.PRIMARY),
    APGB(Constants.TypeConstants.GREAT_BRITAIN_ACT.getType(), Category.PRIMARY),
    // GBLA("GreatBritainLocalAct", Category.PRIMARY),
    // GBPPA
    ANAW(Constants.TypeConstants.WELSH_NATIONAL_ASSEMBLY_ACT.getType(), Category.PRIMARY),
    MWA(Constants.TypeConstants.WELSH_ASSEMBLY_MEASURE.getType(), Category.PRIMARY),
    UKCM(Constants.TypeConstants.UNITED_KINGDOM_CHURCH_MEASURE.getType(), Category.PRIMARY),
    MNIA(Constants.TypeConstants.NORTHERN_IRELAND_ASSEMBLY_MEASURE.getType(), Category.PRIMARY),
    APNI(Constants.TypeConstants.NORTHERN_IRELAND_PARLIAMENT_ACT.getType(), Category.PRIMARY),


    /**
     * Secondary Categories
     */

    UKSI(Constants.TypeConstants.UNITED_KINGDOM_STATUTORY_INSTRUMENT.getType(), Category.SECONDARY),
    UKMD(Constants.TypeConstants.UNITED_KINGDOM_MINISTERIAL_DIRECTION.getType(), Category.SECONDARY),
    UKMO(Constants.TypeConstants.UNITED_KINGDOM_MINISTERIAL_ORDER.getType(), Category.SECONDARY),
    UKSRO(Constants.TypeConstants.UNITED_KINGDOM_STATUTORY_RULE_OR_ORDER.getType(), Category.SECONDARY),
    // UKDSI("UnitedKingdomDraftStatutoryInstrument", Category.SECONDARY),
    WSI(Constants.TypeConstants.WELSH_STATUTORY_INSTRUMENT.getType(), Category.SECONDARY),
    SSI(Constants.TypeConstants.SCOTTISH_STATUTORY_INSTRUMENT.getType(), Category.SECONDARY),
    NISI(Constants.TypeConstants.NORTHERN_IRELAND_ORDER_IN_COUNCIL.getType(), Category.SECONDARY),
    NISR(Constants.TypeConstants.NORTHERN_IRELAND_STATUTORY_RULE.getType(), Category.SECONDARY),
    UKCI(Constants.TypeConstants.UNITED_KINGDOM_CHURCH_INSTRUMENT.getType(), Category.SECONDARY),
    NISRO(Constants.TypeConstants.NORTHERN_IRELAND_STATUTORY_RULE_OR_ORDER.getType(), Category.SECONDARY);
    // NIDSR("NorthernIrelandDraftStatutoryRule", Category.SECONDARY),
    // SDSI("ScottishDraftStatutoryInstrument", Category.SECONDARY),
    // WDSI?
    // NIDSI?

    // Placeholder for Future Categories
    // "eur", "eudn", "eudr", "eut"
    // "ukia" sia wia niia

    ;

    public enum Category {
        PRIMARY,
        SECONDARY
    }

    private final String longName;
    private final Category category;

    Type(String longName, Category category) {
        this.longName = longName;
        this.category = category;
    }

    public String shortName() { return name().toLowerCase(); }

    public String longName() { return longName; }

    public Category category() { return category; }

}
