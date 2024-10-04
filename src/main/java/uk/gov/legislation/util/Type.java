package uk.gov.legislation.util;

public enum Type {

    UKPGA("UnitedKingdomPublicGeneralAct", Category.Primary),

    UKSI("UnitedKingdomStatutoryInstrument", Category.Secondary),
    WSI("WelshStatutoryInstrument", Category.Secondary),
    NISI("NorthernIrelandOrderInCouncil", Category.Secondary);

    public enum Category { Primary, Secondary }

    private final String longName;

    private final Category category;

    private Type(String longName, Category classification) {
        this.longName = longName;
        this.category = classification;
    }

    public String shortName() { return name().toLowerCase(); }

    public String longName() { return longName; }

    public Category category() { return category; }

}
