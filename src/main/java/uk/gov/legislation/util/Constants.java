package uk.gov.legislation.util;

import lombok.Getter;

@Getter
public enum Constants {


    // Transformation failure messages
    TRANSFORMATION_FAIL_AKN("Failed to transform CLML to AKN"),
    TRANSFORMATION_FAIL_HTML("Failed to transform CLML to HTML"),
    TRANSFORMATION_FAIL_JSON("Failed to transform CLML to JSON"),

    // Document not found messages
    DOCUMENT_NOT_FOUND("Document not found: %s, %s, %d");
     final String error;

    Constants(String error) {
        this.error = error;
    }

    /**
     * Type Constance for All Types
     */
     @Getter
     enum TypeConstants {

        UKPGA("ukpga"),
        UNITED_KINGDOM_PUBLIC_GENERAL_ACT("UnitedKingdomPublicGeneralAct"),
        UKLA("ukla"),
        UNITED_KINGDOM_LOCAL_ACT("UnitedKingdomLocalAct"),
        UKPPA("ukppa"),
        UNITED_KINGDOM_PRIVATE_OR_PERSONAL_ACT("UnitedKingdomPrivateOrPersonalAct"),
        ASP("asp"),
        SCOTTISH_ACT("ScottishAct"),
        NIA("nia"),
        NORTHERN_IRELAND_ACT("NorthernIrelandAct"),
        AOSP("aosp"),
        SCOTTISH_OLD_ACT("ScottishOldAct"),
        AEP("aep"),
        ENGLAND_ACT("EnglandAct"),
        AIP("aip"),
        IRELAND_ACT("IrelandAct"),
        APGB("apgb"),
        GREAT_BRITAIN_ACT("GreatBritainAct"),
        GBLA("gbla"),
        GREAT_BRITAIN_LOCAL_ACT("GreatBritainLocalAct"),
        ANAW("anaw"),
        WELSH_NATIONAL_ASSEMBLY_ACT("WelshNationalAssemblyAct"),
        ASC("asc"),
        WELSH_PARLIAMENT_ACT("WelshParliamentAct"),
        MWA("mwa"),
        WELSH_ASSEMBLY_MEASURE("WelshAssemblyMeasure"),
        UKCM("ukcm"),
        UNITED_KINGDOM_CHURCH_MEASURE("UnitedKingdomChurchMeasure"),
        MNIA("mnia"),
        NORTHERN_IRELAND_ASSEMBLY_MEASURE("NorthernIrelandAssemblyMeasure"),
        APNI("apni"),
        NORTHERN_IRELAND_PARLIAMENT_ACT("NorthernIrelandParliamentAct"),
        UKSI("uksi"),
        UNITED_KINGDOM_STATUTORY_INSTRUMENT("UnitedKingdomStatutoryInstrument"),
        WSI("wsi"),
        WELSH_STATUTORY_INSTRUMENT("WelshStatutoryInstrument"),
        SSI("ssi"),
        SCOTTISH_STATUTORY_INSTRUMENT("ScottishStatutoryInstrument"),
        NISI("nisi"),
        NORTHERN_IRELAND_ORDER_IN_COUNCIL("NorthernIrelandOrderInCouncil"),
        UKMD("ukmd"),
        UNITED_KINGDOM_MINISTERIAL_DIRECTION("UnitedKingdomMinisterialDirection"),
        UKSRO("uksro"),
        UNITED_KINGDOM_STATUTORY_RULE_OR_ORDER("UnitedKingdomStatutoryRuleOrOrder"),
        UKDSI("ukdsi"),
        UNITED_KINGDOM_DRAFT_STATUTORY_INSTRUMENT("UnitedKingdomDraftStatutoryInstrument"),
        NISR("nisr"),
        NORTHERN_IRELAND_STATUTORY_RULE("NorthernIrelandStatutoryRule"),
        NISRO("nisro"),
        NORTHERN_IRELAND_STATUTORY_RULE_OR_ORDER("NorthernIrelandStatutoryRuleOrOrder"),
        NIDSR("nidsr"),
        NORTHERN_IRELAND_DRAFT_STATUTORY_RULE("NorthernIrelandDraftStatutoryRule"),
        SDSI("sdsi"),
        SCOTTISH_DRAFT_STATUTORY_INSTRUMENT("ScottishDraftStatutoryInstrument"),
        UKMO("ukmo"),
        UNITED_KINGDOM_MINISTERIAL_ORDER("UnitedKingdomMinisterialOrder"),
        UKCI("ukci"),
        UNITED_KINGDOM_CHURCH_INSTRUMENT("UnitedKingdomChurchInstrument");

        private final String type;

        TypeConstants(String type) {
            this.type = type;
        }

    public static TypeConstants fromType(String type) {
            for (TypeConstants constant : values()) {
                if (constant.type.equals(type)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("Unrecognized type: " + type);
        }
    }
}


