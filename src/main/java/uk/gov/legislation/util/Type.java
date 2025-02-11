package uk.gov.legislation.util;

public enum Type {

    // United Kingdom Acts
    UKPGA("United Kingdom Public General Act", Category.Primary, Country.UK),
    UKLA("United Kingdom Local Act", Category.Primary, Country.UK),
    UKPPA("United Kingdom Private or Personal Act", Category.Primary, Country.UK),
    UKSI("United Kingdom Statutory Instrument", Category.Secondary, Country.UK),
    UKMD("United Kingdom Ministerial Direction", Category.Secondary, Country.UK),
    UKMO("United Kingdom Ministerial Order", Category.Secondary, Country.UK),
    UKSRO("United Kingdom Statutory Rule or Order", Category.Secondary, Country.UK),
    UKDSI("United Kingdom Draft Statutory Instrument", Category.Secondary, Country.UK),

    // Wales Acts
    ANAW("Welsh National Assembly Act", Category.Primary, Country.WALES),
    ASC("Welsh Parliament Act", Category.Primary, Country.WALES),
    MWA("Welsh Assembly Measure", Category.Primary, Country.WALES),
    WSI("Welsh Statutory Instrument", Category.Secondary, Country.WALES),

    // Scotland Acts
    ASP("Scottish Act", Category.Primary, Country.SCOTLAND),
    AOSP("Scottish Old Act", Category.Primary, Country.SCOTLAND),
    SSI("Scottish Statutory Instrument", Category.Secondary, Country.SCOTLAND),
    SDSI("Scottish Draft Statutory Instrument", Category.Secondary, Country.SCOTLAND),

    // Northern Ireland Acts
    NIA("Northern Ireland Act", Category.Primary, Country.NORTHERN_IRELAND),
    AIP("Ireland Act", Category.Primary, Country.NORTHERN_IRELAND),
    NISR("Northern Ireland Statutory Rule", Category.Secondary, Country.NORTHERN_IRELAND),
    NISI("Northern Ireland Order in Council", Category.Secondary, Country.NORTHERN_IRELAND),
    MNIA("Northern Ireland Assembly Measure", Category.Primary, Country.NORTHERN_IRELAND),
    APNI("Northern Ireland Parliament Act", Category.Primary, Country.NORTHERN_IRELAND),
    NISRO("Northern Ireland Statutory Rule or Order", Category.Secondary, Country.NORTHERN_IRELAND),
    NIDSR("Northern Ireland Draft Statutory Rule", Category.Secondary, Country.NORTHERN_IRELAND),

    // Great Britain Acts
    APGB("Great Britain Act", Category.Primary, Country.GREAT_BRITAIN),
    GBLA("Great Britain Local Act", Category.Primary, Country.GREAT_BRITAIN),

    // England Acts
    UKCM("United Kingdom Church Measure", Category.Primary, Country.ENGLAND),
    UKCI("United Kingdom Church Instrument", Category.Secondary, Country.ENGLAND),
    AEP("England Act", Category.Primary, Country.ENGLAND);

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

