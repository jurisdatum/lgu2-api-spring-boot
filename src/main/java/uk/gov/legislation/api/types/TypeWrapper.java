package uk.gov.legislation.api.types;

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
    @Schema(allowableValues = { "ukpga", "ukla", "asp", "nia", "aosp", "aep", "aip", "apgb", "anaw", "mwa", "ukcm",
            "mnia", "apni", "uksi", "ukmd", "ukmo", "uksro", "wsi", "ssi", "nisi", "nisr", "ukci", "nisro" })
    public String shortName() {
        return type.shortName();
    }

    @JsonProperty(index = 2)
    @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct", "UnitedKingdomLocalAct", "ScottishAct",
            "NorthernIrelandAct", "ScottishOldAct", "EnglandAct", "IrelandAct", "GreatBritainAct",
            "WelshNationalAssemblyAct", "WelshAssemblyMeasure", "UnitedKingdomChurchMeasure",
            "NorthernIrelandAssemblyMeasure", "NorthernIrelandParliamentAct", "UnitedKingdomStatutoryInstrument",
            "UnitedKingdomMinisterialDirection", "UnitedKingdomMinisterialOrder", "UnitedKingdomStatutoryRuleOrOrder",
            "WelshStatutoryInstrument", "ScottishStatutoryInstrument", "NorthernIrelandOrderInCouncil",
            "NorthernIrelandStatutoryRule", "UnitedKingdomChurchInstrument", "NorthernIrelandStatutoryRuleOrOrder" })
    public String longName() {
        return type.longName();
    }

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "primary", "secondary" })
    public String category() {
        return type.category().name().toLowerCase();
    }

}
