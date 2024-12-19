package uk.gov.legislation.util;

public class FirstVersion {

    public static String get(String type) {
        Constants.TypeConstants constant = Constants.TypeConstants.fromType(type);

        return switch (constant) {
            case UKPGA, UNITED_KINGDOM_PUBLIC_GENERAL_ACT,
                    UKLA, UNITED_KINGDOM_LOCAL_ACT,
                    UKPPA, UNITED_KINGDOM_PRIVATE_OR_PERSONAL_ACT,
                    ASP, SCOTTISH_ACT,
                    NIA, NORTHERN_IRELAND_ACT,
                    AOSP, SCOTTISH_OLD_ACT,
                    AEP, ENGLAND_ACT,
                    AIP, IRELAND_ACT,
                    APGB, GREAT_BRITAIN_ACT,
                    GBLA, GREAT_BRITAIN_LOCAL_ACT,
                    ANAW, WELSH_NATIONAL_ASSEMBLY_ACT,
                    ASC, WELSH_PARLIAMENT_ACT,
                    MWA, WELSH_ASSEMBLY_MEASURE,
                    UKCM, UNITED_KINGDOM_CHURCH_MEASURE,
                    MNIA, NORTHERN_IRELAND_ASSEMBLY_MEASURE,
                    APNI, NORTHERN_IRELAND_PARLIAMENT_ACT -> "enacted";

            case UKSI, UNITED_KINGDOM_STATUTORY_INSTRUMENT,
                    WSI, WELSH_STATUTORY_INSTRUMENT,
                    SSI, SCOTTISH_STATUTORY_INSTRUMENT,
                    NISI, NORTHERN_IRELAND_ORDER_IN_COUNCIL,
                    UKMD, UNITED_KINGDOM_MINISTERIAL_DIRECTION,
                    UKSRO, UNITED_KINGDOM_STATUTORY_RULE_OR_ORDER,
                    UKDSI, UNITED_KINGDOM_DRAFT_STATUTORY_INSTRUMENT,
                    NISR, NORTHERN_IRELAND_STATUTORY_RULE,
                    NISRO, NORTHERN_IRELAND_STATUTORY_RULE_OR_ORDER,
                    NIDSR, NORTHERN_IRELAND_DRAFT_STATUTORY_RULE,
                    SDSI, SCOTTISH_DRAFT_STATUTORY_INSTRUMENT -> "made";

            case UKMO, UNITED_KINGDOM_MINISTERIAL_ORDER,
                    UKCI, UNITED_KINGDOM_CHURCH_INSTRUMENT -> "created";

        };
    }
}



