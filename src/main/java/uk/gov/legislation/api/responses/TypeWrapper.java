package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Type;

@Schema(name = "DocumentType")
public class TypeWrapper {

    private final Type type;

    public TypeWrapper(Type type) {
        this.type = type;
    }

    @JsonProperty(index = 1)
    @Schema(allowableValues = {
            "ukpga", "ukla", "ukppa", "uksi", "ukmd", "ukmo", "uksro", "ukdsi", "ukcm", "ukci",  // UK
            "asc", "wsi", "anaw", "mwa",  // Wales
            "asp", "aosp", "ssi", "sdsi",  // Scotland
            "nia", "aip", "nisr", "nisi", "mnia", "apni", "nisro", "nidsr",  // Northern Ireland
            "apgb", "gbla",  // Great Britain
            "aep"  // England
    })
    public String shortName() {
        return type.shortName();
    }

    @JsonProperty(index = 2)
    @Schema(allowableValues = {
            "UnitedKingdomPublicGeneralAct", "UnitedKingdomLocalAct", "UnitedKingdomPrivateOrPersonalAct",
            "UnitedKingdomStatutoryInstrument", "UnitedKingdomMinisterialDirection",
            "UnitedKingdomMinisterialOrder", "UnitedKingdomStatutoryRuleOrOrder", "UnitedKingdomDraftStatutoryInstrument",
            "UnitedKingdomChurchMeasure", "UnitedKingdomChurchInstrument",
            "WelshParliamentAct", "WelshStatutoryInstrument", "WelshNationalAssemblyAct", "WelshAssemblyMeasure",
            "ScottishAct", "ScottishOldAct", "ScottishStatutoryInstrument", "ScottishDraftStatutoryInstrument",
            "NorthernIrelandAct", "IrelandAct", "NorthernIrelandStatutoryRule", "NorthernIrelandOrderInCouncil",
            "NorthernIrelandAssemblyMeasure", "NorthernIrelandParliamentAct", "NorthernIrelandStatutoryRuleOrOrder",
            "NorthernIrelandDraftStatutoryRule",
            "GreatBritainAct", "GreatBritainLocalAct", "EnglandAct"
    })
    public String longName() {
        return type.longName();
    }

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "primary", "secondary" })
    public String category() {
        return type.category().name().toLowerCase();
    }

}
